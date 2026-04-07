package fr.jachou.cryptoworld.item;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.item.custom.CollectibleItem;
import fr.jachou.cryptoworld.item.custom.CryptoworldDetector;
import fr.jachou.cryptoworld.item.custom.RigPartItem;
import fr.jachou.cryptoworld.item.custom.SiliciumHyperCharged;
import fr.jachou.cryptoworld.market.CoinType;
import fr.jachou.cryptoworld.rig.RigPartStats;
import fr.jachou.cryptoworld.rig.RigPartType;
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
    public static final RegistryObject<Item> ETHEREUM = ITEMS.register("ethereum",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> CRYPTONIUM_INGOT = ITEMS.register("cryptonium_ingot",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> GTX_1080 = ITEMS.register("gtx_1080",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(640),
                    new RigPartStats(RigPartType.COMPUTE, CoinType.BITCOIN, 4, 10, 8, 0, 1, 0, 0, 1)));

    public static final RegistryObject<Item> SILICIUM = ITEMS.register("silicium",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SILICIUM_HYPERCHARGED = ITEMS.register("silicium_hypercharged",
            () -> new SiliciumHyperCharged(new Item.Properties(), 1200));

    public static final RegistryObject<Item> RTX_2080 = ITEMS.register("rtx_2080",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(720),
                    new RigPartStats(RigPartType.COMPUTE, CoinType.ETHEREUM, 6, 13, 11, 0, 2, 0, 0, 1)));

    public static final RegistryObject<Item> CRYPTOWORLD_DETECTOR = ITEMS.register("cryptoworld_detector",
            () -> new CryptoworldDetector(new Item.Properties().durability(128)));

    public static final RegistryObject<Item> RAM = ITEMS.register("ram",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(480),
                    new RigPartStats(RigPartType.MEMORY, null, 0, 0, 0, 0, 12, 0, 0, 1)));

    public static final RegistryObject<Item> ASIC_MK1 = ITEMS.register("asic_mk1",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(760),
                    new RigPartStats(RigPartType.COMPUTE, CoinType.BITCOIN, 8, 16, 15, 0, 0, 0, 0, 2)));

    public static final RegistryObject<Item> PSU_BASIC = ITEMS.register("psu_basic",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(680),
                    new RigPartStats(RigPartType.PSU, null, 0, 0, 0, 0, 0, 28, 0, 1)));

    public static final RegistryObject<Item> PSU_REDLINE = ITEMS.register("psu_redline",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(860),
                    new RigPartStats(RigPartType.PSU, null, 0, 0, 0, 0, 2, 44, 0, 1)));

    public static final RegistryObject<Item> AIR_COOLER = ITEMS.register("air_cooler",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(560),
                    new RigPartStats(RigPartType.COOLING, null, 0, 2, 0, 14, 1, 0, 0, 1)));

    public static final RegistryObject<Item> WATER_COOLER = ITEMS.register("water_cooler",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(760),
                    new RigPartStats(RigPartType.COOLING, null, 0, 3, 0, 24, 2, 0, 0, 1)));

    public static final RegistryObject<Item> GRID_UPGRADE = ITEMS.register("grid_upgrade",
            () -> new RigPartItem(new Item.Properties().stacksTo(1).durability(420),
                    new RigPartStats(RigPartType.GRID_UPGRADE, null, 0, 0, 0, 0, 0, 0, 18, 0)));

    public static final RegistryObject<Item> COLLECTIBLE_TOKEN = ITEMS.register("collectible_token",
            () -> new CollectibleItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
