package net.senti.createspaghetti.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;
import net.senti.createspaghetti.Block.entity.renderer.ItemSplitterBlockEntityRenderer;
import net.senti.createspaghetti.CreateSpaghetti;

@Mod.EventBusSubscriber(modid = CreateSpaghetti.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModEventBusClientEvents {
    @SubscribeEvent
    public static void registerLayer(EntityRenderersEvent.RegisterLayerDefinitions event) {

    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.ITEM_SPLITTER_BE.get(), ItemSplitterBlockEntityRenderer::new);
    }
}
