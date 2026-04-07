package fr.jachou.cryptoworld.client;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.menu.ModMenuTypes;
import fr.jachou.cryptoworld.screen.MinerScreen;
import fr.jachou.cryptoworld.screen.ServerScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = CryptoWorld.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientModEvents {
    private ClientModEvents() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(ModMenuTypes.MINER_MENU.get(), MinerScreen::new);
            MenuScreens.register(ModMenuTypes.SERVER_MENU.get(), ServerScreen::new);
        });
    }
}
