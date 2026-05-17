package net.senti.createspaghetti.item;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public class ModFoods {
    public static final FoodProperties MEATBALL = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(2)
            .saturationMod(0.4f)
            .effect(() -> new MobEffectInstance(MobEffects.WEAKNESS, 170), 0.5f)
            .build();

    public static final FoodProperties SPAGHETTISAUCE = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(2)
            .saturationMod(0.5f)
            .build();

    public static final FoodProperties SPAGHETTIMEATBALLS = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(3)
            .saturationMod(0.7f)
            .build();

    public static final FoodProperties SPAGHETTISAUCEMEATBALL = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(5)
            .saturationMod(1f)
            .build();

    public static final FoodProperties SPAGHETTI = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();

    public static final FoodProperties RAW_EGG = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(1)
            .saturationMod(0.1f)
            .build();

    public static final FoodProperties SPAGHETTI_PLATE = new FoodProperties.Builder()
            .alwaysEat()
            .nutrition(2)
            .saturationMod(0.3f)
            .build();



}

