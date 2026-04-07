package fr.jachou.cryptoworld.market;

import fr.jachou.cryptoworld.CryptoWorld;
import fr.jachou.cryptoworld.balance.CryptoBalanceConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerWalletSavedData extends SavedData {
    private static final String DATA_NAME = CryptoWorld.MODID + "_wallet";
    private static final SavedData.Factory<PlayerWalletSavedData> FACTORY =
            new SavedData.Factory<>(PlayerWalletSavedData::new, PlayerWalletSavedData::load, null);

    private final Map<UUID, AccountData> accounts = new HashMap<>();
    private long nextOrderId = 1L;
    private long nextPositionId = 1L;

    public static PlayerWalletSavedData get(ServerLevel level) {
        ServerLevel overworld = level.getServer().overworld();
        DimensionDataStorage storage = overworld.getDataStorage();
        return storage.computeIfAbsent(FACTORY, DATA_NAME);
    }

    public double getBalance(UUID playerId, CoinType coinType) {
        return getAccount(playerId).balances.getOrDefault(coinType, 0.0D);
    }

    public int getWholeBalance(UUID playerId, CoinType coinType) {
        return Mth.floor(getBalance(playerId, coinType));
    }

    public int getActiveOrderCount(UUID playerId) {
        return getAccount(playerId).openOrders.size();
    }

    public int getActivePositionCount(UUID playerId) {
        return getAccount(playerId).yieldPositions.size();
    }

    public int getMintedCollectibleCount(UUID playerId) {
        return getAccount(playerId).mintedCollectibles;
    }

    public void addBalance(UUID playerId, CoinType coinType, double amount) {
        if (amount <= 0.0D) {
            return;
        }
        AccountData account = getAccount(playerId);
        account.balances.put(coinType, account.balances.getOrDefault(coinType, 0.0D) + amount);
        setDirty();
    }

    public boolean spendBalance(UUID playerId, CoinType coinType, double amount) {
        if (amount <= 0.0D) {
            return true;
        }
        AccountData account = getAccount(playerId);
        double current = account.balances.getOrDefault(coinType, 0.0D);
        if (current + 1.0E-6D < amount) {
            return false;
        }
        account.balances.put(coinType, Math.max(0.0D, current - amount));
        setDirty();
        return true;
    }

    public int depositCoins(Player player, CoinType coinType, int maxAmount) {
        int deposited = removeCoins(player.getInventory(), coinType, maxAmount);
        if (deposited > 0) {
            addBalance(player.getUUID(), coinType, deposited);
        }
        return deposited;
    }

    public int withdrawCoins(Player player, CoinType coinType, int maxAmount) {
        int available = getWholeBalance(player.getUUID(), coinType);
        int amount = Math.min(available, maxAmount);
        if (amount <= 0 || !spendBalance(player.getUUID(), coinType, amount)) {
            return 0;
        }

        giveItem(player, coinType.createStack(amount));
        return amount;
    }

    public int placeLimitOrderBuy(Player player, ExchangeId exchangeId, CoinType coinType, int amount, int limitPrice) {
        int reserve = Math.max(1, amount * limitPrice);
        if (!removeEmeralds(player.getInventory(), reserve)) {
            return 0;
        }

        AccountData account = getAccount(player.getUUID());
        account.openOrders.add(OpenOrder.limitBuy(nextOrderId++, exchangeId, coinType, amount, limitPrice, reserve, player.level().getGameTime()));
        setDirty();
        return reserve;
    }

    public int placeLimitOrderSell(Player player, ExchangeId exchangeId, CoinType coinType, int amount, int limitPrice) {
        if (!spendBalance(player.getUUID(), coinType, amount)) {
            return 0;
        }

        AccountData account = getAccount(player.getUUID());
        account.openOrders.add(OpenOrder.limitSell(nextOrderId++, exchangeId, coinType, amount, limitPrice, amount, player.level().getGameTime()));
        setDirty();
        return amount;
    }

    public int cancelOldestOrder(Player player) {
        AccountData account = getAccount(player.getUUID());
        if (account.openOrders.isEmpty()) {
            return 0;
        }

        OpenOrder order = account.openOrders.remove(0);
        if (order.side == OrderSide.BUY) {
            giveEmeralds(player, order.reservedEmeralds);
            setDirty();
            return order.reservedEmeralds;
        }

        addBalance(player.getUUID(), order.coinType, order.reservedCoins);
        return order.reservedCoins;
    }

    public int settleOrders(ServerLevel level, Player player, CryptoMarketSavedData market, int feeDiscountPercent) {
        AccountData account = getAccount(player.getUUID());
        if (account.openOrders.isEmpty()) {
            return 0;
        }

        int executed = 0;
        Iterator<OpenOrder> iterator = account.openOrders.iterator();
        while (iterator.hasNext()) {
            OpenOrder order = iterator.next();
            CryptoMarketSavedData.TradeQuote quote = market.createQuote(level, order.exchangeId, order.coinType, order.side, order.quantity, feeDiscountPercent);
            boolean fill = order.side == OrderSide.BUY
                    ? quote.unitPrice() <= order.limitPrice
                    : quote.unitPrice() >= order.limitPrice;
            if (!fill) {
                continue;
            }

            if (order.side == OrderSide.BUY) {
                addBalance(player.getUUID(), order.coinType, order.quantity);
                int refund = Math.max(0, order.reservedEmeralds - quote.totalEmeralds());
                if (refund > 0) {
                    giveEmeralds(player, refund);
                }
            } else {
                giveEmeralds(player, quote.totalEmeralds());
            }

            executed++;
            iterator.remove();
        }

        if (executed > 0) {
            setDirty();
        }
        return executed;
    }

    public YieldPosition openStaking(Player player, double principal, double apr) {
        if (!spendBalance(player.getUUID(), CoinType.ETHEREUM, principal)) {
            return null;
        }
        AccountData account = getAccount(player.getUUID());
        YieldPosition position = new YieldPosition(nextPositionId++, YieldPosition.Kind.STAKING, CoinType.ETHEREUM,
                ExchangeId.NEXUS_ONE, principal, apr, player.level().getGameTime(), CryptoBalanceConfig.STAKE_LOCK_TICKS);
        account.yieldPositions.add(position);
        setDirty();
        return position;
    }

    public YieldPosition openLending(Player player, ExchangeId exchangeId, CoinType coinType, double principal, double apr) {
        if (!spendBalance(player.getUUID(), coinType, principal)) {
            return null;
        }
        AccountData account = getAccount(player.getUUID());
        YieldPosition position = new YieldPosition(nextPositionId++, YieldPosition.Kind.LENDING, coinType,
                exchangeId, principal, apr, player.level().getGameTime(), CryptoBalanceConfig.LEND_LOCK_TICKS);
        account.yieldPositions.add(position);
        setDirty();
        return position;
    }

    public double claimMaturedPositions(ServerLevel level, Player player) {
        AccountData account = getAccount(player.getUUID());
        if (account.yieldPositions.isEmpty()) {
            return 0.0D;
        }

        double credited = 0.0D;
        Iterator<YieldPosition> iterator = account.yieldPositions.iterator();
        while (iterator.hasNext()) {
            YieldPosition position = iterator.next();
            if (!position.isMature(level.getGameTime())) {
                continue;
            }

            double payout = calculatePayout(level, position, false);
            addBalance(player.getUUID(), position.coinType(), payout);
            credited += payout;
            iterator.remove();
        }
        if (credited > 0.0D) {
            setDirty();
        }
        return credited;
    }

    public double exitOldestPosition(ServerLevel level, Player player) {
        AccountData account = getAccount(player.getUUID());
        if (account.yieldPositions.isEmpty()) {
            return 0.0D;
        }

        YieldPosition position = account.yieldPositions.remove(0);
        double payout = calculatePayout(level, position, !position.isMature(level.getGameTime()));
        addBalance(player.getUUID(), position.coinType(), payout);
        setDirty();
        return payout;
    }

    public void incrementMintedCollectibles(UUID playerId) {
        AccountData account = getAccount(playerId);
        account.mintedCollectibles++;
        setDirty();
    }

    private double calculatePayout(ServerLevel level, YieldPosition position, boolean earlyExit) {
        long elapsed = Math.max(0L, level.getGameTime() - position.startedTick());
        double elapsedDays = elapsed / 24000.0D;
        double yield = position.principal() * position.apr() * (elapsedDays / 365.0D);
        double payout = position.principal() + yield;

        if (position.kind() == YieldPosition.Kind.STAKING && earlyExit) {
            payout *= CryptoBalanceConfig.getDepegPenalty(level);
        }
        if (position.kind() == YieldPosition.Kind.LENDING) {
            payout *= CryptoBalanceConfig.getExchangeFailureModifier(level, position.exchangeId().ordinal() * 131L);
        }
        return Math.max(0.0D, payout);
    }

    private AccountData getAccount(UUID playerId) {
        return accounts.computeIfAbsent(playerId, ignored -> new AccountData());
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag accountsTag = new ListTag();
        for (Map.Entry<UUID, AccountData> entry : accounts.entrySet()) {
            CompoundTag accountTag = new CompoundTag();
            accountTag.putUUID("player", entry.getKey());
            accountTag.put("data", entry.getValue().save());
            accountsTag.add(accountTag);
        }
        tag.put("accounts", accountsTag);
        tag.putLong("next_order_id", nextOrderId);
        tag.putLong("next_position_id", nextPositionId);
        return tag;
    }

    public static PlayerWalletSavedData load(CompoundTag tag) {
        PlayerWalletSavedData data = new PlayerWalletSavedData();
        ListTag accountsTag = tag.getList("accounts", Tag.TAG_COMPOUND);
        for (Tag entry : accountsTag) {
            CompoundTag accountTag = (CompoundTag) entry;
            UUID playerId = accountTag.getUUID("player");
            data.accounts.put(playerId, AccountData.load(accountTag.getCompound("data")));
        }
        data.nextOrderId = Math.max(1L, tag.getLong("next_order_id"));
        data.nextPositionId = Math.max(1L, tag.getLong("next_position_id"));
        return data;
    }

    private static int removeCoins(Inventory inventory, CoinType coinType, int maxAmount) {
        int remaining = maxAmount <= 0 ? Integer.MAX_VALUE : maxAmount;
        int removed = 0;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(coinType.getItem())) {
                continue;
            }

            int taken = Math.min(remaining, stack.getCount());
            stack.shrink(taken);
            removed += taken;
            remaining -= taken;
            if (stack.isEmpty()) {
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
        inventory.setChanged();
        return removed;
    }

    private static boolean removeEmeralds(Inventory inventory, int amount) {
        int available = 0;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(Items.EMERALD)) {
                available += stack.getCount();
            }
        }
        if (available < amount) {
            return false;
        }

        int remaining = amount;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(Items.EMERALD)) {
                continue;
            }
            int taken = Math.min(remaining, stack.getCount());
            stack.shrink(taken);
            remaining -= taken;
            if (stack.isEmpty()) {
                inventory.setItem(slot, ItemStack.EMPTY);
            }
        }
        inventory.setChanged();
        return remaining <= 0;
    }

    private static void giveEmeralds(Player player, int amount) {
        giveItem(player, new ItemStack(Items.EMERALD, amount));
    }

    private static void giveItem(Player player, ItemStack stack) {
        ItemStack remaining = stack.copy();
        while (!remaining.isEmpty()) {
            int split = Math.min(remaining.getMaxStackSize(), remaining.getCount());
            ItemStack chunk = remaining.split(split);
            if (!player.addItem(chunk)) {
                player.drop(chunk, false);
            }
        }
    }

    private static final class AccountData {
        private final EnumMap<CoinType, Double> balances = new EnumMap<>(CoinType.class);
        private final List<OpenOrder> openOrders = new ArrayList<>();
        private final List<YieldPosition> yieldPositions = new ArrayList<>();
        private int mintedCollectibles;

        private CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            CompoundTag balancesTag = new CompoundTag();
            for (CoinType coinType : CoinType.values()) {
                balancesTag.putDouble(coinType.name().toLowerCase(), balances.getOrDefault(coinType, 0.0D));
            }
            tag.put("balances", balancesTag);

            ListTag ordersTag = new ListTag();
            for (OpenOrder order : openOrders) {
                ordersTag.add(order.save());
            }
            tag.put("orders", ordersTag);

            ListTag positionsTag = new ListTag();
            for (YieldPosition position : yieldPositions) {
                positionsTag.add(position.save());
            }
            tag.put("positions", positionsTag);
            tag.putInt("minted_collectibles", mintedCollectibles);
            return tag;
        }

        private static AccountData load(CompoundTag tag) {
            AccountData account = new AccountData();
            CompoundTag balancesTag = tag.getCompound("balances");
            for (CoinType coinType : CoinType.values()) {
                account.balances.put(coinType, balancesTag.getDouble(coinType.name().toLowerCase()));
            }

            ListTag ordersTag = tag.getList("orders", Tag.TAG_COMPOUND);
            for (Tag entry : ordersTag) {
                account.openOrders.add(OpenOrder.load((CompoundTag) entry));
            }

            ListTag positionsTag = tag.getList("positions", Tag.TAG_COMPOUND);
            for (Tag entry : positionsTag) {
                account.yieldPositions.add(YieldPosition.load((CompoundTag) entry));
            }
            account.mintedCollectibles = tag.getInt("minted_collectibles");
            return account;
        }
    }

    private static final class OpenOrder {
        private final long id;
        private final ExchangeId exchangeId;
        private final CoinType coinType;
        private final OrderSide side;
        private final int quantity;
        private final int limitPrice;
        private final int reservedEmeralds;
        private final int reservedCoins;
        private final long createdTick;

        private OpenOrder(long id, ExchangeId exchangeId, CoinType coinType, OrderSide side, int quantity,
                          int limitPrice, int reservedEmeralds, int reservedCoins, long createdTick) {
            this.id = id;
            this.exchangeId = exchangeId;
            this.coinType = coinType;
            this.side = side;
            this.quantity = quantity;
            this.limitPrice = limitPrice;
            this.reservedEmeralds = reservedEmeralds;
            this.reservedCoins = reservedCoins;
            this.createdTick = createdTick;
        }

        private static OpenOrder limitBuy(long id, ExchangeId exchangeId, CoinType coinType, int quantity,
                                          int limitPrice, int reservedEmeralds, long createdTick) {
            return new OpenOrder(id, exchangeId, coinType, OrderSide.BUY, quantity, limitPrice, reservedEmeralds, 0, createdTick);
        }

        private static OpenOrder limitSell(long id, ExchangeId exchangeId, CoinType coinType, int quantity,
                                           int limitPrice, int reservedCoins, long createdTick) {
            return new OpenOrder(id, exchangeId, coinType, OrderSide.SELL, quantity, limitPrice, 0, reservedCoins, createdTick);
        }

        private CompoundTag save() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("id", id);
            tag.putInt("exchange", exchangeId.getSerializedId());
            tag.putInt("coin", coinType.getSerializedId());
            tag.putString("side", side.name());
            tag.putInt("quantity", quantity);
            tag.putInt("limit_price", limitPrice);
            tag.putInt("reserved_emeralds", reservedEmeralds);
            tag.putInt("reserved_coins", reservedCoins);
            tag.putLong("created_tick", createdTick);
            return tag;
        }

        private static OpenOrder load(CompoundTag tag) {
            CoinType coinType = CoinType.fromId(tag.getInt("coin"));
            if (coinType == null) {
                coinType = CoinType.BITCOIN;
            }
            return new OpenOrder(
                    tag.getLong("id"),
                    ExchangeId.fromId(tag.getInt("exchange")),
                    coinType,
                    OrderSide.valueOf(tag.getString("side")),
                    tag.getInt("quantity"),
                    tag.getInt("limit_price"),
                    tag.getInt("reserved_emeralds"),
                    tag.getInt("reserved_coins"),
                    tag.getLong("created_tick")
            );
        }
    }
}
