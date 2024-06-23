package fr.jachou.cryptoworld.item;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.item.custom.MetalDetector;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CryptoWorld.MODID);

    public static final RegistryObject<Item> BITCOIN = ITEMS.register("bitcoin",
            () -> new Item(new Item.Properties()));
    public static final  RegistryObject<Item> ETHEREUM = ITEMS.register("ethereum",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CRYPTONIUM_INGOT = ITEMS.register("cryptonium_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> GTX_1080 = ITEMS.register("gtx_1080",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SILICIUM = ITEMS.register("silicium",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SILICIUM_HYPERCHARGED = ITEMS.register("silicium_hypercharged",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RTX_2080 = ITEMS.register("rtx_2080",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SILICIUM_DETECTOR = ITEMS.register("silicium_detector",
            () -> new MetalDetector(new Item.Properties().durability(100)));

    public static final RegistryObject<Item> RAM = ITEMS.register("ram",
            () -> new Item(new Item.Properties().food(ModFoods.RAM)));



    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
