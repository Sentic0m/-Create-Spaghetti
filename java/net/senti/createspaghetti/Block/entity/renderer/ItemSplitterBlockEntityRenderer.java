package net.senti.createspaghetti.Block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LightLayer;
import net.senti.createspaghetti.Block.entity.ItemSplitterEntity;


import net.minecraft.world.level.Level;

public class ItemSplitterBlockEntityRenderer implements BlockEntityRenderer<ItemSplitterEntity> {
    public ItemSplitterBlockEntityRenderer(BlockEntityRendererProvider.Context context) {

    }

    @Override
    public void render(ItemSplitterEntity p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {

        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack itemStack = p_112307_.getRenderStack();

        p_112309_.pushPose();
        p_112309_.translate(0.5f, 0.9f, 0.5f);
        p_112309_.scale(0.35f, 0.35f, 0.35f);
        p_112309_.mulPose(Axis.XP.rotationDegrees(270));

        itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, getLightLevel(p_112307_.getLevel(), p_112307_.getBlockPos()),
                OverlayTexture.NO_OVERLAY, p_112309_, p_112310_, p_112307_.getLevel(), 1);
        p_112309_.popPose();

    }


    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }


}
