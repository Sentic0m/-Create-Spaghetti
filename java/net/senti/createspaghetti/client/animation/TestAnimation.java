package net.senti.createspaghetti.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import org.jetbrains.annotations.Nullable;

/**
 * Erzeugt vom Blockbench-Plugin "Block Animator" aus "test_animated" (wird bei jedem Export überschrieben).
 *
 * Einbinden:
 * 1. Client-Event auf dem Mod-Bus: bei ModelEvent.RegisterAdditional {@link #registerModels} aufrufen.
 * 2. Block: getRenderShape -> RenderShape.ENTITYBLOCK_ANIMATED, damit der Chunk das Modell nicht zusätzlich zeichnet.
 * 3. Block-Entity implementiert AnimatedBlockEntity, Renderer registrieren:
 *    event.registerBlockEntityRenderer(MEIN_BE.get(), TestAnimationRenderer::new);
 */
public final class TestAnimation {
    public static final ResourceLocation ID = new ResourceLocation("create_spaghetti", "test");

    // Animationen
    public static final String PRESS = "press";

    private static final ResourceLocation[] MODELS = {
            new ResourceLocation("create_spaghetti:block/test/gehaeuse"),
            new ResourceLocation("create_spaghetti:block/test/produkt")
    };

    private TestAnimation() {
    }

    public static void registerModels(ModelEvent.RegisterAdditional event) {
        AnimatedBlockModel.clearCache();
        for (ResourceLocation model : MODELS) {
            event.register(model);
        }
    }

    public static AnimatedBlockModel model() {
        return AnimatedBlockModel.get(ID);
    }

    /** Zeichnet das Modell; {@code ticks} = Zeit seit Animationsstart inklusive partialTicks. */
    public static void render(PoseStack ms, MultiBufferSource buffer, @Nullable String animation, float ticks,
                              Direction facing, int light, int overlay) {
        model().render(ms, buffer.getBuffer(RenderType.cutout()), animation, ticks, facing, light, overlay);
    }
}
