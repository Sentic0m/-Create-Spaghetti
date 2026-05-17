package net.senti.createspaghetti.recipe;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;


public class SplittingOutput {

    @Nullable private final ItemStack  item;
    @Nullable private final FluidStack fluid;

    private SplittingOutput(@Nullable ItemStack item, @Nullable FluidStack fluid) {
        this.item  = item;
        this.fluid = fluid;
    }

    public static SplittingOutput ofItem(ItemStack stack) {
        return new SplittingOutput(stack, null);
    }

    public static SplittingOutput ofFluid(FluidStack stack) {
        return new SplittingOutput(null, stack);
    }

    public boolean isItem()  { return item  != null; }
    public boolean isFluid() { return fluid != null; }

    public ItemStack getItem()   { return item.copy(); }

    public FluidStack getFluid() { return fluid.copy(); }

    public static SplittingOutput fromJson(JsonObject json) {
        if (json.has("fluid")) {
            ResourceLocation id = new ResourceLocation(json.get("fluid").getAsString());
            int amount = json.has("amount") ? json.get("amount").getAsInt() : 1000;
            var fluidType = ForgeRegistries.FLUIDS.getValue(id);
            if (fluidType == null)
                throw new IllegalArgumentException("Unknown fluid: " + id);
            return ofFluid(new FluidStack(fluidType, amount));
        } else {
            return ofItem(ShapedRecipe.itemStackFromJson(json));
        }
    }

    public static SplittingOutput fromNetwork(FriendlyByteBuf buf) {
        boolean isFluid = buf.readBoolean();
        if (isFluid) return ofFluid(FluidStack.readFromPacket(buf));
        else         return ofItem(buf.readItem());
    }

    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeBoolean(isFluid());
        if (isFluid()) fluid.writeToPacket(buf);
        else           buf.writeItem(item);
    }

    @Override
    public String toString() {
        return isFluid() ? "FluidOutput{" + fluid + "}" : "ItemOutput{" + item + "}";
    }
}