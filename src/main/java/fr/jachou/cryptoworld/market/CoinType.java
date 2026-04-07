package fr.jachou.cryptoworld.market;

import fr.jachou.cryptoworld.item.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum CoinType {
    BITCOIN(16, 30),
    ETHEREUM(8, 20);

    private final int minPrice;
    private final int maxPrice;

    CoinType(int minPrice, int maxPrice) {
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }

    public Item getItem() {
        return switch (this) {
            case BITCOIN -> ModItems.BITCOIN.get();
            case ETHEREUM -> ModItems.ETHEREUM.get();
        };
    }

    public ItemStack createStack(int amount) {
        return new ItemStack(getItem(), amount);
    }

    public int getSerializedId() {
        return ordinal();
    }

    @Nullable
    public static CoinType fromId(int id) {
        if (id < 0 || id >= values().length) {
            return null;
        }
        return values()[id];
    }
}
