package fr.jachou.cryptoworld.balance;

import fr.jachou.cryptoworld.collectible.CollectibleKind;
import fr.jachou.cryptoworld.market.CoinType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;

public final class CryptoBalanceConfig {
    public static final int DATACENTER_RADIUS = 16;
    public static final int MINER_DATA_VERSION = 2;
    public static final int SERVER_DATA_VERSION = 1;
    public static final int AUTONOMOUS_COMPUTE_SLOTS = 1;
    public static final int DATACENTER_COMPUTE_SLOTS = 4;
    public static final int BASE_OPERATION_TIME = 200;
    public static final int MIN_OPERATION_TIME = 60;
    public static final int HEAT_HOT = 35;
    public static final int HEAT_THROTTLED = 65;
    public static final int HEAT_CRITICAL = 90;
    public static final int BASE_GRID_IMPORT = 18;
    public static final int GRID_UPGRADE_BONUS = 18;
    public static final int GRID_BILL_RATE = 1;
    public static final int BATTERY_BLOCK_CAPACITY = 3600;
    public static final int BATTERY_BLOCK_DISCHARGE = 18;
    public static final int SOLAR_MAX_OUTPUT = 14;
    public static final int WIND_BASE_OUTPUT = 12;
    public static final int VENTILATION_BONUS = 8;
    public static final int WATER_COOLING_BONUS = 18;
    public static final int AUTONOMOUS_POWER_CAP = 20;
    public static final int BACKUP_CELL_POWER = 1600;
    public static final int LIMIT_ORDER_OFFSET = 1;
    public static final int STAKE_LOCK_TICKS = 24_000;
    public static final int LEND_LOCK_TICKS = 18_000;
    public static final int PLAYER_ACTION_AMOUNT_SMALL = 4;
    public static final int PLAYER_ACTION_AMOUNT_MEDIUM = 8;

    private CryptoBalanceConfig() {
    }

    public static int getSolarOutput(ServerLevel level) {
        if (!level.isDay()) {
            return 0;
        }
        if (level.isRaining()) {
            return 6;
        }
        return SOLAR_MAX_OUTPUT;
    }

    public static int getWindOutput(ServerLevel level, long salt) {
        long seed = level.getSeed() ^ (salt * 92821L) ^ (level.getGameTime() / 200L);
        int drift = (int) Math.floorMod(seed, 7L) - 3;
        return Math.max(4, WIND_BASE_OUTPUT + drift);
    }

    public static int getMintCostCoins(CollectibleKind kind) {
        return switch (kind) {
            case MACHINE_SKIN -> 3;
            case OPERATOR_BADGE -> 4;
        };
    }

    public static int getMintCostIngot(CollectibleKind kind) {
        return switch (kind) {
            case MACHINE_SKIN -> 2;
            case OPERATOR_BADGE -> 3;
        };
    }

    public static int getMintCostSilicium(CollectibleKind kind) {
        return switch (kind) {
            case MACHINE_SKIN -> 2;
            case OPERATOR_BADGE -> 1;
        };
    }

    public static double getBaseYieldApr(CoinType coinType) {
        return switch (coinType) {
            case BITCOIN -> 0.06D;
            case ETHEREUM -> 0.09D;
        };
    }

    public static double getStakingApr() {
        return 0.12D;
    }

    public static double getDepegPenalty(ServerLevel level) {
        long seed = level.getSeed() ^ (level.getGameTime() / 2400L) ^ 0x5A17L;
        int bucket = (int) Math.floorMod(seed, 100L);
        if (bucket < 12) {
            return 0.82D;
        }
        if (bucket < 28) {
            return 0.90D;
        }
        return 1.0D;
    }

    public static double getExchangeFailureModifier(ServerLevel level, long exchangeSalt) {
        long seed = level.getSeed() ^ exchangeSalt ^ (level.getGameTime() / 3200L);
        int bucket = (int) Math.floorMod(seed, 100L);
        if (bucket < 8) {
            return 0.7D;
        }
        if (bucket < 20) {
            return 0.85D;
        }
        return 1.0D;
    }

    public static int clampHeat(int heat) {
        return Mth.clamp(heat, 0, 100);
    }
}
