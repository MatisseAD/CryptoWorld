package fr.jachou.cryptoworld.block;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.MinerBlock;
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

import java.util.function.Supplier;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CryptoWorld.MODID);

    public static final RegistryObject<Block> MINER_BLOCK = registerBlock("miner_block",
            () -> new MinerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> SERVER_BLOCK = registerBlock("server_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)));

    public static final RegistryObject<Block> SILICIUM_ORE = registerBlock("silicium_ore",
            () -> new DropExperienceBlock(UniformInt.of(3, 6), (BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE))));

    public static final RegistryObject<Block> SILICIUM_BLOCK = registerBlock("silicium_block",
            () -> new Block((BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK))));

    public static final RegistryObject<Block> CRYPTONIUM_ORE = registerBlock("cryptonium_ore",
            () -> new DropExperienceBlock(UniformInt.of(4, 6), (BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_ORE))));

    public static final RegistryObject<Block> CRYPTONIUM_BLOCK = registerBlock("cryptonium_block",
            () -> new Block((BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK))));



    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
