package fr.jachou.cryptoworld.datagen;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
                                @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, CryptoWorld.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        this.tag(ModTags.Blocks.METAL_DETECTORS)
                .add(ModBlocks.CRYPTONIUM_ORE.get(), ModBlocks.SILICIUM_ORE.get());

        this.tag(ModTags.Blocks.POWER_GENERATORS)
                .add(ModBlocks.SOLAR_PANEL.get(), ModBlocks.WIND_TURBINE.get());

        this.tag(ModTags.Blocks.POWER_STORAGE)
                .add(ModBlocks.BATTERY_BANK.get());

        this.tag(ModTags.Blocks.SERVER_ROOM_COOLING)
                .add(ModBlocks.VENTILATION_UNIT.get(), ModBlocks.WATER_COOLING_UNIT.get());

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CRYPTONIUM_ORE.get(),
                        ModBlocks.CRYPTONIUM_BLOCK.get(),
                        ModBlocks.SERVER_BLOCK.get(),
                        ModBlocks.SILICIUM_ORE.get(),
                        ModBlocks.SILICIUM_BLOCK.get(),
                        ModBlocks.MINER_BLOCK.get(),
                        ModBlocks.SOLAR_PANEL.get(),
                        ModBlocks.WIND_TURBINE.get(),
                        ModBlocks.BATTERY_BANK.get(),
                        ModBlocks.VENTILATION_UNIT.get(),
                        ModBlocks.WATER_COOLING_UNIT.get());

        this.tag(BlockTags.NEEDS_DIAMOND_TOOL)
                .add(ModBlocks.CRYPTONIUM_ORE.get(), ModBlocks.CRYPTONIUM_BLOCK.get());

        this.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(ModBlocks.SILICIUM_ORE.get(),
                        ModBlocks.SILICIUM_BLOCK.get(),
                        ModBlocks.SERVER_BLOCK.get(),
                        ModBlocks.MINER_BLOCK.get(),
                        ModBlocks.SOLAR_PANEL.get(),
                        ModBlocks.WIND_TURBINE.get(),
                        ModBlocks.BATTERY_BANK.get(),
                        ModBlocks.VENTILATION_UNIT.get(),
                        ModBlocks.WATER_COOLING_UNIT.get());
    }
}
