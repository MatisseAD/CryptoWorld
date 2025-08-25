package fr.jachou.cryptoworld.menu;

import fr.jachou.cryptoworld.block.ModBlocks;
import fr.jachou.cryptoworld.blockentity.ExchangeBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;

public class ExchangeMenu extends AbstractContainerMenu {
    private final ExchangeBlockEntity blockEntity;
    private final ContainerLevelAccess access;

    public ExchangeMenu(int id, Inventory playerInv, FriendlyByteBuf buf) {
        this(id, playerInv, (ExchangeBlockEntity) playerInv.player.level().getBlockEntity(buf.readBlockPos()));
    }

    public ExchangeMenu(int id, Inventory playerInv, ExchangeBlockEntity blockEntity) {
        super(ModMenus.EXCHANGE_MENU.get(), id);
        this.blockEntity = blockEntity;
        this.access = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        var handler = blockEntity.getItemHandler();
        // Offer slot
        this.addSlot(new SlotItemHandler(handler, 0, 44, 35));
        // Demand slot (output)
        this.addSlot(new SlotItemHandler(handler, 1, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }

            @Override
            public void onTake(Player player, ItemStack stack) {
                super.onTake(player, stack);
                blockEntity.removeOffer();
            }
        });

        addPlayerInventory(playerInv);
    }

    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(inventory, col, 8 + col * 18, 142));
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.EXCHANGE_BLOCK.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }
}
