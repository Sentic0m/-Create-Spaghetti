package net.senti.createspaghetti.fluid;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.CreateSpaghetti;
import java.util.function.Consumer;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;

public class ModFluidTypes {

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, CreateSpaghetti.MOD_ID);

    private static final ResourceLocation SAUCE_STILL =
            new ResourceLocation(CreateSpaghetti.MOD_ID, "block/tomato_sauce_still");
    private static final ResourceLocation SAUCE_FLOWING =
            new ResourceLocation(CreateSpaghetti.MOD_ID, "block/tomato_sauce_flowing");

    public static final RegistryObject<FluidType> SAUCE_FLUID_TYPE =
            FLUID_TYPES.register("sauce", () -> new FluidType(
                    FluidType.Properties.create()
                            .density(1400)
                            .viscosity(1500)
                            .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                            .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
            ) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return SAUCE_STILL;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return SAUCE_FLOWING;
                        }
                    });
                }
            });
}