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

                        // Blocks
                        pOutput.accept(ModBlocks.SERVER_BLOCK.get());
                        pOutput.accept(ModBlocks.CRYPTONIUM_ORE.get());
                        pOutput.accept(ModBlocks.SILICIUM_ORE.get());
                        pOutput.accept(ModBlocks.CRYPTONIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.SILICIUM_BLOCK.get());
                        pOutput.accept(ModBlocks.EXCHANGE_BLOCK.get());
                    })
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}

