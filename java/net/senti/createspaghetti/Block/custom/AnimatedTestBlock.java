package net.senti.createspaghetti.Block.custom;

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
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.senti.createspaghetti.Block.entity.AnimatedTestBlockEntity;
import net.senti.createspaghetti.client.animation.TestAnimation;
import org.jetbrains.annotations.Nullable;

// Testblock für das Blockbench-Plugin "Block Animator": Rechtsklick spielt die Animation ab
public class AnimatedTestBlock extends HorizontalDirectionalBlock implements EntityBlock {
    // Entspricht dem großen Element im Modell (from [4,0,0] to [13,9,16]), für jede Richtung gedreht
    private static final VoxelShape SHAPE_NORTH = Block.box(4, 0, 0, 13, 9, 16);
    private static final VoxelShape SHAPE_EAST = Block.box(0, 0, 4, 16, 9, 13);
    private static final VoxelShape SHAPE_SOUTH = Block.box(3, 0, 0, 12, 9, 16);
    private static final VoxelShape SHAPE_WEST = Block.box(0, 0, 3, 16, 9, 12);

    public AnimatedTestBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection());
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
        // Gezeichnet wird nur vom TestAnimationRenderer
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof AnimatedTestBlockEntity be) {
            // TestAnimation.PRESS ist eine Konstante und wird beim Kompilieren eingesetzt,
            // die Client-Klasse wird auf dem Server also nicht geladen
            be.playAnimation(TestAnimation.PRESS);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
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
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new AnimatedTestBlockEntity(pos, state);
    }
}
