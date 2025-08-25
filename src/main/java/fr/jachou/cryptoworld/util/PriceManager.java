package fr.jachou.cryptoworld.util;

import fr.jachou.cryptoworld.item.ModItems;
import net.minecraft.world.item.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PriceManager {
    private static final Map<Item, Integer> PRICES = new HashMap<>();
    private static final Random RANDOM = new Random();

    public static void init() {
        PRICES.put(ModItems.BITCOIN.get(), 2); // 1 BTC -> 2 ETH
        PRICES.put(ModItems.ETHEREUM.get(), 2); // 2 ETH -> 1 BTC
    }

    public static void updateRandom() {
        // Randomly vary BTC to ETH rate between 1 and 4
        PRICES.put(ModItems.BITCOIN.get(), 1 + RANDOM.nextInt(4));
    }

    public static int getBitcoinToEthereumRate() {
        return PRICES.getOrDefault(ModItems.BITCOIN.get(), 1);
    }

    public static int getEthereumToBitcoinRate() {
        return PRICES.getOrDefault(ModItems.ETHEREUM.get(), 2);
    }
}
