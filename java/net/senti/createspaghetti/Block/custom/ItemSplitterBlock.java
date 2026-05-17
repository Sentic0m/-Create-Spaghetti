package net.senti.createspaghetti.Block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.senti.createspaghetti.Block.entity.ItemSplitterEntity;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class ItemSplitterBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    public ItemSplitterBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.MODEL;
    }

    @Override
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        if (p_60515_.getBlock() != p_60518_.getBlock()) {
            BlockEntity blockEntity = p_60516_.getBlockEntity(p_60517_);
            if (blockEntity instanceof ItemSplitterEntity) {
                ((ItemSplitterEntity) blockEntity).drops();
            }
        }

        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
    }

    @Override
    public InteractionResult use(BlockState p_60503_, Level p_60504_, BlockPos p_60505_, Player p_60506_, InteractionHand p_60507_, BlockHitResult p_60508_) {
        if (!p_60504_.isClientSide()) {
            BlockEntity entity = p_60504_.getBlockEntity(p_60505_);
            if (entity instanceof ItemSplitterEntity) {
                NetworkHooks.openScreen(((ServerPlayer) p_60506_), (ItemSplitterEntity) entity, p_60505_);
            } else {
                throw new IllegalStateException("Bro der Container provider ist nicht da");
            }
        }
        return InteractionResult.sidedSuccess(p_60504_.isClientSide());
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ItemSplitterEntity(p_153215_,p_153216_);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        if (p_153212_.isClientSide) {
            return null;
        }

        return createTickerHelper(p_153214_, ModBlockEntities.ITEM_SPLITTER_BE.get(),
                (p_155253_, p_155254_, p_155255_, p_155256_) -> p_155256_.tick(p_155253_, p_155254_, p_155255_));
    }
}
