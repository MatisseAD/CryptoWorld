package fr.jachou.cryptoworld.market;

public enum ExchangeId {
    NEXUS_ONE(2, 2, 26, 1),
    QUANTUM_X(4, 3, 18, 2),
    VOLT_MARKET(6, 5, 12, 4);

    private final int feePercent;
    private final int baseSpread;
    private final int liquidity;
    private final int volatility;

    ExchangeId(int feePercent, int baseSpread, int liquidity, int volatility) {
        this.feePercent = feePercent;
        this.baseSpread = baseSpread;
        this.liquidity = liquidity;
        this.volatility = volatility;
    }

    public int getFeePercent() {
        return feePercent;
    }

    public int getBaseSpread() {
        return baseSpread;
    }

    public int getLiquidity() {
        return liquidity;
    }

    public int getVolatility() {
        return volatility;
    }

    public int getSerializedId() {
        return ordinal();
    }

    public static ExchangeId fromId(int id) {
        if (id < 0 || id >= values().length) {
            return NEXUS_ONE;
        }
        return values()[id];
    }
}
