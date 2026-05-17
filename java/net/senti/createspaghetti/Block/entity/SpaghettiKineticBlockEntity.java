package net.senti.createspaghetti.Block.entity;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.SimpleContainer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.senti.createspaghetti.recipe.ModRecipeTypes;
import net.senti.createspaghetti.recipe.SplittingOutput;
import net.senti.createspaghetti.recipe.SplittingRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class SpaghettiKineticBlockEntity extends KineticBlockEntity {

    private int processingTicks = 1;
    private static final int BASE_PROCESSING_TIME = 100;
    private static final int FLUID_TANK_CAPACITY   = 8000;

    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) { setChanged(); }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0;
        }
    };

    private final LazyOptional<IItemHandler> itemHandler = LazyOptional.of(() -> inventory);

    // FIX: southFluidTank → SOUTH-Seite, eastFluidTank → EAST-Seite.
    // Create-Pipes verbinden sich mit diesen Seiten und pullen den Fluid selbst
    // über die Capability. Kein aktives Pushen nötig (und war sogar problematisch).
    private final FluidTank southFluidTank = new FluidTank(FLUID_TANK_CAPACITY);
    private final FluidTank eastFluidTank  = new FluidTank(FLUID_TANK_CAPACITY);

    private final LazyOptional<IFluidHandler> southFluidHandler = LazyOptional.of(() -> southFluidTank);
    private final LazyOptional<IFluidHandler> eastFluidHandler  = LazyOptional.of(() -> eastFluidTank);

    public SpaghettiKineticBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        collectItemsFromTop();

        // FIX: Das aktive Pushen zu Create-Pipes entfernt.
        // Create-Pipes holen sich den Fluid selbst via getCapability(FLUID_HANDLER, SOUTH/EAST).
        // Für normale (Nicht-Pipe) Fluid-Handler können wir aber weiterhin pushen:
        outputFluidToNonPipeNeighbor(southFluidTank, Direction.SOUTH);
        outputFluidToNonPipeNeighbor(eastFluidTank,  Direction.EAST);

        float speed = Math.abs(getSpeed());
        if (speed == 0) return;

        if (inventory.getStackInSlot(0).isEmpty()) return;

        if (!outputSlotsEmpty()) return;

        int stackSize    = inventory.getStackInSlot(0).getCount();
        int requiredTicks = (int) Math.max(1, BASE_PROCESSING_TIME / speed * 16 * stackSize);

        processingTicks++;

        if (processingTicks >= requiredTicks) {
            processingTicks = 0;
            onProcessingComplete();
        }
    }

    private boolean outputSlotsEmpty() {
        return inventory.getStackInSlot(1).isEmpty()
                && inventory.getStackInSlot(2).isEmpty()
                && southFluidTank.isEmpty()
                && eastFluidTank.isEmpty();
    }

    private void collectItemsFromTop() {
        AABB area = new AABB(
                worldPosition.getX() - 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() - 0.5,
                worldPosition.getX() + 1.5, worldPosition.getY() + 2.5, worldPosition.getZ() + 1.5
        );

        List<ItemEntity> items = level.getEntitiesOfClass(ItemEntity.class, area);
        for (ItemEntity itemEntity : items) {
            if (itemEntity.hasPickUpDelay()) continue;

            ItemStack stack = itemEntity.getItem();

            boolean hasRecipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipeTypes.SPLITTING_TYPE.get(), new SimpleContainer(stack), level)
                    .isPresent();

            if (!hasRecipe) continue;

            ItemStack remainder = inventory.insertItem(0, stack, false);
            itemEntity.setItem(remainder);
            if (remainder.isEmpty()) itemEntity.discard();
        }
    }

    protected void onProcessingComplete() {
        if (level == null) return;

        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return;

        Optional<SplittingRecipe> optional = level.getRecipeManager()
                .getRecipeFor(ModRecipeTypes.SPLITTING_TYPE.get(), new SimpleContainer(input), level);
        if (optional.isEmpty()) return;

        SplittingRecipe recipe  = optional.get();
        SplittingOutput output1 = recipe.getOutput1();
        SplittingOutput output2 = recipe.getOutput2();

        int toProcess = computeProcessable(input, output1, output2);
        if (toProcess <= 0) return;

        applyOutput(output1, toProcess, 1, southFluidTank);
        applyOutput(output2, toProcess, 2, eastFluidTank);

        input.shrink(toProcess);
        inventory.setStackInSlot(0, input.isEmpty() ? ItemStack.EMPTY : input);

        outputItemToDirection(1, Direction.SOUTH);
        outputItemToDirection(2, Direction.EAST);

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private int computeProcessable(ItemStack input, SplittingOutput out1, SplittingOutput out2) {
        int max = input.getCount();

        if (out1.isItem()) {
            int perItem = out1.getItem().getCount();
            max = Math.min(max, 64 / Math.max(1, perItem));
        } else {
            int perItem   = out1.getFluid().getAmount();
            int freeSpace = southFluidTank.getCapacity() - southFluidTank.getFluidAmount();
            max = Math.min(max, freeSpace / Math.max(1, perItem));
        }

        if (out2.isItem()) {
            int perItem = out2.getItem().getCount();
            max = Math.min(max, 64 / Math.max(1, perItem));
        } else {
            int perItem   = out2.getFluid().getAmount();
            int freeSpace = eastFluidTank.getCapacity() - eastFluidTank.getFluidAmount();
            max = Math.min(max, freeSpace / Math.max(1, perItem));
        }

        return max;
    }

    private void applyOutput(SplittingOutput output, int toProcess, int itemSlot, FluidTank fluidTank) {
        if (output.isItem()) {
            ItemStack result = output.getItem();
            result.setCount(result.getCount() * toProcess);
            inventory.setStackInSlot(itemSlot, result);
        } else {
            FluidStack result = output.getFluid();
            result.setAmount(result.getAmount() * toProcess);
            fluidTank.fill(result, IFluidHandler.FluidAction.EXECUTE);
        }
    }

    private void outputItemToDirection(int slot, Direction direction) {
        ItemStack stack = inventory.getStackInSlot(slot);
        if (stack.isEmpty()) return;

        BlockPos neighborPos = worldPosition.relative(direction);

        DirectBeltInputBehaviour behaviour = BlockEntityBehaviour.get(
                level, neighborPos, DirectBeltInputBehaviour.TYPE
        );

        if (behaviour != null) {
            ItemStack remainder = behaviour.handleInsertion(stack.copy(), direction, true);
            if (remainder.isEmpty()) {
                behaviour.handleInsertion(stack.copy(), direction, false);
                inventory.setStackInSlot(slot, ItemStack.EMPTY);
                setChanged();
                return;
            }
        }

        double x = worldPosition.getX() + 0.5 + direction.getStepX() * 0.7;
        double y = worldPosition.getY() + 0.5;
        double z = worldPosition.getZ() + 0.5 + direction.getStepZ() * 0.7;

        ItemEntity itemEntity = new ItemEntity(level, x, y, z, stack.copy());
        itemEntity.setDeltaMovement(direction.getStepX() * 0.1, 0.1, direction.getStepZ() * 0.1);
        level.addFreshEntity(itemEntity);
        inventory.setStackInSlot(slot, ItemStack.EMPTY);
        setChanged();
    }

    /**
     * FIX: Pusht Fluid nur zu Nicht-Pipe-Nachbarn (z.B. normale Tanks, Kessel usw.).
     * Create-Pipes pullen sich den Fluid selbst über getCapability → kein Push nötig.
     * Würde man auch in Pipes pushen, entsteht ein Doppelzug und Fluid verschwindet.
     */
    private void outputFluidToNonPipeNeighbor(FluidTank tank, Direction direction) {
        if (tank.isEmpty()) return;

        BlockPos neighborPos = worldPosition.relative(direction);
        var neighborBE = level.getBlockEntity(neighborPos);
        if (neighborBE == null) return;

        // Create-FluidPipeBlock überspringen – die pullen selbst
        if (neighborBE.getBlockState().getBlock() instanceof
                com.simibubi.create.content.fluids.pipes.FluidPipeBlock) return;

        neighborBE.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite())
                .ifPresent(handler -> {
                    FluidStack toSend = tank.getFluid().copy();
                    int filled = handler.fill(toSend, IFluidHandler.FluidAction.SIMULATE);
                    if (filled <= 0) return;

                    toSend.setAmount(filled);
                    handler.fill(toSend, IFluidHandler.FluidAction.EXECUTE);
                    tank.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    setChanged();
                });
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && side == Direction.UP)
            return itemHandler.cast();

        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            if (side == Direction.NORTH) return southFluidHandler.cast();
            if (side == Direction.WEST)  return eastFluidHandler.cast();
        }

        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandler.invalidate();
        southFluidHandler.invalidate();
        eastFluidHandler.invalidate();
    }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        processingTicks = tag.getInt("ProcessingTicks");
        inventory.deserializeNBT(tag.getCompound("Inventory"));
        southFluidTank.readFromNBT(tag.getCompound("SouthTank"));
        eastFluidTank.readFromNBT(tag.getCompound("EastTank"));
    }

    @Override
    public void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("ProcessingTicks", processingTicks);
        tag.put("Inventory", inventory.serializeNBT());
        tag.put("SouthTank", southFluidTank.writeToNBT(new CompoundTag()));
        tag.put("EastTank",  eastFluidTank.writeToNBT(new CompoundTag()));
    }

    public float getProgress() {
        float speed = Math.abs(getSpeed());
        if (speed == 0) return 0;
        int requiredTicks = (int) Math.max(1, BASE_PROCESSING_TIME / speed * 16);
        return (float) processingTicks / requiredTicks;
    }

    public ItemStackHandler getInventory() { return inventory; }
    public FluidTank getSouthFluidTank()   { return southFluidTank; }
    public FluidTank getEastFluidTank()    { return eastFluidTank; }
}