package net.senti.createspaghetti.jei;

import com.simibubi.create.foundation.gui.AllGuiTextures;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.jei.animations.AnimatedSpaghettiBlock;
import net.senti.createspaghetti.recipe.SplittingOutput;
import net.senti.createspaghetti.recipe.SplittingRecipe;

public class SplittingRecipeCategory implements IRecipeCategory<SplittingRecipe> {

    public static final RecipeType<SplittingRecipe> RECIPE_TYPE =
            RecipeType.create(CreateSpaghetti.MOD_ID, "splitting", SplittingRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final AnimatedSpaghettiBlock saw = new AnimatedSpaghettiBlock();

    public SplittingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(160, 70);
        this.icon = guiHelper.createDrawableItemStack(
                new ItemStack(ModBlocks.SPAGHETTI_KINETIC_BLOCK.get()));
    }

    @Override
    public RecipeType<SplittingRecipe> getRecipeType() { return RECIPE_TYPE; }

    @Override
    public Component getTitle() {
        return Component.translatable("jei." + CreateSpaghetti.MOD_ID + ".splitting");
    }

    @Override
    public IDrawable getBackground() { return background; }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SplittingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 44, 6)
                .addItemStack(recipe.getInput());

        addOutputSlot(builder, recipe.getOutput1(), 118, 49);
        addOutputSlot(builder, recipe.getOutput2(), 137, 49);

        builder.addSlot(RecipeIngredientRole.CATALYST, 1000, 11)
                .addItemStack(new ItemStack(ModBlocks.SPAGHETTI_KINETIC_BLOCK.get()));
    }

    private void addOutputSlot(IRecipeLayoutBuilder builder, SplittingOutput output, int x, int y) {
        if (output.isItem()) {
            builder.addSlot(RecipeIngredientRole.OUTPUT, x, y)
                    .addItemStack(output.getItem());
        } else {
            FluidStack fluid = output.getFluid();

            IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.OUTPUT, x, y);

            slot.addIngredient(ForgeTypes.FLUID_STACK, fluid);

            slot.addTooltipCallback((slotView, tooltip) -> {
                tooltip.add(Component.literal(fluid.getAmount() + " mB")
                        .withStyle(net.minecraft.ChatFormatting.GRAY));
            });
        }
    }

    @Override
    public void draw(SplittingRecipe recipe, IRecipeSlotsView slotsView, GuiGraphics graphics, double mouseX, double mouseY) {
        AllGuiTextures.JEI_SLOT.render(graphics, 43, 5);
        AllGuiTextures.JEI_SLOT.render(graphics, 117, 48);
        AllGuiTextures.JEI_SLOT.render(graphics, 136, 48);

        AllGuiTextures.JEI_DOWN_ARROW.render(graphics, 70, 6);
        AllGuiTextures.JEI_SHADOW.render(graphics, 72 - 17, 42 + 13);

        saw.draw(graphics, 72, 42);
    }
}