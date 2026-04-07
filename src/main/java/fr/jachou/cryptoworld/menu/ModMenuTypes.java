package fr.jachou.cryptoworld.menu;

import fr.jachou.cryptoworld.CryptoWorld;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CryptoWorld.MODID);

    public static final RegistryObject<MenuType<MinerMenu>> MINER_MENU =
            MENUS.register("miner_menu", () -> IForgeMenuType.create(MinerMenu::new));

    public static final RegistryObject<MenuType<ServerMenu>> SERVER_MENU =
            MENUS.register("server_menu", () -> IForgeMenuType.create(ServerMenu::new));

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
