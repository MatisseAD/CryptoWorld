package fr.jachou.cryptoworld.market;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.balance.CryptoBalanceConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

public class CryptoMarketSavedData extends SavedData {
    private static final String DATA_NAME = CryptoWorld.MODID + "_market";
    private static final long UPDATE_INTERVAL_TICKS = 2400L;
    private static final SavedData.Factory<CryptoMarketSavedData> FACTORY =
            new SavedData.Factory<>(CryptoMarketSavedData::new, CryptoMarketSavedData::load, null);

    private int bitcoinPrice = 20;
    private int ethereumPrice = 12;
    private long lastProcessedStep = 0L;

    public static CryptoMarketSavedData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(FACTORY, DATA_NAME);
    }

    public static long getUpdateIntervalTicks() {
        return UPDATE_INTERVAL_TICKS;
    }

    public int getPrice(ServerLevel level, CoinType coinType) {
        catchUp(level);
        return switch (coinType) {
            case BITCOIN -> bitcoinPrice;
            case ETHEREUM -> ethereumPrice;
        };
    }

    public int getExchangeMidPrice(ServerLevel level, ExchangeId exchangeId, CoinType coinType) {
        int fair = getPrice(level, coinType);
        long step = level.getGameTime() / UPDATE_INTERVAL_TICKS;
        long seed = level.getSeed()
                ^ (step * 4738221L)
                ^ ((long) exchangeId.ordinal() * 131231L)
                ^ ((long) coinType.ordinal() * 66713L);
        RandomSource random = RandomSource.create(seed);
        int swing = random.nextInt(exchangeId.getVolatility() * 2 + 1) - exchangeId.getVolatility();
        return Math.max(1, fair + swing);
    }

    public TradeQuote createQuote(ServerLevel level, ExchangeId exchangeId, CoinType coinType,
                                  OrderSide side, int amount, int feeDiscountPercent) {
        int unitMid = getExchangeMidPrice(level, exchangeId, coinType);
        int spread = exchangeId.getBaseSpread();
        double slippage = Math.max(0, amount - 1) / (double) exchangeId.getLiquidity();
        double baseUnitPrice = side == OrderSide.BUY
                ? unitMid + spread + slippage
                : Math.max(1, unitMid - spread - slippage);
        double feeMultiplier = 1.0D + Math.max(0, exchangeId.getFeePercent() - feeDiscountPercent) / 100.0D;
        double total = baseUnitPrice * amount;
        total = side == OrderSide.BUY ? total * feeMultiplier : total / feeMultiplier;

        int roundedUnit = side == OrderSide.BUY ? Mth.ceil(baseUnitPrice) : Math.max(1, Mth.floor(baseUnitPrice));
        int roundedTotal = side == OrderSide.BUY ? Mth.ceil(total) : Math.max(1, Mth.floor(total));
        return new TradeQuote(exchangeId, coinType, side, amount, roundedUnit, roundedTotal);
    }

    public ArbitrageSnapshot getArbitrageSnapshot(ServerLevel level, CoinType coinType, int amount, int feeDiscountPercent) {
        TradeQuote bestBuy = null;
        TradeQuote bestSell = null;

        for (ExchangeId exchangeId : ExchangeId.values()) {
            TradeQuote buy = createQuote(level, exchangeId, coinType, OrderSide.BUY, amount, feeDiscountPercent);
            TradeQuote sell = createQuote(level, exchangeId, coinType, OrderSide.SELL, amount, feeDiscountPercent);
            if (bestBuy == null || buy.totalEmeralds() < bestBuy.totalEmeralds()) {
                bestBuy = buy;
            }
            if (bestSell == null || sell.totalEmeralds() > bestSell.totalEmeralds()) {
                bestSell = sell;
            }
        }

        int net = bestSell == null || bestBuy == null ? 0 : bestSell.totalEmeralds() - bestBuy.totalEmeralds();
        return new ArbitrageSnapshot(bestBuy, bestSell, net);
    }

    public double getStakingApr(ServerLevel level) {
        long seed = level.getSeed() ^ (level.getGameTime() / UPDATE_INTERVAL_TICKS) ^ 0x88EL;
        int drift = (int) Math.floorMod(seed, 5L) - 2;
        return CryptoBalanceConfig.getStakingApr() + (drift * 0.005D);
    }

    public double getLendingApr(ServerLevel level, ExchangeId exchangeId, CoinType coinType) {
        long seed = level.getSeed()
                ^ (level.getGameTime() / 1800L)
                ^ ((long) exchangeId.ordinal() * 19139L)
                ^ ((long) coinType.ordinal() * 7177L);
        int drift = (int) Math.floorMod(seed, 7L) - 3;
        return CryptoBalanceConfig.getBaseYieldApr(coinType) + (exchangeId.getVolatility() * 0.005D) + (drift * 0.003D);
    }

    public int getExchangeHealth(ServerLevel level, ExchangeId exchangeId) {
        long seed = level.getSeed() ^ (level.getGameTime() / 2200L) ^ ((long) exchangeId.ordinal() * 8879L);
        int drift = (int) Math.floorMod(seed, 24L);
        return Mth.clamp(76 + drift - exchangeId.getVolatility() * 4, 45, 98);
    }

    private void catchUp(ServerLevel level) {
        long currentStep = level.getGameTime() / UPDATE_INTERVAL_TICKS;
        if (currentStep <= lastProcessedStep) {
            return;
        }

        while (lastProcessedStep < currentStep) {
            lastProcessedStep++;
            bitcoinPrice = nextPrice(level, CoinType.BITCOIN, bitcoinPrice, lastProcessedStep);
            ethereumPrice = nextPrice(level, CoinType.ETHEREUM, ethereumPrice, lastProcessedStep);
        }

        setDirty();
    }

    private int nextPrice(ServerLevel level, CoinType coinType, int currentPrice, long step) {
        long seed = level.getSeed() ^ (step * 341873128712L) ^ ((long) coinType.ordinal() * 132897987541L);
        RandomSource random = RandomSource.create(seed);
        int delta = random.nextInt(5) - 2;
        int next = Mth.clamp(currentPrice + delta, coinType.getMinPrice(), coinType.getMaxPrice());

        if (next == currentPrice) {
            if (currentPrice == coinType.getMinPrice()) {
                next = Math.min(currentPrice + 1, coinType.getMaxPrice());
            } else if (currentPrice == coinType.getMaxPrice()) {
                next = Math.max(currentPrice - 1, coinType.getMinPrice());
            }
        }

        return next;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putInt("bitcoin_price", bitcoinPrice);
        tag.putInt("ethereum_price", ethereumPrice);
        tag.putLong("last_processed_step", lastProcessedStep);
        return tag;
    }

    public static CryptoMarketSavedData load(CompoundTag tag) {
        CryptoMarketSavedData data = new CryptoMarketSavedData();
        data.bitcoinPrice = tag.getInt("bitcoin_price");
        data.ethereumPrice = tag.getInt("ethereum_price");
        data.lastProcessedStep = tag.getLong("last_processed_step");
        if (data.bitcoinPrice <= 0) {
            data.bitcoinPrice = 20;
        }
        if (data.ethereumPrice <= 0) {
            data.ethereumPrice = 12;
        }
        return data;
    }

    public record TradeQuote(ExchangeId exchangeId, CoinType coinType, OrderSide side, int amount,
                             int unitPrice, int totalEmeralds) {
    }

    public record ArbitrageSnapshot(TradeQuote bestBuy, TradeQuote bestSell, int netSpread) {
    }
}
