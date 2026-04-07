package fr.jachou.cryptoworld.util;

import fr.jachou.cryptoworld.CryptoWorld;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Blocks {
        public static final TagKey<Block> METAL_DETECTORS = tag("metal_detectors");
        public static final TagKey<Block> POWER_GENERATORS = tag("power_generators");
        public static final TagKey<Block> POWER_STORAGE = tag("power_storage");
        public static final TagKey<Block> SERVER_ROOM_COOLING = tag("server_room_cooling");

        private static TagKey<Block> tag(String name) {
            return BlockTags.create(new ResourceLocation(CryptoWorld.MODID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> COINS = tag("coins");
        public static final TagKey<Item> GPUS = tag("gpus");
        public static final TagKey<Item> RIG_COMPUTE = tag("rig_compute");
        public static final TagKey<Item> RIG_PSU = tag("rig_psu");
        public static final TagKey<Item> RIG_COOLING = tag("rig_cooling");
        public static final TagKey<Item> MEMORIES = tag("memories");
        public static final TagKey<Item> MINER_FUEL = tag("miner_fuel");
        public static final TagKey<Item> BACKUP_CELLS = tag("backup_cells");
        public static final TagKey<Item> GRID_UPGRADES = tag("grid_upgrades");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(new ResourceLocation(CryptoWorld.MODID, name));
        }
    }
}
