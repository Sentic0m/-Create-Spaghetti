package net.senti.createspaghetti.screen;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.senti.createspaghetti.CreateSpaghetti;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CreateSpaghetti.MOD_ID);

    public static final RegistryObject<MenuType<ItemSplitterMenu>> ITEM_SPLITTER_MENU =
            registeMenuType("item_splitter_menu", ItemSplitterMenu::new);


    public static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registeMenuType (String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
