package fr.jachou.cryptoworld.blockentity;

import fr.jachou.cryptoworld.item.ModItems;
import fr.jachou.cryptoworld.menu.ExchangeMenu;
import fr.jachou.cryptoworld.util.PriceManager;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ExchangeBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            if (slot == 0) {
                updateDemand();
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 0;
        }
    };

    public ExchangeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXCHANGE_BLOCK_ENTITY.get(), pos, state);
    }

    public void updateDemand() {
        ItemStack offer = itemHandler.getStackInSlot(0);
        if (offer.isEmpty()) {
            itemHandler.setStackInSlot(1, ItemStack.EMPTY);
            return;
        }
        if (offer.getItem() == ModItems.BITCOIN.get()) {
            int rate = PriceManager.getBitcoinToEthereumRate();
            itemHandler.setStackInSlot(1, new ItemStack(ModItems.ETHEREUM.get(), offer.getCount() * rate));
        } else if (offer.getItem() == ModItems.ETHEREUM.get()) {
            int rate = PriceManager.getEthereumToBitcoinRate();
            int amount = offer.getCount() / rate;
            if (amount > 0) {
                itemHandler.setStackInSlot(1, new ItemStack(ModItems.BITCOIN.get(), amount));
            } else {
                itemHandler.setStackInSlot(1, ItemStack.EMPTY);
            }
        } else {
            itemHandler.setStackInSlot(1, ItemStack.EMPTY);
        }
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public void removeOffer() {
        itemHandler.setStackInSlot(0, ItemStack.EMPTY);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Exchange");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ExchangeMenu(id, inventory, this);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
    }
}
