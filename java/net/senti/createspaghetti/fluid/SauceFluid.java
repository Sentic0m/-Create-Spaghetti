package net.senti.createspaghetti.fluid;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public abstract class SauceFluid extends ForgeFlowingFluid {

    protected SauceFluid(Properties properties) {
        super(properties);
    }

    public static class Source extends SauceFluid {
        public Source(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state) { return 8; }

        @Override
        public boolean isSource(FluidState state) { return true; }
    }

    public static class Flowing extends SauceFluid {
        public Flowing(Properties properties) {
            super(properties);
        }

        @Override
        public int getAmount(FluidState state) {
            return state.getValue(LEVEL);
        }

        @Override
        public boolean isSource(FluidState state) { return false; }
    }
}