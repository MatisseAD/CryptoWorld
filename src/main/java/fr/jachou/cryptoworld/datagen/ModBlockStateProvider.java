package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, CryptoWorld.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (StylizedAssetManifest.BlockModelSpec spec : StylizedAssetManifest.loadBlockModels()) {
            RegistryObject<Block> block = ModBlocks.getRegisteredBlock(spec.name());
            if (block == null) {
                throw new IllegalStateException("Block declared in stylized manifest is not registered: " + spec.name());
            }

            switch (spec.layout()) {
                case CUBE_ALL -> blockWithItem(block, texture(spec.all()));
                case CUBE_BOTTOM_TOP -> machineBlock(block, spec);
            }
        }
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject, ResourceLocation texture) {
        simpleBlockWithItem(blockRegistryObject.get(),
                models().cubeAll(blockRegistryObject.getId().getPath(), texture));
    }

    private void machineBlock(RegistryObject<Block> blockRegistryObject, StylizedAssetManifest.BlockModelSpec spec) {
        ModelFile model = models().cubeBottomTop(blockRegistryObject.getId().getPath(),
                texture(spec.side()),
                texture(spec.bottomOrTop()),
                texture(spec.top()));
        simpleBlock(blockRegistryObject.get(), model);
        simpleBlockItem(blockRegistryObject.get(), model);
    }

    private ResourceLocation texture(String name) {
        return new ResourceLocation(CryptoWorld.MODID, "block/" + name);
    }
}
