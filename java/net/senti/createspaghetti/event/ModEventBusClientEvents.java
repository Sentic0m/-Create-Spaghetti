package net.senti.createspaghetti.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.senti.createspaghetti.Block.entity.renderer.SpaghettiPressRenderer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;
import net.senti.createspaghetti.Block.entity.renderer.ItemSplitterBlockEntityRenderer;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.client.animation.TestAnimation;
import net.senti.createspaghetti.client.animation.TestAnimationRenderer;

@Mod.EventBusSubscriber(modid = CreateSpaghetti.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ITEM_SPLITTER_BE.get(), ItemSplitterBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.SPAGHETTI_PRESS_BE.get(), SpaghettiPressRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.ANIMATED_TEST_BE.get(), TestAnimationRenderer::new);
    }

    @SubscribeEvent
    public static void registerModels(ModelEvent.RegisterAdditional event) {
        // Das Produkt ist kein Block, sein Modell muss extra geladen werden
        event.register(SpaghettiPressRenderer.PRODUCT_MODEL);
        // Knochen-Modelle aus dem Blockbench-Export
        TestAnimation.registerModels(event);
    }
}
