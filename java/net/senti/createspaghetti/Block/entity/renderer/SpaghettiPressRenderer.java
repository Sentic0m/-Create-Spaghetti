package net.senti.createspaghetti.Block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.senti.createspaghetti.Block.custom.SpaghettiPressBlock;
import net.senti.createspaghetti.Block.entity.SpaghettiPressBlockEntity;
import net.senti.createspaghetti.CreateSpaghetti;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpaghettiPressRenderer implements BlockEntityRenderer<SpaghettiPressBlockEntity> {
    public static final ResourceLocation PRODUCT_MODEL = new ResourceLocation(CreateSpaghetti.MOD_ID, "block/spaghetti_press_product");

    public SpaghettiPressRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SpaghettiPressBlockEntity be, float partialTicks, PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
        VertexConsumer consumer = buffer.getBuffer(RenderType.cutout());

        ms.pushPose();
        ms.translate(0, SpaghettiPressBlock.Y_OFFSET_PIXELS / 16f, 0);

        // Die Presse selbst (Drehung steckt schon im Blockstate-Modell, daher keine zusätzliche Drehung für die Schattierung)
        BakedModel pressModel = Minecraft.getInstance().getBlockRenderer().getBlockModel(be.getBlockState());
        renderShaded(ms, consumer, be.getLevel(), be.getBlockState(), pressModel, Direction.NORTH, light, overlay);

        if (be.hasProduct()) {
            Direction facing = be.getFacing();
            float distance = be.getProductDistance(partialTicks);
            // Mit dem Licht des Blocks zeichnen, über dem das Produkt gerade ist
            int productLight = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().relative(facing, Math.round(distance)));
            if (distance < SpaghettiPressBlockEntity.SHRINK_END) {
                renderProductBlock(be, distance, ms, consumer, productLight, overlay);
            } else {
                renderProductItem(be, distance, ms, buffer, productLight);
            }
        }
        ms.popPose();
    }

    // Der Spaghetti-Block fährt heraus und schrumpft ab SHRINK_START zu seiner Mitte hin weg
    private void renderProductBlock(SpaghettiPressBlockEntity be, float distance, PoseStack ms, VertexConsumer consumer,
                                    int light, int overlay) {
        Direction facing = be.getFacing();
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(PRODUCT_MODEL);
        float shrink = Mth.clamp((distance - SpaghettiPressBlockEntity.SHRINK_START)
                / (SpaghettiPressBlockEntity.SHRINK_END - SpaghettiPressBlockEntity.SHRINK_START), 0, 1);
        float scale = 1 - shrink * shrink;

        ms.pushPose();
        ms.translate(facing.getStepX() * distance, 0, facing.getStepZ() * distance);
        // Modell ist für Norden gebaut, wie die Presse selbst
        ms.translate(.5, 0, .5);
        ms.mulPose(Axis.YP.rotationDegrees(180 - facing.toYRot()));
        ms.translate(-.5, 0, -.5);
        // Mitte des Produkts im Modell (from [6,2,4] to [11,7,12])
        ms.translate(8.5 / 16, 4.5 / 16, 8 / 16f);
        ms.scale(scale, scale, scale);
        ms.translate(-8.5 / 16, -4.5 / 16, -8 / 16f);
        renderShaded(ms, consumer, be.getLevel(), null, model, facing, light, overlay);
        ms.popPose();
    }

    // Danach taucht das fertige Item auf: wächst, dreht sich, hüpft kurz und legt sich zum Schluss flach auf den Belt
    private void renderProductItem(SpaghettiPressBlockEntity be, float distance, PoseStack ms, MultiBufferSource buffer, int light) {
        Direction facing = be.getFacing();
        float t = Mth.clamp((distance - SpaghettiPressBlockEntity.SHRINK_END)
                / (SpaghettiPressBlockEntity.PRODUCT_TRAVEL - SpaghettiPressBlockEntity.SHRINK_END), 0, 1);
        float grow = easeOutBack(Math.min(t / .4f, 1));
        float spin = 720 * (1 - (1 - t) * (1 - t) * (1 - t));
        float hop = Mth.sin(t * Mth.PI) * 3 / 16f;
        float lie = Mth.clamp((t - .75f) / .25f, 0, 1);

        ms.pushPose();
        ms.translate(.5 + facing.getStepX() * distance, 2.5 / 16 + hop, .5 + facing.getStepZ() * distance);
        ms.mulPose(Axis.YP.rotationDegrees(180 - facing.toYRot() + spin));
        ms.mulPose(Axis.XP.rotationDegrees(90 * lie * lie * (3 - 2 * lie)));
        ms.scale(.5f * grow, .5f * grow, .5f * grow);
        Minecraft.getInstance().getItemRenderer().renderStatic(be.getProduct(), ItemDisplayContext.FIXED, light,
                OverlayTexture.NO_OVERLAY, ms, buffer, be.getLevel(), 0);
        ms.popPose();
    }

    private static float easeOutBack(float x) {
        float c = 1.7f;
        float u = x - 1;
        return 1 + (c + 1) * u * u * u + c * u * u;
    }

    // Wie der Chunk-Renderer: jede Fläche je nach Richtung abdunkeln (oben 100 %, N/S 80 %, O/W 60 %, unten 50 %).
    // modelFacing gibt an, wohin die Nordseite des Modells nach der Drehung im PoseStack zeigt.
    private void renderShaded(PoseStack ms, VertexConsumer consumer, Level level, @Nullable BlockState state,
                              BakedModel model, Direction modelFacing, int light, int overlay) {
        // Bewusst ohne Felder: nach einem Hotswap (hotreg) wären neue Felder im bestehenden Renderer null
        RandomSource random = RandomSource.create(42L);
        renderQuads(ms, consumer, level, model.getQuads(state, null, random, ModelData.EMPTY, RenderType.cutout()), modelFacing, light, overlay);
        for (Direction side : Direction.values()) {
            random.setSeed(42L);
            renderQuads(ms, consumer, level, model.getQuads(state, side, random, ModelData.EMPTY, RenderType.cutout()), modelFacing, light, overlay);
        }
    }

    private static void renderQuads(PoseStack ms, VertexConsumer consumer, Level level, List<BakedQuad> quads,
                                    Direction modelFacing, int light, int overlay) {
        for (BakedQuad quad : quads) {
            float shade = quad.isShade() ? level.getShade(toWorld(quad.getDirection(), modelFacing), true) : 1f;
            consumer.putBulkData(ms.last(), quad, shade, shade, shade, light, overlay);
        }
    }

    private static Direction toWorld(Direction dir, Direction modelFacing) {
        if (dir.getAxis().isVertical()) {
            return dir;
        }
        // Norden → modelFacing: so oft im Uhrzeigersinn drehen
        int steps = (modelFacing.get2DDataValue() + 2) % 4;
        for (int i = 0; i < steps; i++) {
            dir = dir.getClockWise();
        }
        return dir;
    }
}
