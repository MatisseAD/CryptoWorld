package fr.jachou.cryptoworld.item.custom;

import fr.jachou.cryptoworld.rig.RigPartStats;
import fr.jachou.cryptoworld.rig.RigPartType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RigPartItem extends Item {
    private final RigPartStats rigPartStats;

    public RigPartItem(Properties properties, RigPartStats rigPartStats) {
        super(properties);
        this.rigPartStats = rigPartStats;
    }

    public RigPartStats getRigPartStats() {
        return rigPartStats;
    }

    public RigPartType getRigPartType() {
        return rigPartStats.type();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        if (rigPartStats.computePower() > 0) {
            tooltipComponents.add(Component.translatable("tooltip.cryptoworld.compute_power", rigPartStats.computePower()));
        }
        if (rigPartStats.powerDraw() > 0) {
            tooltipComponents.add(Component.translatable("tooltip.cryptoworld.power_draw", rigPartStats.powerDraw()));
        }
        if (rigPartStats.coolingPower() > 0) {
            tooltipComponents.add(Component.translatable("tooltip.cryptoworld.cooling_power", rigPartStats.coolingPower()));
        }
        if (rigPartStats.powerCapacity() > 0) {
            tooltipComponents.add(Component.translatable("tooltip.cryptoworld.power_capacity", rigPartStats.powerCapacity()));
        }
        if (rigPartStats.stability() > 0) {
            tooltipComponents.add(Component.translatable("tooltip.cryptoworld.stability", rigPartStats.stability()));
        }
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
