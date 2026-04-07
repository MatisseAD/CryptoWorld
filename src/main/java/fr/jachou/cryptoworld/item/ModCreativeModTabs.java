package fr.jachou.cryptoworld.item;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CryptoWorld.MODID);

    public static final RegistryObject<CreativeModeTab> CRYPTO_TAB = CREATIVE_MODE_TABS.register("crypto_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModItems.BITCOIN.get()))
                    .title(Component.translatable("creativetab.crypto_tab"))
                    .displayItems((pParameters, pOutput) -> {

                        // Items
                        pOutput.accept(ModItems.BITCOIN.get());
                        pOutput.accept(ModItems.ETHEREUM.get());
                        pOutput.accept(ModItems.CRYPTONIUM_INGOT.get());
                        pOutput.accept(ModItems.GTX_1080.get());
                        pOutput.accept(ModItems.RTX_2080.get());
                        pOutput.accept(ModItems.CRYPTOWORLD_DETECTOR.get());
                        pOutput.accept(ModItems.RAM.get());
                        pOutput.accept(ModItems.SILICIUM.get());
                        pOutput.accept(ModItems.SILICIUM_HYPERCHARGED.get());
                        pOutput.accept(ModItems.ASIC_MK1.get());
                        pOutput.accept(ModItems.PSU_BASIC.get());
                        pOutput.accept(ModItems.PSU_REDLINE.get());
                        pOutput.accept(ModItems.AIR_COOLER.get());
                        pOutput.accept(ModItems.WATER_COOLER.get());
                        pOutput.accept(ModItems.GRID_UPGRADE.get());
                        pOutput.accept(ModItems.COLLECTIBLE_TOKEN.get());

                        // Blocks
                        pOutput.accept(ModBlocks.SERVER_BLOCK.get());
                        pOutput.accept(ModBlocks.CRYPTONIUM_ORE.get());
                        pOutput.accept(ModBlocks.SILICIUM_ORE.get());
                        pOutput.accept(ModBlocks.CRYPTONIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.SILICIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.MINER_BLOCK.get());
                        pOutput.accept(ModBlocks.SOLAR_PANEL.get());
                        pOutput.accept(ModBlocks.WIND_TURBINE.get());
                        pOutput.accept(ModBlocks.BATTERY_BANK.get());
                        pOutput.accept(ModBlocks.VENTILATION_UNIT.get());
                        pOutput.accept(ModBlocks.WATER_COOLING_UNIT.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
