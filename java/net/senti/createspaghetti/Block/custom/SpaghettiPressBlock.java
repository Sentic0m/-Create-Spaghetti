package net.senti.createspaghetti.Block.custom;

import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;
import net.senti.createspaghetti.Block.entity.SpaghettiPressBlockEntity;
import org.jetbrains.annotations.Nullable;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpaghettiPressBlock extends HorizontalDirectionalBlock implements EntityBlock, IWrenchable {
    public static final int Y_OFFSET_PIXELS = -3;

    // Entspricht dem Element im Modell (from [4,0,0] to [13,9,16]), für jede Richtung gedreht und nach unten versetzt
    private static final VoxelShape SHAPE_NORTH = Block.box(4, Y_OFFSET_PIXELS, 0, 13, 9 + Y_OFFSET_PIXELS, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(0, Y_OFFSET_PIXELS, 4, 16, 9 + Y_OFFSET_PIXELS, 13);
    private static final VoxelShape SHAPE_SOUTH = Block.box(3, Y_OFFSET_PIXELS, 0, 12, 9 + Y_OFFSET_PIXELS, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, Y_OFFSET_PIXELS, 3, 16, 9 + Y_OFFSET_PIXELS, 12);

    public SpaghettiPressBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            facing = facing.getOpposite();
        }
        return defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public BlockState getRotatedBlockState(BlockState originalState, Direction targetedFace) {
        return originalState.setValue(FACING, originalState.getValue(FACING).getClockWise());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST -> SHAPE_EAST;
            case SOUTH -> SHAPE_SOUTH;
            case WEST -> SHAPE_WEST;
            default -> SHAPE_NORTH;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!(level.getBlockEntity(pos) instanceof SpaghettiPressBlockEntity press) || !player.getItemInHand(hand).isEmpty()
                || press.getInput().getStackInSlot(0).isEmpty()) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            player.setItemInHand(hand, press.getInput().extractItem(0, 64, false));
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof SpaghettiPressBlockEntity press) {
            press.drops();
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpaghettiPressBlockEntity(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.SPAGHETTI_PRESS_BE.get()
                ? (l, p, s, be) -> ((SpaghettiPressBlockEntity) be).tick()
                : null;
    }
}
