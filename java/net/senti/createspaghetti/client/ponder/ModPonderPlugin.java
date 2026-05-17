package net.senti.createspaghetti.client.ponder;

import net.createmod.ponder.api.registration.PonderPlugin;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.minecraft.resources.ResourceLocation;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.index.PonderIndex;

public class ModPonderPlugin implements PonderPlugin {

    @Override
    public String getModId() {
        return CreateSpaghetti.MOD_ID;
    }

    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderIndex.register(helper);
    }
}