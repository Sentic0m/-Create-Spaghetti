package net.senti.createspaghetti;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.item.ModItems;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CreateSpaghetti.MOD_ID);

    public static final RegistryObject<CreativeModeTab> CREATE_SPAGHETTI_TAB = CREATIVE_MODE_TABS.register("create_spaghetti_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.SPAGHETTISAUCEMEATBALL.get()))
                    .title(Component.translatable("creativetab.create_spaghetti"))
                    .displayItems((pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.SPAGHETTI_KINETIC_BLOCK.get());
                        pOutput.accept(ModBlocks.MEATBALL_BLOCK.get());
                        pOutput.accept(ModItems.SPAGHETTISAUCEMEATBALL.get());
                        pOutput.accept(ModItems.SPAGHETTISAUCE.get());
                        pOutput.accept(ModItems.SPAGHETTIMEATBALLS.get());
                        pOutput.accept(ModItems.SPAGHETTI_PLATE.get());
                        pOutput.accept(ModItems.SPAGHETTI.get());
                        pOutput.accept(ModItems.PLATE.get());
                        pOutput.accept(ModItems.UNFINISHED_PLATE.get());
                        pOutput.accept(ModItems.SAUCE_BUCKET.get());
                        pOutput.accept(ModItems.RAW_MEATBALL.get());
                        pOutput.accept(ModItems.MEATBALL.get());
                        pOutput.accept(ModItems.TOMATO.get());
                        pOutput.accept(ModItems.SMASHED_TOMATO.get());
                        pOutput.accept(ModItems.SPAGHETTI_DOUGH.get());
                        pOutput.accept(ModItems.RAW_EGG.get());
                        pOutput.accept(ModItems.EGG_WHITE.get());
                        pOutput.accept(ModItems.EGG_YOLK.get());
                        pOutput.accept(ModItems.EGG_SHELL.get());

                        pOutput.accept(ModBlocks.TOMATO_CROP.get());


                    })
                    .build());


    public static void register(IEventBus eventBus) {CREATIVE_MODE_TABS.register(eventBus); }

}