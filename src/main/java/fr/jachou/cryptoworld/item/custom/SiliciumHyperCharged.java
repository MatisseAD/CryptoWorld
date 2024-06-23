package fr.jachou.cryptoworld.item.custom;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.Nullable;

public class SiliciumHyperCharged extends Item {

    private int burnTime = 0;
    
    public SiliciumHyperCharged(Properties pProperties, int pBurnTime) {
        super(pProperties);
        this.burnTime = pBurnTime;
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return this.burnTime;
    }

    @Override
    public boolean isFoil(ItemStack itemStack) {
        return true;
    }
}
