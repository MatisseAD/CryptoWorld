package fr.jachou.cryptoworld.menu;

import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.blockentity.MinerBlockEntity;
import fr.jachou.cryptoworld.rig.HeatState;
import fr.jachou.cryptoworld.rig.PoolId;
import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class MinerMenu extends AbstractContainerMenu {
    private static final int MACHINE_SLOT_COUNT = MinerBlockEntity.SLOT_COUNT;
    private static final int PLAYER_INVENTORY_START = MACHINE_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END = PLAYER_INVENTORY_START + 27;
    private static final int HOTBAR_START = PLAYER_INVENTORY_END;
    private static final int HOTBAR_END = HOTBAR_START + 9;
    private static final int ACTION_CYCLE_POOL = 0;

    private final MinerBlockEntity blockEntity;
    private final ContainerLevelAccess access;
    private final ContainerData data;

    public MinerMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, (MinerBlockEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()),
                new SimpleContainerData(MinerBlockEntity.DATA_COUNT));
    }

    public MinerMenu(int id, Inventory inventory, MinerBlockEntity entity, ContainerData data) {
        super(ModMenuTypes.MINER_MENU.get(), id);
        this.blockEntity = entity;
        this.data = data;
        this.access = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());

        checkContainerDataCount(data, MinerBlockEntity.DATA_COUNT);

        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.COMPUTE_SLOT_1, 17, 21));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.COMPUTE_SLOT_2, 35, 21));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.COMPUTE_SLOT_3, 17, 39));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.COMPUTE_SLOT_4, 35, 39));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.PSU_SLOT, 17, 60));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.COOLING_SLOT, 35, 60));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.MEMORY_SLOT, 53, 21));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.BACKUP_CELL_SLOT, 53, 60));
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), MinerBlockEntity.OUTPUT_SLOT, 143, 39) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });

        addPlayerInventory(inventory);
        addPlayerHotbar(inventory);
        addDataSlots(data);
    }

    public int getProgressScaled(int width) {
        int progress = data.get(MinerBlockEntity.DATA_PROGRESS);
        int maxProgress = data.get(MinerBlockEntity.DATA_MAX_PROGRESS);
        if (progress <= 0 || maxProgress <= 0) {
            return 0;
        }
        return progress * width / maxProgress;
    }

    public int getBackupPowerScaled(int height) {
        int power = data.get(MinerBlockEntity.DATA_BACKUP_POWER);
        return power <= 0 ? 0 : Math.min(height, power * height / 1600);
    }

    public int getBackupPower() {
        return data.get(MinerBlockEntity.DATA_BACKUP_POWER);
    }

    public int getPowerDraw() {
        return data.get(MinerBlockEntity.DATA_POWER_DRAW);
    }

    public int getHeat() {
        return data.get(MinerBlockEntity.DATA_HEAT);
    }

    public HeatState getHeatState() {
        return HeatState.values()[Math.max(0, Math.min(HeatState.values().length - 1, data.get(MinerBlockEntity.DATA_HEAT_STATE)))];
    }

    public int getDurabilityAverage() {
        return data.get(MinerBlockEntity.DATA_DURABILITY);
    }

    public PoolId getPoolId() {
        return PoolId.fromId(data.get(MinerBlockEntity.DATA_POOL));
    }

    public boolean isLinked() {
        return data.get(MinerBlockEntity.DATA_LINKED) == 1;
    }

    public int getSelectedCoinId() {
        return data.get(MinerBlockEntity.DATA_SELECTED_COIN);
    }

    public void cyclePool() {
        this.clickMenuButton(null, ACTION_CYCLE_POOL);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 94 + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inventory) {
        for (int slot = 0; slot < 9; ++slot) {
            this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 152));
        }
    }

    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id == ACTION_CYCLE_POOL) {
            blockEntity.cyclePool();
            return true;
        }
        return super.clickMenuButton(player, id);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot sourceSlot = this.slots.get(index);
        if (!sourceSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack sourceStackCopy = sourceStack.copy();

        if (index < MACHINE_SLOT_COUNT) {
            if (!this.moveItemStackTo(sourceStack, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.is(ModTags.Items.RIG_COMPUTE)) {
            if (!this.moveItemStackTo(sourceStack, MinerBlockEntity.COMPUTE_SLOT_1, MinerBlockEntity.COMPUTE_SLOT_4 + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.is(ModTags.Items.RIG_PSU)) {
            if (!this.moveItemStackTo(sourceStack, MinerBlockEntity.PSU_SLOT, MinerBlockEntity.PSU_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.is(ModTags.Items.RIG_COOLING)) {
            if (!this.moveItemStackTo(sourceStack, MinerBlockEntity.COOLING_SLOT, MinerBlockEntity.COOLING_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.is(ModTags.Items.MEMORIES)) {
            if (!this.moveItemStackTo(sourceStack, MinerBlockEntity.MEMORY_SLOT, MinerBlockEntity.MEMORY_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (sourceStack.is(ModTags.Items.BACKUP_CELLS)) {
            if (!this.moveItemStackTo(sourceStack, MinerBlockEntity.BACKUP_CELL_SLOT, MinerBlockEntity.BACKUP_CELL_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < PLAYER_INVENTORY_END) {
            if (!this.moveItemStackTo(sourceStack, HOTBAR_START, HOTBAR_END, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(sourceStack, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
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
        return stillValid(this.access, player, ModBlocks.MINER_BLOCK.get());
    }
}
