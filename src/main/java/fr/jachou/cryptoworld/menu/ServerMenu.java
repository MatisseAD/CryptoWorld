package fr.jachou.cryptoworld.menu;

import fr.jachou.cryptoworld.balance.CryptoBalanceConfig;
import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.blockentity.ServerBlockEntity;
import fr.jachou.cryptoworld.collectible.CollectibleKind;
import fr.jachou.cryptoworld.item.ModItems;
import fr.jachou.cryptoworld.item.custom.CollectibleItem;
import fr.jachou.cryptoworld.market.CoinType;
import fr.jachou.cryptoworld.market.CryptoMarketSavedData;
import fr.jachou.cryptoworld.market.ExchangeId;
import fr.jachou.cryptoworld.market.OrderSide;
import fr.jachou.cryptoworld.market.PlayerWalletSavedData;
import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ServerMenu extends AbstractContainerMenu {
    public static final int TAB_OVERVIEW = 0;
    public static final int TAB_POWER = 1;
    public static final int TAB_EXCHANGE = 2;
    public static final int TAB_YIELD = 3;
    public static final int TAB_CONTRACTS = 4;

    private static final int SLOT_COUNT = 3;
    private static final int PLAYER_INVENTORY_START = SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;

    private static final int DATA_TAB = 0;
    private static final int DATA_SELECTED_EXCHANGE = 1;
    private static final int DATA_FAIR_BTC = 2;
    private static final int DATA_FAIR_ETH = 3;
    private static final int DATA_WALLET_BTC = 4;
    private static final int DATA_WALLET_ETH = 5;
    private static final int DATA_BILL = 6;
    private static final int DATA_GRID_CAP = 7;
    private static final int DATA_BATTERY = 8;
    private static final int DATA_AVAILABLE = 9;
    private static final int DATA_RIGS = 10;
    private static final int DATA_SOLAR = 11;
    private static final int DATA_WIND = 12;
    private static final int DATA_BROWNOUT = 13;
    private static final int DATA_BEST_BUY_EX = 14;
    private static final int DATA_BEST_SELL_EX = 15;
    private static final int DATA_ARB_SPREAD = 16;
    private static final int DATA_ORDERS = 17;
    private static final int DATA_POSITIONS = 18;
    private static final int DATA_MINTED = 19;
    private static final int DATA_STAKE_APY = 20;
    private static final int DATA_LEND_APY = 21;
    private static final int DATA_EXCHANGE_HEALTH = 22;
    private static final int DATA_COUNT = 23;

    private final ServerBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;
    private final Inventory playerInventory;

    public ServerMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, (ServerBlockEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(DATA_COUNT));
    }

    public ServerMenu(int id, Inventory inventory, ServerBlockEntity entity) {
        this(id, inventory, entity, new SimpleContainerData(DATA_COUNT));
    }

    private ServerMenu(int id, Inventory inventory, ServerBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.SERVER_MENU.get(), id);
        this.blockEntity = entity;
        this.data = data;
        this.playerInventory = inventory;
        this.access = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());

        checkContainerDataCount(data, DATA_COUNT);

        // Machine slots inside the side panel (right side of GUI)
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), ServerBlockEntity.GRID_SLOT_1, 176, 30));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), ServerBlockEntity.GRID_SLOT_2, 194, 30));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), ServerBlockEntity.BADGE_SLOT, 185, 52));

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
        addDataSlots(data);

        if (inventory.player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            refreshData(serverLevel, inventory.player, PlayerWalletSavedData.get(serverLevel), CryptoMarketSavedData.get(serverLevel));
        }
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (this.blockEntity.getLevel() != null && !this.blockEntity.getLevel().isClientSide && this.getCarried().isEmpty()) {
            Player player = this.playerInventory.player;
            if (player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
                PlayerWalletSavedData wallet = PlayerWalletSavedData.get(serverLevel);
                CryptoMarketSavedData market = CryptoMarketSavedData.get(serverLevel);
                wallet.settleOrders(serverLevel, player, market, blockEntity.getFeeDiscountPercent());
                refreshData(serverLevel, player, wallet, market);
            }
        }
    }

    private void refreshData(net.minecraft.server.level.ServerLevel level, Player player,
                             PlayerWalletSavedData wallet, CryptoMarketSavedData market) {
        ExchangeId exchangeId = ExchangeId.fromId(data.get(DATA_SELECTED_EXCHANGE));
        CryptoMarketSavedData.ArbitrageSnapshot arbitrage = market.getArbitrageSnapshot(level, CoinType.BITCOIN, CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL, blockEntity.getFeeDiscountPercent());

        data.set(DATA_FAIR_BTC, market.getPrice(level, CoinType.BITCOIN));
        data.set(DATA_FAIR_ETH, market.getPrice(level, CoinType.ETHEREUM));
        data.set(DATA_WALLET_BTC, wallet.getWholeBalance(player.getUUID(), CoinType.BITCOIN));
        data.set(DATA_WALLET_ETH, wallet.getWholeBalance(player.getUUID(), CoinType.ETHEREUM));
        data.set(DATA_BILL, blockEntity.getBillDue());
        data.set(DATA_GRID_CAP, blockEntity.getGridImportCap());
        data.set(DATA_BATTERY, blockEntity.getStoredBatteryPower());
        data.set(DATA_AVAILABLE, blockEntity.getAvailablePowerDisplay());
        data.set(DATA_RIGS, blockEntity.getRigCount());
        data.set(DATA_SOLAR, blockEntity.getSolarCount());
        data.set(DATA_WIND, blockEntity.getWindCount());
        data.set(DATA_BROWNOUT, blockEntity.getAvailablePowerDisplay() <= 0 ? 1 : 0);
        data.set(DATA_BEST_BUY_EX, arbitrage.bestBuy() == null ? 0 : arbitrage.bestBuy().exchangeId().getSerializedId());
        data.set(DATA_BEST_SELL_EX, arbitrage.bestSell() == null ? 0 : arbitrage.bestSell().exchangeId().getSerializedId());
        data.set(DATA_ARB_SPREAD, arbitrage.netSpread());
        data.set(DATA_ORDERS, wallet.getActiveOrderCount(player.getUUID()));
        data.set(DATA_POSITIONS, wallet.getActivePositionCount(player.getUUID()));
        data.set(DATA_MINTED, wallet.getMintedCollectibleCount(player.getUUID()));
        data.set(DATA_STAKE_APY, (int) Math.round(market.getStakingApr(level) * 100));
        data.set(DATA_LEND_APY, (int) Math.round(market.getLendingApr(level, exchangeId, CoinType.BITCOIN) * 100));
        data.set(DATA_EXCHANGE_HEALTH, market.getExchangeHealth(level, exchangeId));
    }

    public int getCurrentTab() {
        return data.get(DATA_TAB);
    }

    public int getSelectedExchangeId() {
        return data.get(DATA_SELECTED_EXCHANGE);
    }

    public ExchangeId getSelectedExchange() {
        return ExchangeId.fromId(getSelectedExchangeId());
    }

    public int getFairPrice(CoinType coinType) {
        return data.get(coinType == CoinType.BITCOIN ? DATA_FAIR_BTC : DATA_FAIR_ETH);
    }

    public int getWalletBalance(CoinType coinType) {
        return data.get(coinType == CoinType.BITCOIN ? DATA_WALLET_BTC : DATA_WALLET_ETH);
    }

    public int getBillDue() {
        return data.get(DATA_BILL);
    }

    public int getGridCap() {
        return data.get(DATA_GRID_CAP);
    }

    public int getBatteryStored() {
        return data.get(DATA_BATTERY);
    }

    public int getAvailablePower() {
        return data.get(DATA_AVAILABLE);
    }

    public int getRigCount() {
        return data.get(DATA_RIGS);
    }

    public int getSolarCount() {
        return data.get(DATA_SOLAR);
    }

    public int getWindCount() {
        return data.get(DATA_WIND);
    }

    public int getBestBuyExchangeId() {
        return data.get(DATA_BEST_BUY_EX);
    }

    public int getBestSellExchangeId() {
        return data.get(DATA_BEST_SELL_EX);
    }

    public int getArbitrageSpread() {
        return data.get(DATA_ARB_SPREAD);
    }

    public int getActiveOrders() {
        return data.get(DATA_ORDERS);
    }

    public int getActivePositions() {
        return data.get(DATA_POSITIONS);
    }

    public int getMintedCollectibles() {
        return data.get(DATA_MINTED);
    }

    public int getStakeApyPercent() {
        return data.get(DATA_STAKE_APY);
    }

    public int getLendApyPercent() {
        return data.get(DATA_LEND_APY);
    }

    public int getExchangeHealth() {
        return data.get(DATA_EXCHANGE_HEALTH);
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (player == null || !(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return false;
        }

        PlayerWalletSavedData wallet = PlayerWalletSavedData.get(serverLevel);
        CryptoMarketSavedData market = CryptoMarketSavedData.get(serverLevel);

        if (id >= 0 && id <= TAB_CONTRACTS) {
            data.set(DATA_TAB, id);
            refreshData(serverLevel, player, wallet, market);
            return true;
        }

        switch (id) {
            case 50 -> data.set(DATA_SELECTED_EXCHANGE, (getSelectedExchangeId() + ExchangeId.values().length - 1) % ExchangeId.values().length);
            case 51 -> data.set(DATA_SELECTED_EXCHANGE, (getSelectedExchangeId() + 1) % ExchangeId.values().length);
            case 100 -> wallet.depositCoins(player, CoinType.BITCOIN, 0);
            case 101 -> wallet.depositCoins(player, CoinType.ETHEREUM, 0);
            case 102 -> wallet.withdrawCoins(player, CoinType.BITCOIN, CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_MEDIUM);
            case 103 -> wallet.withdrawCoins(player, CoinType.ETHEREUM, CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_MEDIUM);
            case 104 -> handleMarketBuy(player, wallet, market, getSelectedExchange(), CoinType.BITCOIN);
            case 105 -> handleMarketSell(player, wallet, market, getSelectedExchange(), CoinType.BITCOIN);
            case 106 -> handleMarketBuy(player, wallet, market, getSelectedExchange(), CoinType.ETHEREUM);
            case 107 -> handleMarketSell(player, wallet, market, getSelectedExchange(), CoinType.ETHEREUM);
            case 108 -> wallet.placeLimitOrderBuy(player, getSelectedExchange(), CoinType.BITCOIN,
                    CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL,
                    Math.max(1, market.getExchangeMidPrice(serverLevel, getSelectedExchange(), CoinType.BITCOIN) - CryptoBalanceConfig.LIMIT_ORDER_OFFSET));
            case 109 -> wallet.placeLimitOrderSell(player, getSelectedExchange(), CoinType.BITCOIN,
                    CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_MEDIUM,
                    market.getExchangeMidPrice(serverLevel, getSelectedExchange(), CoinType.BITCOIN) + CryptoBalanceConfig.LIMIT_ORDER_OFFSET);
            case 110 -> wallet.placeLimitOrderBuy(player, getSelectedExchange(), CoinType.ETHEREUM,
                    CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL,
                    Math.max(1, market.getExchangeMidPrice(serverLevel, getSelectedExchange(), CoinType.ETHEREUM) - CryptoBalanceConfig.LIMIT_ORDER_OFFSET));
            case 111 -> wallet.placeLimitOrderSell(player, getSelectedExchange(), CoinType.ETHEREUM,
                    CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_MEDIUM,
                    market.getExchangeMidPrice(serverLevel, getSelectedExchange(), CoinType.ETHEREUM) + CryptoBalanceConfig.LIMIT_ORDER_OFFSET);
            case 112 -> wallet.cancelOldestOrder(player);
            case 120 -> blockEntity.payBill(player);
            case 130 -> wallet.openStaking(player, CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL, market.getStakingApr(serverLevel));
            case 131 -> wallet.openLending(player, getSelectedExchange(), CoinType.BITCOIN, CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL,
                    market.getLendingApr(serverLevel, getSelectedExchange(), CoinType.BITCOIN));
            case 132 -> wallet.openLending(player, getSelectedExchange(), CoinType.ETHEREUM, CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL,
                    market.getLendingApr(serverLevel, getSelectedExchange(), CoinType.ETHEREUM));
            case 133 -> wallet.exitOldestPosition(serverLevel, player);
            case 134 -> wallet.claimMaturedPositions(serverLevel, player);
            case 140 -> mintCollectible(serverLevel, player, wallet, CollectibleKind.MACHINE_SKIN);
            case 141 -> mintCollectible(serverLevel, player, wallet, CollectibleKind.OPERATOR_BADGE);
            default -> {
                return false;
            }
        }

        refreshData(serverLevel, player, wallet, market);
        return true;
    }

    private void handleMarketBuy(Player player, PlayerWalletSavedData wallet, CryptoMarketSavedData market,
                                 ExchangeId exchangeId, CoinType coinType) {
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        CryptoMarketSavedData.TradeQuote quote = market.createQuote(serverLevel, exchangeId, coinType,
                OrderSide.BUY, CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL, blockEntity.getFeeDiscountPercent());
        if (!takeItem(player, net.minecraft.world.item.Items.EMERALD, quote.totalEmeralds())) {
            return;
        }
        wallet.addBalance(player.getUUID(), coinType, quote.amount());
    }

    private void handleMarketSell(Player player, PlayerWalletSavedData wallet, CryptoMarketSavedData market,
                                  ExchangeId exchangeId, CoinType coinType) {
        if (!(player.level() instanceof net.minecraft.server.level.ServerLevel serverLevel)) {
            return;
        }
        int amount = CryptoBalanceConfig.PLAYER_ACTION_AMOUNT_SMALL;
        if (!wallet.spendBalance(player.getUUID(), coinType, amount)) {
            return;
        }
        CryptoMarketSavedData.TradeQuote quote = market.createQuote(serverLevel, exchangeId, coinType,
                OrderSide.SELL, amount, blockEntity.getFeeDiscountPercent());
        giveItem(player, new ItemStack(net.minecraft.world.item.Items.EMERALD, quote.totalEmeralds()));
    }

    private void mintCollectible(net.minecraft.server.level.ServerLevel level, Player player,
                                 PlayerWalletSavedData wallet, CollectibleKind kind) {
        CoinType requiredCoin = kind == CollectibleKind.MACHINE_SKIN ? CoinType.ETHEREUM : CoinType.BITCOIN;
        if (!wallet.spendBalance(player.getUUID(), requiredCoin, CryptoBalanceConfig.getMintCostCoins(kind))) {
            return;
        }
        if (!takeItem(player, ModItems.CRYPTONIUM_INGOT.get(), CryptoBalanceConfig.getMintCostIngot(kind))
                || !takeItem(player, ModItems.SILICIUM.get(), CryptoBalanceConfig.getMintCostSilicium(kind))) {
            wallet.addBalance(player.getUUID(), requiredCoin, CryptoBalanceConfig.getMintCostCoins(kind));
            return;
        }

        String collection = kind == CollectibleKind.MACHINE_SKIN ? "datacenter" : "operator";
        String rarity = Math.floorMod(level.getGameTime() + player.blockPosition().asLong(), 11L) == 0 ? "rare" : "standard";
        String effect = kind == CollectibleKind.MACHINE_SKIN
                ? (Math.floorMod(level.getGameTime(), 2L) == 0 ? "ember" : "aurora")
                : (Math.floorMod(level.getGameTime(), 2L) == 0 ? "fee_discount" : "cooling_boost");
        ItemStack collectible = CollectibleItem.createCollectible(ModItems.COLLECTIBLE_TOKEN.get(), kind, collection, rarity, effect, level.getGameTime());
        giveItem(player, collectible);
        wallet.incrementMintedCollectibles(player.getUUID());
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 10 + column * 18, 166 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(inventory, slot, 10 + slot * 18, 224));
        }
    }

    private boolean takeItem(Player player, Item item, int amount) {
        int count = 0;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.is(item)) {
                count += stack.getCount();
            }
        }
        if (count < amount) {
            return false;
        }
        int remaining = amount;
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(item)) {
                continue;
            }
            int taken = Math.min(remaining, stack.getCount());
            stack.shrink(taken);
            if (stack.isEmpty()) {
                inventory.setItem(slot, ItemStack.EMPTY);
            }
            remaining -= taken;
        }
        inventory.setChanged();
        return true;
    }

    private void giveItem(Player player, ItemStack stack) {
        ItemStack remaining = stack.copy();
        while (!remaining.isEmpty()) {
            int split = Math.min(remaining.getMaxStackSize(), remaining.getCount());
            ItemStack chunk = remaining.split(split);
            if (!player.addItem(chunk)) {
                player.drop(chunk, false);
            }
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack sourceStackCopy = sourceStack.copy();

        if (index < SLOT_COUNT) {
            if (!moveItemStackTo(sourceStack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.is(ModTags.Items.GRID_UPGRADES)) {
            if (!moveItemStackTo(sourceStack, ServerBlockEntity.GRID_SLOT_1, ServerBlockEntity.GRID_SLOT_2 + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.getItem() instanceof CollectibleItem
                && CollectibleItem.getKind(sourceStack) == CollectibleKind.OPERATOR_BADGE) {
            if (!moveItemStackTo(sourceStack, ServerBlockEntity.BADGE_SLOT, ServerBlockEntity.BADGE_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < PLAYER_INVENTORY_END) {
            if (!moveItemStackTo(sourceStack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(sourceStack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
            return ItemStack.EMPTY;
        }

        if (sourceStack.isEmpty()) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(player, sourceStack);
        return sourceStackCopy;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.SERVER_BLOCK.get());
    }
}
