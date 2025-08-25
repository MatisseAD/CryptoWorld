package fr.jachou.cryptoworld;

import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.blockentity.ModBlockEntities;
import fr.jachou.cryptoworld.datagen.DataGenerators;
import fr.jachou.cryptoworld.item.ModCreativeModTabs;
import fr.jachou.cryptoworld.item.ModItems;
import fr.jachou.cryptoworld.menu.ModMenus;
import fr.jachou.cryptoworld.util.PriceManager;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
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
        ModMenus.register(bus);

        bus.addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        bus.addListener(this::doClientStuff);
        bus.addListener(this::addCreative);

        PriceManager.init();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(ModItems.BITCOIN);
            event.accept(ModItems.ETHEREUM);
        }
    }

    private void setup(final FMLCommonSetupEvent event) {
        // Initialisation côté serveur
    }

    private void doClientStuff(final FMLClientSetupEvent event) {
    }
}
