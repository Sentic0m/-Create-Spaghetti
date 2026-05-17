package net.senti.createspaghetti.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.recipe.ModRecipeTypes;
import net.senti.createspaghetti.recipe.SplittingRecipe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@JeiPlugin
public class CreateSpaghettiJEIPlugin implements IModPlugin {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateSpaghettiJEIPlugin.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CreateSpaghetti.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new SplittingRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft mc = Minecraft.getInstance();

        // Level kann null sein wenn JEI vor dem Weltladen initialisiert
        if (mc.level == null) {
            LOGGER.warn("[CreateSpaghetti JEI] level is null, splitting recipes not registered!");
            return;
        }

        RecipeManager manager = mc.level.getRecipeManager();
        List<SplittingRecipe> recipes = manager.getAllRecipesFor(ModRecipeTypes.SPLITTING_TYPE.get());

        LOGGER.info("[CreateSpaghetti JEI] Registering {} splitting recipe(s)", recipes.size());

        registration.addRecipes(SplittingRecipeCategory.RECIPE_TYPE, recipes);
    }
}