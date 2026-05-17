package net.senti.createspaghetti.recipe;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.CreateSpaghetti;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateSpaghetti.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, CreateSpaghetti.MOD_ID);

    public static final RegistryObject<RecipeType<SplittingRecipe>> SPLITTING_TYPE =
            RECIPE_TYPES.register("splitting", () -> RecipeType.simple(
                    new net.minecraft.resources.ResourceLocation(CreateSpaghetti.MOD_ID, "splitting")
            ));

    public static final RegistryObject<RecipeSerializer<SplittingRecipe>> SPLITTING_SERIALIZER =
            SERIALIZERS.register("splitting", SplittingRecipe.Serializer::new);

    public static final RegistryObject<RecipeType<NoodlePressRecipe>> NOODLE_PRESS_TYPE =
            RECIPE_TYPES.register("noodle_press", () -> RecipeType.simple(
                    new net.minecraft.resources.ResourceLocation(CreateSpaghetti.MOD_ID, "noodle_press")
            ));

    public static final RegistryObject<RecipeSerializer<NoodlePressRecipe>> NOODLE_PRESS_SERIALIZER =
            SERIALIZERS.register("noodle_press", NoodlePressRecipeSerializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        RECIPE_TYPES.register(eventBus);
    }
}