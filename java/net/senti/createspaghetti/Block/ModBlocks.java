package net.senti.createspaghetti.Block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.Block.custom.AnimatedTestBlock;
import net.senti.createspaghetti.Block.custom.ItemSplitterBlock;
//import net.senti.createspaghetti.Block.entity.not_in_use.NoodlePressBlock;
import net.senti.createspaghetti.Block.custom.SpaghettiKineticBlock;
import net.senti.createspaghetti.Block.custom.SpaghettiPressBlock;
import net.senti.createspaghetti.Block.custom.TomatoCropBlock;
import net.senti.createspaghetti.CreateSpaghetti;
import net.senti.createspaghetti.fluid.ModFluids;
import net.senti.createspaghetti.item.ModItems;
import net.senti.createspaghetti.item.SpaghettiKineticBlockItem;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CreateSpaghetti.MOD_ID);

    public static final RegistryObject<Block> MEATBALL_BLOCK = registerBlock("meatball_block",
            () -> new Block(BlockBehaviour.Properties.copy(Blocks.SLIME_BLOCK)));

    public static final RegistryObject<Block> SSPAGHETTI_PRESS = registerBlock("spaghetti_press",
            () -> new SpaghettiPressBlock(BlockBehaviour.Properties.copy(Blocks.ANDESITE).mapColor(MapColor.PODZOL)
                    .sound(SoundType.WOOD).noOcclusion()));

    public static final RegistryObject<Block> ANIMATED_TEST_BLOCK = registerBlock("animated_test_block",
            () -> new AnimatedTestBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final RegistryObject<Block> ITEM_SPLITTER = registerBlock("item_splitter",
            () -> new ItemSplitterBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).noOcclusion()));

    public static final RegistryObject<Block> SPAGHETTI_KINETIC_BLOCK = registerBlock("spaghetti_kinetic_block",
            () -> new SpaghettiKineticBlock(BlockBehaviour.Properties.of().strength(1.5f, 6.0f).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));

    public static final RegistryObject<Block> TOMATO_CROP =
            BLOCKS.register("tomato_crop",
                    () -> new TomatoCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT)
                            .noCollission().noOcclusion().randomTicks()));

    public static final RegistryObject<Block> SPAGHETTI_GRASS =
            BLOCKS.register("spaghetti_grass", SpaghettiGrassBlock::new);

    public static final RegistryObject<LiquidBlock> SAUCE_BLOCK = BLOCKS.register("sauce",
            () -> new LiquidBlock(ModFluids.SAUCE_SOURCE,
                    BlockBehaviour.Properties.copy(Blocks.WATER).noLootTable()) {
                @Override
                public void neighborChanged(BlockState state, Level level, BlockPos pos,
                                            Block block, BlockPos fromPos, boolean isMoving) {
                    if (level.getFluidState(fromPos).getType().isSame(Fluids.WATER)) return;
                    super.neighborChanged(state, level, pos, block, fromPos, isMoving);
                }
            });

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        if (name.equals("spaghetti_press")) {
            return ModItems.ITEMS.register(name, () -> new SpaghettiKineticBlockItem(block.get(), new Item.Properties()));
        }
        if (name.equals("spaghetti_kinetic_block")) {
            return ModItems.ITEMS.register(name, () -> new SpaghettiKineticBlockItem(block.get(), new Item.Properties()));
        }
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}