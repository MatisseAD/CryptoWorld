package fr.jachou.cryptoworld.rig;

import net.minecraft.util.Mth;

public enum PoolId {
    SOLO(0, 140, 1),
    STEADY_POOL(3, 100, 2),
    HIGH_FEE_POOL(6, 75, 4);

    private final int feePercent;
    private final int variancePercent;
    private final int payoutIntervalCycles;

    PoolId(int feePercent, int variancePercent, int payoutIntervalCycles) {
        this.feePercent = feePercent;
        this.variancePercent = variancePercent;
        this.payoutIntervalCycles = payoutIntervalCycles;
    }

    public int getFeePercent() {
        return feePercent;
    }

    public int getVariancePercent() {
        return variancePercent;
    }

    public int getPayoutIntervalCycles() {
        return payoutIntervalCycles;
    }

    public int applyVariance(int baseAmount, long salt) {
        if (baseAmount <= 0) {
            return 0;
        }
        int swing = (int) Math.floorMod(salt, variancePercent + 1L);
        int modifier = variancePercent - swing;
        return Math.max(1, Mth.floor(baseAmount * modifier / 100.0F));
    }

    public int getSerializedId() {
        return ordinal();
    }

    public static PoolId fromId(int id) {
        if (id < 0 || id >= values().length) {
            return SOLO;
        }
        return values()[id];
    }
}
