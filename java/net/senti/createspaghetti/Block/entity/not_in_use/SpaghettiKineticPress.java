//I had no other idea to name this class but it matches the purpose

package net.senti.createspaghetti.Block.entity.not_in_use;

import com.simibubi.create.content.fluids.pipes.FluidPipeBlock;
import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;
import net.senti.createspaghetti.Block.entity.SpaghettiKineticBlockEntity;

import java.util.List;

public class SpaghettiKineticPress extends KineticBlock implements IBE<SpaghettiKineticBlockEntity> {

    public static final BooleanProperty WEST  = BooleanProperty.create("west");
    public static final BooleanProperty NORTH = BooleanProperty.create("north");

    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 10, 16);

    public SpaghettiKineticPress(Properties properties) {
        super(properties.noOcclusion());
        registerDefaultState(defaultBlockState()
                .setValue(WEST, false)
                .setValue(NORTH, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(WEST, NORTH);
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        return defaultBlockState()
                .setValue(WEST,  isPipe(level, pos, Direction.WEST))
                .setValue(NORTH, isPipe(level, pos, Direction.NORTH));
    }

    private boolean isPipe(LevelReader level, BlockPos pos, Direction direction) {
        BlockState neighbor = level.getBlockState(pos.relative(direction));

        if (!(neighbor.getBlock() instanceof FluidPipeBlock))
            return false;

        Direction pipeOpeningFacing = direction.getOpposite();
        String faceName = pipeOpeningFacing.getName();

        // Alle Properties der Pipe durchsuchen
        for (net.minecraft.world.level.block.state.properties.Property<?> property : neighbor.getProperties()) {
            if (property.getName().equalsIgnoreCase(faceName)) {
                String value = neighbor.getValue(property).toString().toLowerCase();
                return !value.equals("none");
            }
        }
        return true;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.WEST)
            return state.setValue(WEST,  isPipe(level, pos, Direction.WEST));
        if (direction == Direction.NORTH)
            return state.setValue(NORTH, isPipe(level, pos, Direction.NORTH));
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean showCapacityWithAnnotation() { return true; }

    @Override
    public Class<SpaghettiKineticBlockEntity> getBlockEntityClass() {
        return SpaghettiKineticBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends SpaghettiKineticBlockEntity> getBlockEntityType() {
        return ModBlockEntities.SPAGHETTI_KINETIC_BE.get();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.DOWN;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return Direction.Axis.Y;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(ModBlocks.SPAGHETTI_KINETIC_BLOCK.get().asItem()));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }
}