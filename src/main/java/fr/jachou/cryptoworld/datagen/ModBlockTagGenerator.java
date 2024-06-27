package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,  @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CryptoWorld.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Blocks.METAL_DETECTORS)
                .add(ModBlocks.CRYPTONIUM_ORE.get()).add(ModBlocks.SILICIUM_ORE.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CRYPTONIUM_ORE.get(),
                        ModBlocks.CRYPTONIUM_ORE.get(),
                        ModBlocks.CRYPTONIUM_BLOCK.get(),
                        ModBlocks.SERVER_BLOCK.get(),
                        ModBlocks.SILICIUM_ORE.get(),
                        ModBlocks.SILICIUM_BLOCK.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.CRYPTONIUM_ORE.get()).add(ModBlocks.CRYPTONIUM_BLOCK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SILICIUM_ORE.get()).add(ModBlocks.SERVER_BLOCK.get());
    }
}
