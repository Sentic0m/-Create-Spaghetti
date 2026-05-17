/*
package net.senti.createspaghetti.Block.entity.not_in_use;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;

import java.util.List;

public class NoodlePressBlock extends KineticBlock implements IBE<NoodlePressBlockEntity> {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final VoxelShape SHAPE_NS = Shapes.or(
            box(0, 0, 0, 16, 4, 16),
            box(0, 12, 0, 16, 16, 16),
            box(0, 4, 0, 4, 12, 16),
            box(12, 4, 0, 16, 12, 16)
    );

    private static final VoxelShape SHAPE_EW = Shapes.or(
            box(0, 0, 0, 16, 4, 16),
            box(0, 12, 0, 16, 16, 16),
            box(0, 4, 0, 16, 12, 4),
            box(0, 4, 12, 16, 12, 16)
    );

    public NoodlePressBlock(Properties properties) {
        super(properties.noOcclusion());
        registerDefaultState(defaultBlockState().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.NORTH || facing == Direction.SOUTH) return SHAPE_NS;
        return SHAPE_EW;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        Direction facing = state.getValue(FACING);
        if (facing == Direction.NORTH || facing == Direction.SOUTH) return Direction.Axis.Z;
        return Direction.Axis.X;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == Direction.UP || face == Direction.DOWN;
    }

    @Override
    public boolean showCapacityWithAnnotation() {
        return true;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        return List.of(new ItemStack(ModBlocks.NOODLE_PRESS.get().asItem()));
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        return InteractionResult.PASS;
    }

    @Override
    public Class<NoodlePressBlockEntity> getBlockEntityClass() {
        return NoodlePressBlockEntity.class;
    }

    @Override
    public BlockEntityType<? extends NoodlePressBlockEntity> getBlockEntityType() {
        return ModBlockEntities.NOODLE_PRESS_BE.get();
    }
}
 */