package net.senti.createspaghetti.Block.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.Block.ModBlocks;
import net.senti.createspaghetti.Block.entity.not_in_use.NoodlePressBlockEntity;
import net.senti.createspaghetti.CreateSpaghetti;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreateSpaghetti.MOD_ID);

    public static final RegistryObject<BlockEntityType<ItemSplitterEntity>> ITEM_SPLITTER_BE =
            BLOCK_ENTITIES.register("item_splitter_be", () ->
                    BlockEntityType.Builder.of(ItemSplitterEntity::new,
                            ModBlocks.ITEM_SPLITTER.get()).build(null));

    public static final RegistryObject<BlockEntityType<SpaghettiKineticBlockEntity>> SPAGHETTI_KINETIC_BE =
            BLOCK_ENTITIES.register("spaghetti_kinetic_be", () ->
                    BlockEntityType.Builder.of((pos, state) -> new SpaghettiKineticBlockEntity(ModBlockEntities.SPAGHETTI_KINETIC_BE.get(), pos, state),
                            ModBlocks.SPAGHETTI_KINETIC_BLOCK.get()).build(null));

    /*
    public static final RegistryObject<BlockEntityType<NoodlePressBlockEntity>> NOODLE_PRESS_BE =
            BLOCK_ENTITIES.register("noodle_press",
                    () -> BlockEntityType.Builder
                            .of((pos, state) -> new NoodlePressBlockEntity(ModBlockEntities.NOODLE_PRESS_BE.get(), pos, state), ModBlocks.NOODLE_PRESS.get()).build(null));
     */
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}