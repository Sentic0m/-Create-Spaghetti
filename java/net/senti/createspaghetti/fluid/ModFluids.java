package net.senti.createspaghetti.fluid;

import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.item.ModItems;

public class ModFluids {

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, CreateSpaghetti.MOD_ID);

    public static final RegistryObject<FlowingFluid> SAUCE_SOURCE =
            FLUIDS.register("sauce", () -> new ForgeFlowingFluid.Source(ModFluids.SAUCE_PROPERTIES));

    public static final RegistryObject<FlowingFluid> SAUCE_FLOWING =
            FLUIDS.register("sauce_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.SAUCE_PROPERTIES));

    public static final ForgeFlowingFluid.Properties SAUCE_PROPERTIES =
            new ForgeFlowingFluid.Properties(
                    ModFluidTypes.SAUCE_FLUID_TYPE,
                    SAUCE_SOURCE,
                    SAUCE_FLOWING
            )
                    .levelDecreasePerBlock(2)
                    .tickRate(25)
                    .slopeFindDistance(3)
                    .explosionResistance(100f)
                    .block(ModBlocks.SAUCE_BLOCK)
                    .bucket(ModItems.SAUCE_BUCKET);

}