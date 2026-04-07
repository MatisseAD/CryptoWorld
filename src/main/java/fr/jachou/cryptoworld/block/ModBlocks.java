package fr.jachou.cryptoworld.block;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CryptoWorld.MODID);

    private static final Map<String, RegistryObject<? extends Block>> BLOCK_LOOKUP = new LinkedHashMap<>();

    public static final RegistryObject<Block> MINER_BLOCK = registerBlock("miner_block",
            () -> new MinerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(4.5F)));

    public static final RegistryObject<Block> SERVER_BLOCK = registerBlock("server_block",
            () -> new ServerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(4.0F)));

    public static final RegistryObject<Block> SOLAR_PANEL = registerBlock("solar_panel",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3.5F)));

    public static final RegistryObject<Block> WIND_TURBINE = registerBlock("wind_turbine",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3.5F)));

    public static final RegistryObject<Block> BATTERY_BANK = registerBlock("battery_bank",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(4.0F)));

    public static final RegistryObject<Block> VENTILATION_UNIT = registerBlock("ventilation_unit",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3.5F)));

    public static final RegistryObject<Block> WATER_COOLING_UNIT = registerBlock("water_cooling_unit",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(3.5F)));

    public static final RegistryObject<Block> SILICIUM_ORE = registerBlock("silicium_ore",
            () -> new DropExperienceBlock(UniformInt.of(2, 5), BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_ORE).strength(3.0F)));

    public static final RegistryObject<Block> SILICIUM_BLOCK = registerBlock("silicium_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).strength(4.0F)));

    public static final RegistryObject<Block> CRYPTONIUM_ORE = registerBlock("cryptonium_ore",
            () -> new DropExperienceBlock(UniformInt.of(4, 7), BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE).strength(4.5F)));

    public static final RegistryObject<Block> CRYPTONIUM_BLOCK = registerBlock("cryptonium_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK).strength(5.0F)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        BLOCK_LOOKUP.put(name, toReturn);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

    @SuppressWarnings("unchecked")
    public static RegistryObject<Block> getRegisteredBlock(String name) {
        return (RegistryObject<Block>) BLOCK_LOOKUP.get(name);
    }
}
