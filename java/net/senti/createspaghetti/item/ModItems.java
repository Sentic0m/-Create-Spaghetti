package net.senti.createspaghetti.item;

import net.minecraft.world.item.*;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.fluid.ModFluids;
import net.senti.createspaghetti.item.custom.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.item.custom.Spaghettisauce;
import net.senti.createspaghetti.item.custom.Spaghettisaucemeatball;
import net.senti.createspaghetti.item.custom.Spaghettimeatball;



public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "create_spaghetti");

    public static final RegistryObject<Item> SPAGHETTISAUCEMEATBALL = ITEMS.register("spaghetti_sauce_meatball",
            () -> new Spaghettisaucemeatball(new Item.Properties().food(ModFoods.SPAGHETTISAUCEMEATBALL)));

    public static final RegistryObject<Item> SPAGHETTIMEATBALLS = ITEMS.register("spaghetti_meatball",
            () -> new Spaghettimeatball(new Item.Properties().food(ModFoods.SPAGHETTIMEATBALLS)));

    public static final RegistryObject<Item> SPAGHETTISAUCE = ITEMS.register("spaghetti_sauce",
            () -> new Spaghettisauce(new Item.Properties().food(ModFoods.SPAGHETTISAUCE)));

    public static final RegistryObject<Item> SPAGHETTI_PLATE = ITEMS.register("spaghetti_plate",
            () -> new Spaghetti(new Item.Properties().food(ModFoods.SPAGHETTI_PLATE)));

    public static final RegistryObject<Item> SPAGHETTI = ITEMS.register("spaghetti",
            () -> new Spaghetti(new Item.Properties().food(ModFoods.SPAGHETTI)));

    public static final RegistryObject<Item> RAW_EGG = ITEMS.register("raw_egg",
            () -> new Item(new Item.Properties().food(ModFoods.RAW_EGG)));

    public static final RegistryObject<Item> EGG_SHELL = ITEMS.register("egg_shell",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPAGHETTI_DOUGH = ITEMS.register("spaghetti_dough",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> EGG_YOLK = ITEMS.register("egg_yolk",
            () -> new Item(new Item.Properties().food(ModFoods.SPAGHETTI)));

    public static final RegistryObject<Item> EGG_WHITE = ITEMS.register("egg_white",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> UNFINISHED_PLATE = ITEMS.register("unfinished_plate",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PLATE = ITEMS.register("plate",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> MEATBALL = ITEMS.register("meatball",
            () -> new Meatball(new Item.Properties().food(ModFoods.MEATBALL)));

    public static final RegistryObject<Item> RAW_MEATBALL = ITEMS.register("raw_meatball",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SAUCE_BUCKET = ITEMS.register("sauce_bucket",
            () -> new BucketItem(ModFluids.SAUCE_SOURCE, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> TOMATOSEEDS = ITEMS.register("tomato_seeds",
                    () -> new ItemNameBlockItem(ModBlocks.TOMATO_CROP.get(), new Item.Properties()));

    public static final RegistryObject<Item> TOMATO = ITEMS.register("tomato",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SMASHED_TOMATO = ITEMS.register("smashed_tomato",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> TOMATO_SKIN = ITEMS.register("tomato_skin",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SPAGHETTI_GRASS_ITEM = ITEMS.register("spaghetti_grass",
            () -> new BlockItem(ModBlocks.SPAGHETTI_GRASS.get(), new Item.Properties()));

    public static final RegistryObject<Item> INCOMPLETE_SPAGHETTI = ITEMS.register("incomplete_spaghetti",
            () -> new Item(new Item.Properties()));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
