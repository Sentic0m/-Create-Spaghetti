package net.senti.createspaghetti.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.senti.createspaghetti.CreateSpaghetti;

import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = CreateSpaghetti.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput output = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new ModBlockStateProvider(output, existingFileHelper));
        generator.addProvider(true, new ModItemModelProvider(output, existingFileHelper));
        generator.addProvider(true, new ModRecipeProvider(output));
    }
}

