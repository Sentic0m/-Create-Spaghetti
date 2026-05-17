package net.senti.createspaghetti.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.item.ModItems;

import java.util.List;
import java.util.function.Consumer;

public class ModRecipeProvider extends RecipeProvider {
    public static final List<ItemLike> SPAGHETTI_SMELTABLES =
            List.of(ModItems.UNFINISHED_PLATE.get());

    public ModRecipeProvider(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> pWriter) {
        oreBlasting(pWriter, SPAGHETTI_SMELTABLES, RecipeCategory.MISC, ModItems.UNFINISHED_PLATE.get(), 0.25f, 200, "plate");
        oreSmelting(pWriter, SPAGHETTI_SMELTABLES, RecipeCategory.MISC, ModItems.UNFINISHED_PLATE.get(), 0.25f, 100, "plate");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MEATBALL_BLOCK.get())
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .pattern("SSS")
                .define('S', ModItems.PLATE.get())
                .unlockedBy(getHasName(ModItems.PLATE.get()), has(ModItems.PLATE.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PLATE.get(), 9)
                .requires(ModBlocks.MEATBALL_BLOCK.get())
                .unlockedBy(getHasName(ModBlocks.MEATBALL_BLOCK.get()), has(ModBlocks.MEATBALL_BLOCK.get()))
                .save(pWriter);
    }

    protected static void oreBlasting(Consumer<FinishedRecipe> p_248775_, List<ItemLike> p_251504_, RecipeCategory p_248846_, ItemLike p_249735_, float p_248783_, int p_250303_, String p_251984_) {
        oreCooking(p_248775_, RecipeSerializer.BLASTING_RECIPE, p_251504_, p_248846_, p_249735_, p_248783_, p_250303_, p_251984_, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> p_250791_, RecipeSerializer<? extends AbstractCookingRecipe> p_251817_, List<ItemLike> p_249619_, RecipeCategory p_251154_, ItemLike p_250066_, float p_251871_, int p_251316_, String p_251450_, String p_249236_) {
        for(ItemLike itemlike : p_249619_) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), p_251154_, p_250066_, p_251871_, p_251316_, p_251817_)
                    .group(p_251450_).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(p_250791_,  CreateSpaghetti.MOD_ID + getItemName(p_250066_) + p_249236_ + "_" + getItemName(itemlike));
        }

    }

}
