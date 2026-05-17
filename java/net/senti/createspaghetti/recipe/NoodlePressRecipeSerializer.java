package net.senti.createspaghetti.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class NoodlePressRecipeSerializer implements RecipeSerializer<NoodlePressRecipe> {

    @Override
    public NoodlePressRecipe fromJson(ResourceLocation id, JsonObject json) {
        Ingredient input = Ingredient.fromJson(json.get("ingredient"));

        JsonObject resultObj = GsonHelper.getAsJsonObject(json, "result");
        ResourceLocation itemId = new ResourceLocation(GsonHelper.getAsString(resultObj, "item"));
        int count = GsonHelper.getAsInt(resultObj, "count", 1);
        ItemStack output = new ItemStack(ForgeRegistries.ITEMS.getValue(itemId), count);

        int processingTime = GsonHelper.getAsInt(json, "processingTime", 100);

        return new NoodlePressRecipe(id, input, output, processingTime);
    }

    @Override
    public @Nullable NoodlePressRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
        Ingredient input = Ingredient.fromNetwork(buf);
        ItemStack output = buf.readItem();
        int processingTime = buf.readInt();
        return new NoodlePressRecipe(id, input, output, processingTime);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, NoodlePressRecipe recipe) {
        recipe.getInput().toNetwork(buf);
        buf.writeItem(recipe.getOutput());
        buf.writeInt(recipe.getProcessingTime());
    }
}