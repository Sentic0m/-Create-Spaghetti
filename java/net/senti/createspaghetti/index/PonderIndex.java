package net.senti.createspaghetti.index;

import com.simibubi.create.infrastructure.ponder.AllCreatePonderTags;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.client.ponder.SpaghettiPonderScenes;

public class PonderIndex {

    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        helper.forComponents(new ResourceLocation(CreateSpaghetti.MOD_ID, "spaghetti_kinetic_block"))
                .addStoryBoard(
                        new ResourceLocation(CreateSpaghetti.MOD_ID, "spaghetti_kinetic_block"),
                        SpaghettiPonderScenes::spaghettiKineticBlock,
                        AllCreatePonderTags.KINETIC_APPLIANCES
                );
        //.addStoryBoard(
        //new ResourceLocation(CreateSpaghetti.MOD_ID, "spaghetti_kinetic_block_fluid"),
        //SpaghettiPonderScenes::spaghettiKineticBlockFluid,
        //AllCreatePonderTags.FLUIDS
        //)
    }
}