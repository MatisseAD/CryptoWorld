package fr.jachou.cryptoworld.market;

import net.minecraft.nbt.CompoundTag;

public class YieldPosition {
    public enum Kind {
        STAKING,
        LENDING
    }

    private final long id;
    private final Kind kind;
    private final CoinType coinType;
    private final ExchangeId exchangeId;
    private final double principal;
    private final double apr;
    private final long startedTick;
    private final long lockDuration;

    public YieldPosition(long id, Kind kind, CoinType coinType, ExchangeId exchangeId,
                         double principal, double apr, long startedTick, long lockDuration) {
        this.id = id;
        this.kind = kind;
        this.coinType = coinType;
        this.exchangeId = exchangeId;
        this.principal = principal;
        this.apr = apr;
        this.startedTick = startedTick;
        this.lockDuration = lockDuration;
    }

    public long id() {
        return id;
    }

    public Kind kind() {
        return kind;
    }

    public CoinType coinType() {
        return coinType;
    }

    public ExchangeId exchangeId() {
        return exchangeId;
    }

    public double principal() {
        return principal;
    }

    public double apr() {
        return apr;
    }

    public long startedTick() {
        return startedTick;
    }

    public long lockDuration() {
        return lockDuration;
    }

    public boolean isMature(long gameTime) {
        return gameTime - startedTick >= lockDuration;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("id", id);
        tag.putString("kind", kind.name());
        tag.putInt("coin", coinType.getSerializedId());
        tag.putInt("exchange", exchangeId.getSerializedId());
        tag.putDouble("principal", principal);
        tag.putDouble("apr", apr);
        tag.putLong("started_tick", startedTick);
        tag.putLong("lock_duration", lockDuration);
        return tag;
    }

    public static YieldPosition load(CompoundTag tag) {
        Kind kind = Kind.valueOf(tag.getString("kind"));
        CoinType coinType = CoinType.fromId(tag.getInt("coin"));
        if (coinType == null) {
            coinType = CoinType.BITCOIN;
        }
        return new YieldPosition(
                tag.getLong("id"),
                kind,
                coinType,
                ExchangeId.fromId(tag.getInt("exchange")),
                tag.getDouble("principal"),
                tag.getDouble("apr"),
                tag.getLong("started_tick"),
                tag.getLong("lock_duration")
        );
    }
}
