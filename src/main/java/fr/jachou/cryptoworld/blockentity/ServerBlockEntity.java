package fr.jachou.cryptoworld.blockentity;

import fr.jachou.cryptoworld.balance.CryptoBalanceConfig;
import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.item.custom.CollectibleItem;
import fr.jachou.cryptoworld.item.custom.RigPartItem;
import fr.jachou.cryptoworld.menu.ServerMenu;
import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ServerBlockEntity extends BlockEntity implements MenuProvider, SkinnableMachine {
    public static final int GRID_SLOT_1 = 0;
    public static final int GRID_SLOT_2 = 1;
    public static final int BADGE_SLOT = 2;

    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case GRID_SLOT_1, GRID_SLOT_2 -> stack.is(ModTags.Items.GRID_UPGRADES);
                case BADGE_SLOT -> stack.getItem() instanceof CollectibleItem
                        && CollectibleItem.getKind(stack) == fr.jachou.cryptoworld.collectible.CollectibleKind.OPERATOR_BADGE;
                default -> false;
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private UUID ownerUuid;
    private String ownerName = "";
    private String skinId = "default";
    private int storedBatteryPower;
    private int billDue;
    private int solarCount;
    private int windCount;
    private int batteryCount;
    private int ventilationCount;
    private int waterCoolingCount;
    private int rigCount;
    private long lastPowerTick = Long.MIN_VALUE;
    private long lastScanTick = Long.MIN_VALUE;
    private int generatedBudget;
    private int gridBudget;
    private int batteryBudget;
    private int availablePowerDisplay;
    private boolean brownout;

    public ServerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SERVER_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public void setOwner(Player player) {
        if (ownerUuid == null) {
            ownerUuid = player.getUUID();
            ownerName = player.getGameProfile().getName();
            setChanged();
        }
    }

    @Nullable
    public UUID getOwnerUuid() {
        return ownerUuid;
    }

    public String getSkinId() {
        return skinId;
    }

    public int getStoredBatteryPower() {
        return storedBatteryPower;
    }

    public int getBillDue() {
        return billDue;
    }

    public int getAvailablePowerDisplay() {
        return availablePowerDisplay;
    }

    public int getGridImportCap() {
        return CryptoBalanceConfig.BASE_GRID_IMPORT + getGridBoostFromSlots();
    }

    public int getRigCount() {
        return rigCount;
    }

    public int getSolarCount() {
        return solarCount;
    }

    public int getWindCount() {
        return windCount;
    }

    public int getBatteryCount() {
        return batteryCount;
    }

    public int getFeeDiscountPercent() {
        ItemStack badgeStack = itemHandler.getStackInSlot(BADGE_SLOT);
        if (badgeStack.getItem() instanceof CollectibleItem && "fee_discount".equals(CollectibleItem.getEffect(badgeStack))) {
            return 2;
        }
        return 0;
    }

    public int getCoolingInfrastructureBonus() {
        int bonus = ventilationCount * CryptoBalanceConfig.VENTILATION_BONUS
                + waterCoolingCount * CryptoBalanceConfig.WATER_COOLING_BONUS;
        ItemStack badgeStack = itemHandler.getStackInSlot(BADGE_SLOT);
        if (badgeStack.getItem() instanceof CollectibleItem && "cooling_boost".equals(CollectibleItem.getEffect(badgeStack))) {
            bonus += 6;
        }
        return bonus;
    }

    public boolean hasServerRoomBonus(BlockPos minerPos) {
        if (level == null) {
            return false;
        }
        return !level.canSeeSky(minerPos.above()) && (ventilationCount > 0 || waterCoolingCount > 0);
    }

    public boolean requestPower(int amount) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        prepareTickBudget(serverLevel);

        int batteryAvailable = Math.min(batteryBudget, storedBatteryPower);
        int totalAvailable = generatedBudget + gridBudget + batteryAvailable;
        availablePowerDisplay = totalAvailable;
        if (amount > totalAvailable) {
            brownout = true;
            return false;
        }

        int remaining = amount;
        int fromGeneration = Math.min(remaining, generatedBudget);
        generatedBudget -= fromGeneration;
        remaining -= fromGeneration;

        int fromBattery = Math.min(remaining, Math.min(batteryBudget, storedBatteryPower));
        storedBatteryPower -= fromBattery;
        batteryBudget -= fromBattery;
        remaining -= fromBattery;

        int fromGrid = Math.min(remaining, gridBudget);
        gridBudget -= fromGrid;
        billDue += fromGrid * CryptoBalanceConfig.GRID_BILL_RATE;
        remaining -= fromGrid;

        availablePowerDisplay = generatedBudget + gridBudget + Math.min(batteryBudget, storedBatteryPower);
        setChanged();
        return remaining <= 0;
    }

    public int payBill(Player player) {
        int paid = takeEmeralds(player, billDue);
        billDue -= paid;
        setChanged();
        return paid;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ServerBlockEntity entity) {
        if (!(level instanceof ServerLevel serverLevel)) {
            return;
        }
        entity.prepareTickBudget(serverLevel);
        if (serverLevel.getGameTime() - entity.lastScanTick >= 40L) {
            entity.scanCluster(serverLevel);
            entity.setChanged();
        }
    }

    private void prepareTickBudget(ServerLevel level) {
        if (lastPowerTick == level.getGameTime()) {
            return;
        }

        if (lastPowerTick != Long.MIN_VALUE) {
            chargeBattery(generatedBudget);
        }

        if (level.getGameTime() - lastScanTick >= 40L) {
            scanCluster(level);
        }

        lastPowerTick = level.getGameTime();
        generatedBudget = solarCount * CryptoBalanceConfig.getSolarOutput(level)
                + windCount * CryptoBalanceConfig.getWindOutput(level, worldPosition.asLong());
        gridBudget = getGridImportCap();
        batteryBudget = batteryCount * CryptoBalanceConfig.BATTERY_BLOCK_DISCHARGE;
        availablePowerDisplay = generatedBudget + gridBudget + Math.min(batteryBudget, storedBatteryPower);
        brownout = false;
    }

    private void chargeBattery(int generatedOverflow) {
        if (generatedOverflow <= 0 || batteryCount <= 0) {
            return;
        }
        int capacity = batteryCount * CryptoBalanceConfig.BATTERY_BLOCK_CAPACITY;
        storedBatteryPower = Math.min(capacity, storedBatteryPower + generatedOverflow);
    }

    private void scanCluster(ServerLevel level) {
        lastScanTick = level.getGameTime();
        solarCount = 0;
        windCount = 0;
        batteryCount = 0;
        ventilationCount = 0;
        waterCoolingCount = 0;
        rigCount = 0;

        int radius = CryptoBalanceConfig.DATACENTER_RADIUS;
        BlockPos min = worldPosition.offset(-radius, -radius, -radius);
        BlockPos max = worldPosition.offset(radius, radius, radius);

        for (BlockPos cursor : BlockPos.betweenClosed(min, max)) {
            BlockState scannedState = level.getBlockState(cursor);
            if (scannedState.is(ModBlocks.SOLAR_PANEL.get())) {
                solarCount++;
            } else if (scannedState.is(ModBlocks.WIND_TURBINE.get())) {
                windCount++;
            } else if (scannedState.is(ModBlocks.BATTERY_BANK.get())) {
                batteryCount++;
            } else if (scannedState.is(ModBlocks.VENTILATION_UNIT.get())) {
                ventilationCount++;
            } else if (scannedState.is(ModBlocks.WATER_COOLING_UNIT.get())) {
                waterCoolingCount++;
            }

            if (level.getBlockEntity(cursor) instanceof MinerBlockEntity) {
                rigCount++;
            }
        }
    }

    private int getGridBoostFromSlots() {
        int bonus = 0;
        for (int slot = GRID_SLOT_1; slot <= GRID_SLOT_2; slot++) {
            ItemStack stack = itemHandler.getStackInSlot(slot);
            if (stack.getItem() instanceof RigPartItem rigPartItem) {
                bonus += rigPartItem.getRigPartStats().gridBoost();
            }
        }
        return bonus;
    }

    private int takeEmeralds(Player player, int amount) {
        int remaining = amount;
        Inventory inventory = player.getInventory();
        for (int slot = 0; slot < inventory.getContainerSize() && remaining > 0; slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.is(net.minecraft.world.item.Items.EMERALD)) {
                continue;
            }
            int taken = Math.min(stack.getCount(), remaining);
            stack.shrink(taken);
            if (stack.isEmpty()) {
                inventory.setItem(slot, ItemStack.EMPTY);
            }
            remaining -= taken;
        }
        inventory.setChanged();
        return amount - remaining;
    }

    public SimpleContainer getDrops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
            inventory.setItem(slot, itemHandler.getStackInSlot(slot));
        }
        return inventory;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        if (ownerUuid != null) {
            tag.putUUID("owner_uuid", ownerUuid);
        }
        tag.putString("owner_name", ownerName);
        tag.putString("skin_id", skinId);
        tag.putInt("stored_battery_power", storedBatteryPower);
        tag.putInt("bill_due", billDue);
        tag.putInt("data_version", CryptoBalanceConfig.SERVER_DATA_VERSION);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        ownerUuid = tag.hasUUID("owner_uuid") ? tag.getUUID("owner_uuid") : null;
        ownerName = tag.getString("owner_name");
        skinId = tag.contains("skin_id") ? tag.getString("skin_id") : "default";
        storedBatteryPower = tag.getInt("stored_battery_power");
        billDue = tag.getInt("bill_due");
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cryptoworld.server_block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        setOwner(player);
        return new ServerMenu(id, inventory, this);
    }

    @Override
    public boolean applySkin(String skinId) {
        this.skinId = skinId;
        setChanged();
        return true;
    }
}
