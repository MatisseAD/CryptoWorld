package fr.jachou.cryptoworld;

import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.blockentity.ModBlockEntities;
import fr.jachou.cryptoworld.item.ModCreativeModTabs;
import fr.jachou.cryptoworld.item.ModItems;
import fr.jachou.cryptoworld.menu.ModMenuTypes;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CryptoWorld.MODID)
public class CryptoWorld {
    public static final String MODID = "cryptoworld";

    public CryptoWorld() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModCreativeModTabs.register(bus);
        ModItems.register(bus);
        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModMenuTypes.register(bus);

        bus.addListener(this::setup);
        bus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BITCOIN);
            event.accept(ModItems.ETHEREUM);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Common setup hook reserved for future networking or compat init.
    }
}
