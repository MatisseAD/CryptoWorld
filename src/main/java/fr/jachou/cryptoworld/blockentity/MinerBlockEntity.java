package fr.jachou.cryptoworld.blockentity;

import fr.jachou.cryptoworld.balance.CryptoBalanceConfig;
import fr.jachou.cryptoworld.item.custom.RigPartItem;
import fr.jachou.cryptoworld.market.CoinType;
import fr.jachou.cryptoworld.market.PlayerWalletSavedData;
import fr.jachou.cryptoworld.menu.MinerMenu;
import fr.jachou.cryptoworld.rig.HeatState;
import fr.jachou.cryptoworld.rig.PoolId;
import fr.jachou.cryptoworld.rig.RigPartStats;
import fr.jachou.cryptoworld.rig.RigPartType;
import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MinerBlockEntity extends BlockEntity implements MenuProvider, SkinnableMachine {
  public static final int COMPUTE_SLOT_1 = 0;
  public static final int COMPUTE_SLOT_2 = 1;
  public static final int COMPUTE_SLOT_3 = 2;
  public static final int COMPUTE_SLOT_4 = 3;
  public static final int PSU_SLOT = 4;
  public static final int COOLING_SLOT = 5;
  public static final int MEMORY_SLOT = 6;
  public static final int BACKUP_CELL_SLOT = 7;
  public static final int OUTPUT_SLOT = 8;
  public static final int SLOT_COUNT = 9;

  public static final int DATA_PROGRESS = 0;
  public static final int DATA_MAX_PROGRESS = 1;
  public static final int DATA_BACKUP_POWER = 2;
  public static final int DATA_SELECTED_COIN = 3;
  public static final int DATA_POWER_DRAW = 4;
  public static final int DATA_HEAT = 5;
  public static final int DATA_HEAT_STATE = 6;
  public static final int DATA_DURABILITY = 7;
  public static final int DATA_POOL = 8;
  public static final int DATA_LINKED = 9;
  public static final int DATA_COUNT = 10;

  private int progress;
  private int maxProgress = CryptoBalanceConfig.BASE_OPERATION_TIME;
  private int backupPower;
  private int selectedCoinId = -1;
  private int powerDrawDisplay;
  private int heat;
  private int heatStateId;
  private int durabilityAverage = 100;
  private int poolId = PoolId.SOLO.getSerializedId();
  private int linkedFlag;
  private int pendingPoolCycles;
  private int bufferedPayout;
  private long linkedControllerPos = Long.MIN_VALUE;
  private long lastControllerLookup = Long.MIN_VALUE;
  private UUID ownerUuid;
  private String ownerName = "";
  private String skinId = "default";

  private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
    @Override
    protected void onContentsChanged(int slot) {
      setChanged();
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
      return switch (slot) {
        case COMPUTE_SLOT_1, COMPUTE_SLOT_2, COMPUTE_SLOT_3, COMPUTE_SLOT_4 -> stack.is(ModTags.Items.RIG_COMPUTE);
        case PSU_SLOT -> stack.is(ModTags.Items.RIG_PSU);
        case COOLING_SLOT -> stack.is(ModTags.Items.RIG_COOLING);
        case MEMORY_SLOT -> stack.is(ModTags.Items.MEMORIES);
        case BACKUP_CELL_SLOT -> stack.is(ModTags.Items.BACKUP_CELLS);
        case OUTPUT_SLOT -> false;
        default -> false;
      };
    }
  };

  private final ContainerData dataAccess = new ContainerData() {
    @Override
    public int get(int index) {
      return switch (index) {
        case DATA_PROGRESS -> progress;
        case DATA_MAX_PROGRESS -> maxProgress;
        case DATA_BACKUP_POWER -> backupPower;
        case DATA_SELECTED_COIN -> selectedCoinId;
        case DATA_POWER_DRAW -> powerDrawDisplay;
        case DATA_HEAT -> heat;
        case DATA_HEAT_STATE -> heatStateId;
        case DATA_DURABILITY -> durabilityAverage;
        case DATA_POOL -> poolId;
        case DATA_LINKED -> linkedFlag;
        default -> 0;
      };
    }

    @Override
    public void set(int index, int value) {
      switch (index) {
        case DATA_PROGRESS -> progress = value;
        case DATA_MAX_PROGRESS -> maxProgress = value;
        case DATA_BACKUP_POWER -> backupPower = value;
        case DATA_SELECTED_COIN -> selectedCoinId = value;
        case DATA_POWER_DRAW -> powerDrawDisplay = value;
        case DATA_HEAT -> heat = value;
        case DATA_HEAT_STATE -> heatStateId = value;
        case DATA_DURABILITY -> durabilityAverage = value;
        case DATA_POOL -> poolId = value;
        case DATA_LINKED -> linkedFlag = value;
        default -> {
        }
      }
    }

    @Override
    public int getCount() {
      return DATA_COUNT;
    }
  };

  private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

  public MinerBlockEntity(BlockPos pos, BlockState state) {
    super(ModBlockEntities.MINER_BLOCK_ENTITY.get(), pos, state);
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

  public void setOwner(Player player) {
    if (ownerUuid == null) {
      ownerUuid = player.getUUID();
      ownerName = player.getGameProfile().getName();
      setChanged();
    }
  }

  public ItemStackHandler getItemHandler() {
    return itemHandler;
  }

  public ContainerData getDataAccess() {
    return dataAccess;
  }

  public void cyclePool() {
    PoolId next = PoolId.fromId((poolId + 1) % PoolId.values().length);
    poolId = next.getSerializedId();
    setChanged();
  }

  public String getSkinId() {
    return skinId;
  }

  public SimpleContainer getDrops() {
    SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
    for (int slot = 0; slot < itemHandler.getSlots(); slot++) {
      inventory.setItem(slot, itemHandler.getStackInSlot(slot));
    }
    return inventory;
  }

  public static void tick(Level level, BlockPos pos, BlockState state, MinerBlockEntity entity) {
    if (!(level instanceof ServerLevel serverLevel)) {
      return;
    }

    RigSnapshot snapshot = entity.buildSnapshot(serverLevel);
    entity.selectedCoinId = snapshot.coinType == null ? -1 : snapshot.coinType.getSerializedId();
    entity.powerDrawDisplay = snapshot.powerDraw;
    entity.maxProgress = snapshot.operationTime;
    entity.durabilityAverage = snapshot.durabilityAverage;
    entity.linkedFlag = snapshot.linked ? 1 : 0;
    entity.heatStateId = HeatState.fromHeat(entity.heat).ordinal();

    if (snapshot.coinType == null || snapshot.computeStacks.isEmpty()) {
      entity.progress = 0;
      entity.coolDown(snapshot.coolingPower + 4);
      setChanged(level, pos, state);
      return;
    }

    if (!entity.hasOperatingPower(snapshot)) {
      entity.progress = 0;
      entity.coolDown(snapshot.coolingPower / 2 + 2);
      setChanged(level, pos, state);
      return;
    }

    entity.heat = CryptoBalanceConfig.clampHeat(entity.heat + snapshot.heatGeneration - snapshot.coolingPower);
    HeatState heatState = HeatState.fromHeat(entity.heat);
    entity.heatStateId = heatState.ordinal();

    if (heatState == HeatState.CRITICAL) {
      entity.progress = 0;
      entity.damageStacks(snapshot.computeStacks, 1);
      entity.coolDown(snapshot.coolingPower / 3 + 1);
      setChanged(level, pos, state);
      return;
    }

    if (snapshot.payoutDue && snapshot.outputMode && !entity.canOutput(snapshot.coinType, snapshot.predictedPayout)) {
      entity.progress = 0;
      setChanged(level, pos, state);
      return;
    }

    entity.progress++;
    if (entity.progress >= snapshot.operationTime) {
      entity.completeCycle(serverLevel, snapshot);
      entity.progress = 0;
    }

    setChanged(level, pos, state);
  }

  private RigSnapshot buildSnapshot(ServerLevel level) {
    ServerBlockEntity controller = findController(level);
    boolean linked = controller != null;
    PoolId pool = PoolId.fromId(poolId);
    ItemStack psuStack = itemHandler.getStackInSlot(PSU_SLOT);
    ItemStack coolingStack = itemHandler.getStackInSlot(COOLING_SLOT);
    ItemStack memoryStack = itemHandler.getStackInSlot(MEMORY_SLOT);

    RigPartStats psuStats = getRigStats(psuStack);
    RigPartStats coolingStats = getRigStats(coolingStack);
    RigPartStats memoryStats = getRigStats(memoryStack);

    int allowedComputeSlots = linked && psuStats.type() == RigPartType.PSU
        ? CryptoBalanceConfig.DATACENTER_COMPUTE_SLOTS
        : CryptoBalanceConfig.AUTONOMOUS_COMPUTE_SLOTS;
    int powerCapacity = linked && psuStats.type() == RigPartType.PSU
        ? psuStats.powerCapacity()
        : CryptoBalanceConfig.AUTONOMOUS_POWER_CAP;

    CoinType preferredCoin = getPreferredCoin();
    List<ItemStack> activeCompute = new ArrayList<>();
    int computePower = 0;
    int powerDraw = coolingStats.powerDraw();
    int heatGeneration = 0;
    int stability = memoryStats.stability();

    for (int slot = COMPUTE_SLOT_1; slot <= COMPUTE_SLOT_4 && activeCompute.size() < allowedComputeSlots; slot++) {
      ItemStack stack = itemHandler.getStackInSlot(slot);
      RigPartStats stats = getRigStats(stack);
      if (stats.type() != RigPartType.COMPUTE || stats.coinType() != preferredCoin) {
        continue;
      }
      if (powerDraw + stats.powerDraw() > powerCapacity) {
        continue;
      }

      activeCompute.add(stack);
      computePower += stats.computePower();
      powerDraw += stats.powerDraw();
      heatGeneration += stats.heatGeneration();
      stability += stats.stability();
    }

    boolean roomBonus = linked && controller != null && controller.hasServerRoomBonus(worldPosition);
    int coolingPower = coolingStats.coolingPower()
        + (roomBonus && controller != null ? controller.getCoolingInfrastructureBonus() : 0);
    int operationTime = computePower <= 0
        ? CryptoBalanceConfig.BASE_OPERATION_TIME
        : Math.max(CryptoBalanceConfig.MIN_OPERATION_TIME,
            CryptoBalanceConfig.BASE_OPERATION_TIME - computePower * 9 - stability * 2 - (roomBonus ? 8 : 0));

    HeatState currentHeatState = HeatState.fromHeat(heat);
    if (currentHeatState == HeatState.HOT) {
      operationTime += 20;
    } else if (currentHeatState == HeatState.THROTTLED) {
      operationTime += 60;
    }

    int baseOutput = Math.max(1, computePower / 4);
    if (memoryStats.type() == RigPartType.MEMORY) {
      baseOutput++;
    }
    if (currentHeatState == HeatState.THROTTLED) {
      baseOutput = Math.max(1, baseOutput - 1);
    }

    int totalBuffered = bufferedPayout + baseOutput;
    int cyclesAfterThis = pendingPoolCycles + 1;
    boolean payoutDue = cyclesAfterThis >= pool.getPayoutIntervalCycles();
    int predictedPayout = payoutDue
        ? applyPoolPayout(pool, level, preferredCoin, totalBuffered)
        : 0;

    return new RigSnapshot(
        preferredCoin,
        activeCompute,
        linked,
        controller,
        powerDraw,
        heatGeneration,
        coolingPower,
        operationTime,
        baseOutput,
        computePower,
        computeDurabilityAverage(activeCompute, psuStack, coolingStack, memoryStack),
        payoutDue,
        predictedPayout,
        controller == null && ownerUuid == null);
  }

  @Nullable
  private CoinType getPreferredCoin() {
    int bitcoinPower = 0;
    int ethereumPower = 0;
    for (int slot = COMPUTE_SLOT_1; slot <= COMPUTE_SLOT_4; slot++) {
      RigPartStats stats = getRigStats(itemHandler.getStackInSlot(slot));
      if (stats.type() != RigPartType.COMPUTE || stats.coinType() == null) {
        continue;
      }
      if (stats.coinType() == CoinType.BITCOIN) {
        bitcoinPower += stats.computePower();
      } else {
        ethereumPower += stats.computePower();
      }
    }
    if (bitcoinPower == 0 && ethereumPower == 0) {
      return null;
    }
    return bitcoinPower >= ethereumPower ? CoinType.BITCOIN : CoinType.ETHEREUM;
  }

  private boolean hasOperatingPower(RigSnapshot snapshot) {
    if (snapshot.linked && snapshot.controller != null && snapshot.controller.requestPower(snapshot.powerDraw)) {
      return true;
    }

    if (backupPower < snapshot.powerDraw && !consumeBackupCell()) {
      return false;
    }

    if (backupPower < snapshot.powerDraw) {
      return false;
    }

    backupPower -= snapshot.powerDraw;
    return true;
  }

  private boolean consumeBackupCell() {
    ItemStack backupCell = itemHandler.getStackInSlot(BACKUP_CELL_SLOT);
    if (!backupCell.is(ModTags.Items.BACKUP_CELLS)) {
      return false;
    }
    backupCell.shrink(1);
    if (backupCell.isEmpty()) {
      itemHandler.setStackInSlot(BACKUP_CELL_SLOT, ItemStack.EMPTY);
    }
    backupPower += CryptoBalanceConfig.BACKUP_CELL_POWER;
    return true;
  }

  private void completeCycle(ServerLevel level, RigSnapshot snapshot) {
    PoolId pool = PoolId.fromId(poolId);
    bufferedPayout += snapshot.baseOutput;
    pendingPoolCycles++;

    damageStacks(snapshot.computeStacks, 1);
    damageIfPresent(itemHandler.getStackInSlot(PSU_SLOT), 1);
    if (HeatState.fromHeat(heat).ordinal() >= HeatState.HOT.ordinal()) {
      damageIfPresent(itemHandler.getStackInSlot(COOLING_SLOT), 1);
    }

    if (pendingPoolCycles < pool.getPayoutIntervalCycles()) {
      return;
    }

    int payout = applyPoolPayout(pool, level, snapshot.coinType, bufferedPayout);
    pendingPoolCycles = 0;
    bufferedPayout = 0;

    if (snapshot.outputMode) {
      addToOutput(snapshot.coinType, payout);
      return;
    }

    UUID walletOwner = snapshot.controller != null && snapshot.controller.getOwnerUuid() != null
        ? snapshot.controller.getOwnerUuid()
        : ownerUuid;
    if (walletOwner == null) {
      addToOutput(snapshot.coinType, payout);
      return;
    }

    PlayerWalletSavedData.get(level).addBalance(walletOwner, snapshot.coinType, payout);
  }

  private int applyPoolPayout(PoolId pool, ServerLevel level, @Nullable CoinType coinType, int grossAmount) {
    if (coinType == null || grossAmount <= 0) {
      return 0;
    }
    long salt = level.getGameTime() + worldPosition.asLong() + coinType.ordinal() * 71L;
    int varianceAdjusted = pool.applyVariance(grossAmount, salt);
    return Math.max(1, varianceAdjusted * (100 - pool.getFeePercent()) / 100);
  }

  private void addToOutput(CoinType coinType, int amount) {
    ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
    if (outputStack.isEmpty()) {
      itemHandler.setStackInSlot(OUTPUT_SLOT, coinType.createStack(amount));
      return;
    }
    outputStack.grow(amount);
  }

  private boolean canOutput(CoinType coinType, int amount) {
    ItemStack outputStack = itemHandler.getStackInSlot(OUTPUT_SLOT);
    if (outputStack.isEmpty()) {
      return true;
    }
    if (!outputStack.is(coinType.getItem())) {
      return false;
    }
    return outputStack.getCount() + amount <= outputStack.getMaxStackSize();
  }

  @Nullable
  private ServerBlockEntity findController(ServerLevel level) {
    if (linkedControllerPos != Long.MIN_VALUE) {
      BlockEntity cached = level.getBlockEntity(BlockPos.of(linkedControllerPos));
      if (cached instanceof ServerBlockEntity controller
          && BlockPos.of(linkedControllerPos).closerThan(worldPosition, CryptoBalanceConfig.DATACENTER_RADIUS + 0.5D)) {
        return controller;
      }
    }

    if (level.getGameTime() - lastControllerLookup < 20L) {
      return null;
    }

    lastControllerLookup = level.getGameTime();
    BlockPos min = worldPosition.offset(-CryptoBalanceConfig.DATACENTER_RADIUS, -CryptoBalanceConfig.DATACENTER_RADIUS,
        -CryptoBalanceConfig.DATACENTER_RADIUS);
    BlockPos max = worldPosition.offset(CryptoBalanceConfig.DATACENTER_RADIUS, CryptoBalanceConfig.DATACENTER_RADIUS,
        CryptoBalanceConfig.DATACENTER_RADIUS);
    ServerBlockEntity nearest = null;
    double nearestDistance = Double.MAX_VALUE;
    for (BlockPos cursor : BlockPos.betweenClosed(min, max)) {
      BlockEntity blockEntity = level.getBlockEntity(cursor);
      if (!(blockEntity instanceof ServerBlockEntity controller)) {
        continue;
      }
      double distance = cursor.distSqr(worldPosition);
      if (distance < nearestDistance) {
        nearestDistance = distance;
        nearest = controller;
      }
    }

    linkedControllerPos = nearest == null ? Long.MIN_VALUE : nearest.getBlockPos().asLong();
    return nearest;
  }

  private RigPartStats getRigStats(ItemStack stack) {
    if (stack.getItem() instanceof RigPartItem rigPartItem) {
      return rigPartItem.getRigPartStats();
    }
    return RigPartStats.EMPTY;
  }

  private int computeDurabilityAverage(List<ItemStack> computeStacks, ItemStack psuStack, ItemStack coolingStack,
      ItemStack memoryStack) {
    List<ItemStack> tracked = new ArrayList<>(computeStacks);
    if (!psuStack.isEmpty()) {
      tracked.add(psuStack);
    }
    if (!coolingStack.isEmpty()) {
      tracked.add(coolingStack);
    }
    if (!memoryStack.isEmpty()) {
      tracked.add(memoryStack);
    }
    if (tracked.isEmpty()) {
      return 100;
    }

    int total = 0;
    for (ItemStack stack : tracked) {
      if (stack.getMaxDamage() <= 0) {
        total += 100;
      } else {
        total += Math.max(0,
            (int) Math.round((stack.getMaxDamage() - stack.getDamageValue()) * 100.0D / stack.getMaxDamage()));
      }
    }
    return total / tracked.size();
  }

  private void damageStacks(List<ItemStack> stacks, int amount) {
    for (ItemStack stack : stacks) {
      damageIfPresent(stack, amount);
    }
  }

  private void damageIfPresent(ItemStack stack, int amount) {
    if (stack.isEmpty() || stack.getMaxDamage() <= 0) {
      return;
    }
    int nextDamage = stack.getDamageValue() + amount;
    if (nextDamage >= stack.getMaxDamage()) {
      stack.shrink(1);
      return;
    }
    stack.setDamageValue(nextDamage);
  }

  private void coolDown(int amount) {
    heat = Math.max(0, heat - Math.max(1, amount));
  }

  @Override
  protected void saveAdditional(CompoundTag tag) {
    tag.put("inventory", itemHandler.serializeNBT());
    tag.putInt("miner.progress", progress);
    tag.putInt("miner.max_progress", maxProgress);
    tag.putInt("miner.backup_power", backupPower);
    tag.putInt("miner.selected_coin", selectedCoinId);
    tag.putInt("miner.power_draw", powerDrawDisplay);
    tag.putInt("miner.heat", heat);
    tag.putInt("miner.heat_state", heatStateId);
    tag.putInt("miner.durability", durabilityAverage);
    tag.putInt("miner.pool", poolId);
    tag.putInt("miner.pending_pool_cycles", pendingPoolCycles);
    tag.putInt("miner.buffered_payout", bufferedPayout);
    tag.putLong("miner.linked_controller", linkedControllerPos);
    if (ownerUuid != null) {
      tag.putUUID("miner.owner_uuid", ownerUuid);
    }
    tag.putString("miner.owner_name", ownerName);
    tag.putString("miner.skin_id", skinId);
    tag.putInt("miner.data_version", CryptoBalanceConfig.MINER_DATA_VERSION);
    super.saveAdditional(tag);
  }

  @Override
  public void load(CompoundTag tag) {
    super.load(tag);
    itemHandler.deserializeNBT(tag.getCompound("inventory"));
    int dataVersion = tag.getInt("miner.data_version");
    if (dataVersion < CryptoBalanceConfig.MINER_DATA_VERSION) {
      migrateLegacyInventory();
    }
    progress = tag.getInt("miner.progress");
    maxProgress = tag.contains("miner.max_progress") ? tag.getInt("miner.max_progress")
        : CryptoBalanceConfig.BASE_OPERATION_TIME;
    backupPower = tag.contains("miner.backup_power") ? tag.getInt("miner.backup_power") : tag.getInt("miner.fuel");
    selectedCoinId = tag.contains("miner.selected_coin") ? tag.getInt("miner.selected_coin") : -1;
    powerDrawDisplay = tag.getInt("miner.power_draw");
    heat = tag.getInt("miner.heat");
    heatStateId = tag.getInt("miner.heat_state");
    durabilityAverage = tag.contains("miner.durability") ? tag.getInt("miner.durability") : 100;
    poolId = tag.contains("miner.pool") ? tag.getInt("miner.pool") : PoolId.SOLO.getSerializedId();
    pendingPoolCycles = tag.getInt("miner.pending_pool_cycles");
    bufferedPayout = tag.getInt("miner.buffered_payout");
    linkedControllerPos = tag.contains("miner.linked_controller") ? tag.getLong("miner.linked_controller")
        : Long.MIN_VALUE;
    ownerUuid = tag.hasUUID("miner.owner_uuid") ? tag.getUUID("miner.owner_uuid") : null;
    ownerName = tag.getString("miner.owner_name");
    skinId = tag.contains("miner.skin_id") ? tag.getString("miner.skin_id") : "default";
  }

  private void migrateLegacyInventory() {
    ItemStack legacyGpu = legacyStack(COMPUTE_SLOT_1);
    ItemStack legacyMemory = legacyStack(COMPUTE_SLOT_2);
    ItemStack legacyFuel = legacyStack(COMPUTE_SLOT_3);
    ItemStack legacyOutput = legacyStack(COMPUTE_SLOT_4);

    CompoundTag emptyInventory = new CompoundTag();
    emptyInventory.putInt("Size", SLOT_COUNT);
    emptyInventory.put("Items", new ListTag());
    itemHandler.deserializeNBT(emptyInventory);

    if (!legacyGpu.isEmpty()) {
      itemHandler.setStackInSlot(COMPUTE_SLOT_1, legacyGpu);
    }
    if (!legacyMemory.isEmpty()) {
      itemHandler.setStackInSlot(MEMORY_SLOT, legacyMemory);
    }
    if (!legacyFuel.isEmpty()) {
      itemHandler.setStackInSlot(BACKUP_CELL_SLOT, legacyFuel);
    }
    if (!legacyOutput.isEmpty()) {
      itemHandler.setStackInSlot(OUTPUT_SLOT, legacyOutput);
    }
  }

  private ItemStack legacyStack(int slot) {
    if (slot < 0 || slot >= itemHandler.getSlots()) {
      return ItemStack.EMPTY;
    }
    return itemHandler.getStackInSlot(slot).copy();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("block.cryptoworld.miner_block");
  }

  @Nullable
  @Override
  public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
    setOwner(player);
    return new MinerMenu(id, inventory, this, dataAccess);
  }

  @Override
  public boolean applySkin(String skinId) {
    this.skinId = skinId;
    setChanged();
    return true;
  }

  private record RigSnapshot(
      @Nullable CoinType coinType,
      List<ItemStack> computeStacks,
      boolean linked,
      @Nullable ServerBlockEntity controller,
      int powerDraw,
      int heatGeneration,
      int coolingPower,
      int operationTime,
      int baseOutput,
      int computePower,
      int durabilityAverage,
      boolean payoutDue,
      int predictedPayout,
      boolean outputMode) {
  }
}
