package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerators extends ItemTagsProvider {
    
    public ModItemTagGenerators(PackOutput pPackOutput, CompletableFuture<HolderLookup.Provider> pCompletableFuture, CompletableFuture<TagLookup<Block>> tagLookupCompletableFuture, @Nullable ExistingFileHelper existingFileHelper) {
        super(pPackOutput, pCompletableFuture, tagLookupCompletableFuture, CryptoWorld.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        
    }
}
