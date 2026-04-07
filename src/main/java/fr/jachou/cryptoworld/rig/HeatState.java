package fr.jachou.cryptoworld.rig;

import fr.jachou.cryptoworld.balance.CryptoBalanceConfig;

public enum HeatState {
    NORMAL,
    HOT,
    THROTTLED,
    CRITICAL;

    public static HeatState fromHeat(int heat) {
        if (heat >= CryptoBalanceConfig.HEAT_CRITICAL) {
            return CRITICAL;
        }
        if (heat >= CryptoBalanceConfig.HEAT_THROTTLED) {
            return THROTTLED;
        }
        if (heat >= CryptoBalanceConfig.HEAT_HOT) {
            return HOT;
        }
        return NORMAL;
    }
}
