package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CryptoWorld.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // Cryptonium Group
        blockWithItem(ModBlocks.CRYPTONIUM_BLOCK);
        blockWithItem(ModBlocks.CRYPTONIUM_ORE);

        // Silicium Group
        blockWithItem(ModBlocks.SILICIUM_ORE);
        blockWithItem(ModBlocks.SILICIUM_BLOCK);
        blockWithItem(ModBlocks.SERVER_BLOCK);
        blockWithItem(ModBlocks.EXCHANGE_BLOCK);
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}
