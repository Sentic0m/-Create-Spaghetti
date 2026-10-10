package net.senti.createspaghetti.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

/**
 * Erzeugt vom Blockbench-Plugin "Block Animator" (wird bei jedem Export überschrieben).
 * Dreht das Modell nach FACING/HORIZONTAL_FACING des Blocks, falls vorhanden.
 */
public class TestAnimationRenderer<T extends BlockEntity & AnimatedBlockEntity> implements BlockEntityRenderer<T> {
    public TestAnimationRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(T be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        TestAnimation.render(ms, buffer, be.getAnimation(), be.getAnimationTicks(partialTicks),
                facing(be.getBlockState()), light, overlay);
    }

    private static Direction facing(BlockState state) {
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        if (state.hasProperty(BlockStateProperties.FACING) && state.getValue(BlockStateProperties.FACING).getAxis().isHorizontal()) {
            return state.getValue(BlockStateProperties.FACING);
        }
        return Direction.NORTH;
    }
}
