package net.senti.createspaghetti.client;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.createmod.catnip.render.CachedBuffers;
import net.createmod.catnip.render.SuperByteBuffer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.Block.entity.ModBlockEntities;
import net.senti.createspaghetti.Block.entity.SpaghettiKineticBlockEntity;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.client.ponder.ModPonderPlugin;
import com.mojang.blaze3d.vertex.PoseStack;

@Mod.EventBusSubscriber(
        modid = CreateSpaghetti.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class ClientSetup {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {

        BlockEntityRenderers.register(
                ModBlockEntities.SPAGHETTI_KINETIC_BE.get(),
                ctx -> new KineticBlockEntityRenderer<SpaghettiKineticBlockEntity>(ctx) {
                    @Override
                    protected void renderSafe(SpaghettiKineticBlockEntity be, float partialTicks,
                                              PoseStack ms, MultiBufferSource buffer, int light, int overlay) {
                        if (be.getLevel() instanceof net.createmod.ponder.api.level.PonderLevel) return;
                        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);

                        SuperByteBuffer shaft = CachedBuffers.partial(
                                AllPartialModels.SHAFT_HALF,
                                be.getBlockState()
                        );

                        shaft.rotateCentered(
                                (float) Math.toRadians(-90),
                                Direction.WEST
                        );

                        KineticBlockEntityRenderer.kineticRotationTransform(
                                shaft, be,
                                Direction.Axis.Z,
                                -KineticBlockEntityRenderer.getAngleForBe(be, be.getBlockPos().above(), Direction.Axis.Z),
                                light
                        ).renderInto(ms, buffer.getBuffer(RenderType.cutoutMipped()));
                    }
                }
        );

        ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.SPAGHETTI_GRASS.get(),
                RenderType.cutout()
        );

        ItemBlockRenderTypes.setRenderLayer(
                ModBlocks.SPAGHETTI_KINETIC_BLOCK.get(),
                RenderType.cutout()
        );

        net.createmod.ponder.foundation.PonderIndex.addPlugin(new ModPonderPlugin());
    }
}