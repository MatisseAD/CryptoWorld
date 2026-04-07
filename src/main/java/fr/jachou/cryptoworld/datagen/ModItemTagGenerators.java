package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.item.ModItems;
import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagGenerators extends ItemTagsProvider {
    public ModItemTagGenerators(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(packOutput, lookupProvider, blockTags, CryptoWorld.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Items.COINS)
                .add(ModItems.BITCOIN.get(), ModItems.ETHEREUM.get());

        this.tag(ModTags.Items.GPUS)
                .add(ModItems.GTX_1080.get(), ModItems.RTX_2080.get());

        this.tag(ModTags.Items.RIG_COMPUTE)
                .add(ModItems.GTX_1080.get(), ModItems.RTX_2080.get(), ModItems.ASIC_MK1.get());

        this.tag(ModTags.Items.RIG_PSU)
                .add(ModItems.PSU_BASIC.get(), ModItems.PSU_REDLINE.get());

        this.tag(ModTags.Items.RIG_COOLING)
                .add(ModItems.AIR_COOLER.get(), ModItems.WATER_COOLER.get());

        this.tag(ModTags.Items.MEMORIES)
                .add(ModItems.RAM.get());

        this.tag(ModTags.Items.MINER_FUEL)
                .add(ModItems.SILICIUM_HYPERCHARGED.get());

        this.tag(ModTags.Items.BACKUP_CELLS)
                .add(ModItems.SILICIUM_HYPERCHARGED.get());

        this.tag(ModTags.Items.GRID_UPGRADES)
                .add(ModItems.GRID_UPGRADE.get());
    }
}
