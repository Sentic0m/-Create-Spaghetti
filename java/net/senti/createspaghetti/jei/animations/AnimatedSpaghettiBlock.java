package net.senti.createspaghetti.jei.animations;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.simibubi.create.compat.jei.category.animations.AnimatedKinetics;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.senti.createspaghetti.Block.ModBlocks;

public class AnimatedSpaghettiBlock extends AnimatedKinetics {

    @Override
    public void draw(GuiGraphics graphics, int xOffset, int yOffset) {
        PoseStack matrixStack = graphics.pose();
        matrixStack.pushPose();
        matrixStack.translate(xOffset, yOffset, 0);
        matrixStack.translate(0, 0, 200);
        matrixStack.translate(2, 22, 0);
        matrixStack.mulPose(Axis.XP.rotationDegrees(-15.5f));
        matrixStack.mulPose(Axis.YP.rotationDegrees(22.5f + 90));
        int scale = 25;

        blockElement(ModBlocks.SPAGHETTI_KINETIC_BLOCK.get().defaultBlockState())
                .rotateBlock(0, 180, 0)
                .scale(scale)
                .render(graphics);

        matrixStack.popPose();
    }
}