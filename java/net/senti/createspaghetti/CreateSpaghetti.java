package net.senti.createspaghetti;

import com.mojang.logging.LogUtils;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;
import net.senti.createspaghetti.fluid.ModFluidTypes;
import net.senti.createspaghetti.fluid.ModFluids;
import net.senti.createspaghetti.item.ModItems;
import net.senti.createspaghetti.recipe.ModRecipeTypes;
import net.senti.createspaghetti.screen.ItemSplitterScreen;
import net.senti.createspaghetti.screen.ModMenuTypes;
import org.slf4j.Logger;
import com.simibubi.create.api.stress.BlockStressValues;

@Mod(CreateSpaghetti.MOD_ID)
public class CreateSpaghetti {
    public static final String MOD_ID = "create_spaghetti";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CreateSpaghetti() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModRecipeTypes.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            BlockStressValues.IMPACTS.register(ModBlocks.SPAGHETTI_KINETIC_BLOCK.get(), () -> 4.0);
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        }
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            MenuScreens.register(ModMenuTypes.ITEM_SPLITTER_MENU.get(), ItemSplitterScreen::new);
        }
    }
}