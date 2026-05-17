package net.senti.createspaghetti.recipe;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.senti.createspaghetti.CreateSpaghetti;

public class SplittingRecipe implements Recipe<Container> {

    private final ResourceLocation id;
    private final ItemStack        input;
    private final SplittingOutput  output1;
    private final SplittingOutput  output2;
    private final int              processingTime;

    public SplittingRecipe(ResourceLocation id,
                           ItemStack input,
                           SplittingOutput output1,
                           SplittingOutput output2,
                           int processingTime) {
        this.id             = id;
        this.input          = input;
        this.output1        = output1;
        this.output2        = output2;
        this.processingTime = processingTime;
    }

    // ── Getter ─────────────────────────────────────────────────────────────

    public ItemStack       getInput()          { return input; }
    public SplittingOutput getOutput1()        { return output1; }
    public SplittingOutput getOutput2()        { return output2; }
    public int             getProcessingTime() { return processingTime; }

    // ── Recipe-Interface ───────────────────────────────────────────────────

    public boolean matches(ItemStack stack) {
        return ItemStack.isSameItem(stack, input);
    }

    @Override
    public boolean matches(Container container, Level level) {
        return matches(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return getResultItem(registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) { return true; }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        // Vanilla braucht ein Item – erstes Item-Output liefern, sonst leer
        if (output1.isItem()) return output1.getItem();
        if (output2.isItem()) return output2.getItem();
        return ItemStack.EMPTY;
    }

    @Override
    public ResourceLocation getId() { return id; }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeTypes.SPLITTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SPLITTING_TYPE.get();
    }

    public static class Serializer implements RecipeSerializer<SplittingRecipe> {

        @Override
        public SplittingRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack       input          = ShapedRecipe.itemStackFromJson(json.getAsJsonObject("input"));
            SplittingOutput output1        = SplittingOutput.fromJson(json.getAsJsonObject("output1"));
            SplittingOutput output2        = SplittingOutput.fromJson(json.getAsJsonObject("output2"));
            int             processingTime = json.has("processingTime")
                    ? json.get("processingTime").getAsInt() : 100;
            return new SplittingRecipe(id, input, output1, output2, processingTime);
        }

        @Override
        public SplittingRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            ItemStack       input          = buf.readItem();
            SplittingOutput output1        = SplittingOutput.fromNetwork(buf);
            SplittingOutput output2        = SplittingOutput.fromNetwork(buf);
            int             processingTime = buf.readInt();
            return new SplittingRecipe(id, input, output1, output2, processingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, SplittingRecipe recipe) {
            buf.writeItem(recipe.input);
            recipe.output1.toNetwork(buf);
            recipe.output2.toNetwork(buf);
            buf.writeInt(recipe.processingTime);
        }
    }
}