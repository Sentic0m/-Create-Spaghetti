package net.senti.createspaghetti.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.item.ModItems;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, CreateSpaghetti.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        simpleItem(ModItems.SPAGHETTI_GRASS_ITEM);
        simpleItem(ModItems.MEATBALL);
        simpleItem(ModItems.PLATE);
        simpleItem(ModItems.SPAGHETTISAUCE);
        simpleItem(ModItems.SPAGHETTI);
        simpleItem(ModItems.SPAGHETTIMEATBALLS);
        simpleItem(ModItems.SPAGHETTISAUCEMEATBALL);
        simpleItem(ModItems.TOMATO);
        simpleItem(ModItems.SAUCE_BUCKET);
        simpleItem(ModItems.TOMATOSEEDS);
        simpleItem(ModItems.UNFINISHED_PLATE);


    }

    private ItemModelBuilder simpleItem(RegistryObject<Item> item) {
        return withExistingParent(item.getId().getPath(),
                new ResourceLocation("item/generated")).texture("layer0,",
                new ResourceLocation(CreateSpaghetti.MOD_ID,"item/" + item.getId().getPath()));
    }
}
