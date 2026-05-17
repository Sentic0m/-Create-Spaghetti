package net.senti.createspaghetti.item.custom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class Meatball extends Item {

    public Meatball(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack result = super.finishUsingItem(stack, level, entity);

        if (!level.isClientSide()) {
            double random = Math.random();

            if (random < 0.50) {
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 200, 0));

            } else if (random < 0.80) {
                // nichts

            } else if (random < 0.90) {
                entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0));

            } else {
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200, 0));
            }
        }

        return result;
    }
}
