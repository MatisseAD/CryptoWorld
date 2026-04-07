package fr.jachou.cryptoworld.rig;

import fr.jachou.cryptoworld.market.CoinType;
import org.jetbrains.annotations.Nullable;

public record RigPartStats(
        RigPartType type,
        @Nullable CoinType coinType,
        int computePower,
        int powerDraw,
        int heatGeneration,
        int coolingPower,
        int stability,
        int powerCapacity,
        int gridBoost,
        int durabilityDamagePerCycle
) {
    public static final RigPartStats EMPTY =
            new RigPartStats(RigPartType.UNKNOWN, null, 0, 0, 0, 0, 0, 0, 0, 0);
}
