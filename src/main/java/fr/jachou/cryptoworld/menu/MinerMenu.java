package fr.jachou.cryptoworld.menu;

import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.blockentity.MinerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class MinerMenu extends AbstractContainerMenu {
    private final MinerBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public MinerMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, (MinerBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public MinerMenu(int id, Inventory inv, MinerBlockEntity entity) {
        super(ModMenuTypes.MINER_MENU.get(), id);
        this.blockEntity = entity;
        this.access = ContainerLevelAccess.create(entity.getLevel(), entity.getBlockPos());

        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 0, 26, 17)); // GPU
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 1, 26, 53)); // RAM
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 2, 56, 53)); // Fuel
        this.addSlot(new SlotItemHandler(entity.getItemHandler(), 3, 116, 35)); // Output

        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    private void addPlayerInventory(Inventory inv) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory inv) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(inv, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        int size = blockEntity.getItemHandler().getSlots();
        if (slot != null && slot.hasItem()) {
            ItemStack stackInSlot = slot.getItem();
            stack = stackInSlot.copy();
            if (index < size) {
                if (!this.moveItemStackTo(stackInSlot, size, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(stackInSlot, 0, size, false)) {
                return ItemStack.EMPTY;
            }

            if (stackInSlot.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(this.access, player, ModBlocks.MINER_BLOCK.get());
    }
}
