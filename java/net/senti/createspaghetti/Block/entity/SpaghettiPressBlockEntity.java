package net.senti.createspaghetti.Block.entity;

import com.simibubi.create.content.kinetics.belt.BeltBlockEntity;
import com.simibubi.create.content.kinetics.belt.behaviour.DirectBeltInputBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour.TransportedResult;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.senti.createspaghetti.Block.custom.SpaghettiPressBlock;
import net.senti.createspaghetti.recipe.ModRecipeTypes;
import net.senti.createspaghetti.recipe.NoodlePressRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class SpaghettiPressBlockEntity extends BlockEntity {
    // Abschnitte der Strecke, die das Produkt im Belt-Tempo zurücklegt (in Blöcken ab Pressenmitte):
    // bis SHRINK_START fährt der Spaghetti-Block, bis SHRINK_END schrumpft er nach innen weg,
    // danach taucht das Item drehend auf und wird bei PRODUCT_TRAVEL (Mitte des übernächsten Blocks) echt
    public static final float SHRINK_START = 1f;
    public static final float SHRINK_END = 1.3f;
    public static final float PRODUCT_TRAVEL = 2f;
    // Steht ein Inventar direkt vor dem Ausgang, fährt das Produkt nur bis in dessen Seite
    public static final float DIRECT_OUTPUT = .5f;
    // Geschwindigkeit ohne Belt darunter: entspricht einem Belt mit 16 RPM
    private static final float SPEED_WITHOUT_BELT = 16 / 480f;

    private final ItemStackHandler input = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return level == null || findRecipe(stack).isPresent();
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> inputCap = LazyOptional.of(() -> input);

    private float progress;
    // Das fertige Ergebnis, das gerade als spaghetti_press_product-Modell herausfährt
    private ItemStack product = ItemStack.EMPTY;
    private float productDistance;
    private float prevProductDistance;

    public SpaghettiPressBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SPAGHETTI_PRESS_BE.get(), pos, state);
    }

    public void tick() {
        prevProductDistance = productDistance;

        if (!product.isEmpty()) {
            // Steht direkt am Ausgang ein Inventar (Kiste, Trichter, Chute, Depot …), geht das Produkt
            // wie bei einem Trichter nur bis in dessen Seite hinein
            boolean direct = hasInventory(worldPosition.relative(getFacing()), getFacing());
            float end = direct ? DIRECT_OUTPUT : PRODUCT_TRAVEL;
            productDistance = Math.min(end, productDistance + getProductSpeed());
            if (!level.isClientSide && productDistance >= end) {
                if (direct) {
                    product = insertInto(worldPosition.relative(getFacing()), getFacing(), product);
                    finishProductIfEmpty();
                } else {
                    releaseProduct();
                }
            }
        }

        if (level.isClientSide) {
            return;
        }

        collectInputs();

        Optional<NoodlePressRecipe> recipe = findRecipe(input.getStackInSlot(0));
        if (recipe.isEmpty()) {
            progress = 0;
            return;
        }

        if (progress < recipe.get().getProcessingTime()) {
            progress += getProcessingSpeed();
            setChanged();
        }
        // Fertig, aber das vorige Produkt ist noch unterwegs: warten
        if (progress >= recipe.get().getProcessingTime() && product.isEmpty()) {
            product = recipe.get().getOutput();
            input.extractItem(0, 1, false);
            progress = 0;
            productDistance = prevProductDistance = 0;
            sync();
        }
    }

    // Später: aus den Stress Units / der Drehzahl berechnen. 1 = Rezeptzeit in Ticks.
    protected float getProcessingSpeed() {
        return 1f;
    }

    // Das Produkt fährt so schnell wie der Belt unter der Presse (bzw. unter seiner Strecke davor)
    private float getProductSpeed() {
        for (int i = 0; i <= PRODUCT_TRAVEL; i++) {
            BeltBlockEntity belt = getBelt(worldPosition.relative(getFacing(), i).below());
            if (belt != null) {
                return Math.abs(belt.getBeltMovementSpeed());
            }
        }
        return SPEED_WITHOUT_BELT;
    }

    @Nullable
    private BeltBlockEntity getBelt(BlockPos pos) {
        return level.getBlockEntity(pos) instanceof BeltBlockEntity belt ? belt : null;
    }

    private void releaseProduct() {
        BlockPos outPos = worldPosition.relative(getFacing(), (int) PRODUCT_TRAVEL);

        // Wie ein Trichter/Chute: zuerst in das, was darunter steht (Belt, Depot, Chute, Trichter, Kiste),
        // dann in das, was direkt davor steht, sonst als Item auf den Boden
        if (hasInventory(outPos.below(), Direction.DOWN)) {
            product = insertInto(outPos.below(), Direction.DOWN, product);
        } else if (hasInventory(outPos, getFacing())) {
            product = insertInto(outPos, getFacing(), product);
        } else if (level.getBlockState(outPos).getCollisionShape(level, outPos).isEmpty()) {
            // Das Item liegt da schon flach am Boden (siehe SpaghettiPressRenderer.renderProductItem)
            Vec3 at = Vec3.atBottomCenterOf(outPos).add(0, .05, 0);
            ItemEntity item = new ItemEntity(level, at.x, at.y, at.z, product);
            item.setDeltaMovement(Vec3.ZERO);
            level.addFreshEntity(item);
            product = ItemStack.EMPTY;
        }
        // Belt belegt, Kiste voll oder Ausgang zugebaut: Produkt bleibt stehen, nächsten Tick neuer Versuch
        finishProductIfEmpty();
    }

    private void finishProductIfEmpty() {
        if (product.isEmpty()) {
            productDistance = prevProductDistance = 0;
            sync();
        }
    }

    // Create-Blöcke mit Belt-Eingang (Belt, Depot, Chute …) oder alles mit Item-Inventar (Kiste, Trichter, Fass …)
    private boolean hasInventory(BlockPos target, Direction movement) {
        if (BlockEntityBehaviour.get(level, target, DirectBeltInputBehaviour.TYPE) != null) {
            return true;
        }
        BlockEntity be = level.getBlockEntity(target);
        return be != null && be.getCapability(ForgeCapabilities.ITEM_HANDLER, movement.getOpposite()).isPresent();
    }

    // Gibt zurück, was nicht hineinpasst
    private ItemStack insertInto(BlockPos target, Direction movement, ItemStack stack) {
        DirectBeltInputBehaviour beltInput = BlockEntityBehaviour.get(level, target, DirectBeltInputBehaviour.TYPE);
        if (beltInput != null) {
            return beltInput.canInsertFromSide(movement) ? beltInput.handleInsertion(stack, movement, false) : stack;
        }
        BlockEntity be = level.getBlockEntity(target);
        if (be == null) {
            return stack;
        }
        return be.getCapability(ForgeCapabilities.ITEM_HANDLER, movement.getOpposite())
                .map(handler -> ItemHandlerHelper.insertItemStacked(handler, stack.copy(), false))
                .orElse(stack);
    }

    // Wie Create-Maschinen über einem Belt und wie ein Trichter: passende Items vom Belt darunter
    // und hingeworfene Items auf der Presse landen im eigenen Inventar
    private void collectInputs() {
        TransportedItemStackHandlerBehaviour belt = BlockEntityBehaviour.get(level, worldPosition.below(), TransportedItemStackHandlerBehaviour.TYPE);
        if (belt != null) {
            belt.handleCenteredProcessingOnAllItems(.25f, transported -> {
                ItemStack rest = input.insertItem(0, transported.stack, false);
                if (rest.getCount() == transported.stack.getCount()) {
                    return TransportedResult.doNothing();
                }
                if (rest.isEmpty()) {
                    return TransportedResult.removeItem();
                }
                TransportedItemStack left = transported.copy();
                left.stack = rest;
                return TransportedResult.convertTo(left);
            });
        }

        if (level.getGameTime() % 4 != 0) {
            return;
        }
        AABB area = new AABB(worldPosition).expandTowards(0, .5, 0);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, area, ItemEntity::isAlive)) {
            ItemStack rest = input.insertItem(0, item.getItem(), false);
            if (rest.isEmpty()) {
                item.discard();
            } else {
                item.setItem(rest);
            }
        }
    }

    public Optional<NoodlePressRecipe> findRecipe(ItemStack stack) {
        if (stack.isEmpty()) {
            return Optional.empty();
        }
        return level.getRecipeManager().getRecipeFor(ModRecipeTypes.NOODLE_PRESS_TYPE.get(), new SimpleContainer(stack), level);
    }

    public Direction getFacing() {
        return getBlockState().getValue(SpaghettiPressBlock.FACING);
    }

    public boolean hasProduct() {
        return !product.isEmpty();
    }

    public ItemStack getProduct() {
        return product;
    }

    public float getProductDistance(float partialTicks) {
        return Mth.lerp(partialTicks, prevProductDistance, productDistance);
    }

    public ItemStackHandler getInput() {
        return input;
    }

    public void drops() {
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), input.getStackInSlot(0));
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), product);
    }

    private void sync() {
        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inputCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inputCap.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("Input", input.serializeNBT());
        tag.putFloat("Progress", progress);
        tag.put("Product", product.save(new CompoundTag()));
        tag.putFloat("ProductDistance", productDistance);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        input.deserializeNBT(tag.getCompound("Input"));
        progress = tag.getFloat("Progress");
        product = ItemStack.of(tag.getCompound("Product"));
        productDistance = prevProductDistance = tag.getFloat("ProductDistance");
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

    @Override
    public AABB getRenderBoundingBox() {
        // Das Produkt fährt bis zwei Blöcke vor die Presse
        return new AABB(worldPosition).inflate(PRODUCT_TRAVEL);
    }
}
