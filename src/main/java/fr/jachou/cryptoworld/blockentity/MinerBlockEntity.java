package fr.jachou.cryptoworld.blockentity;

import fr.jachou.cryptoworld.item.ModItems;
import fr.jachou.cryptoworld.menu.MinerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
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

public class MinerBlockEntity extends BlockEntity implements net.minecraft.world.MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case 0 -> stack.is(ModItems.GTX_1080.get()) || stack.is(ModItems.RTX_2080.get());
                case 1 -> stack.is(ModItems.RAM.get());
                case 2 -> stack.is(ModItems.SILICIUM_HYPERCHARGED.get());
                case 3 -> stack.is(ModItems.BITCOIN.get()) || stack.is(ModItems.ETHEREUM.get());
                default -> super.isItemValid(slot, stack);
            };
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
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable net.minecraft.core.Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.cryptoworld.miner_block");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.Inventory inventory, Player player) {
        return new MinerMenu(id, inventory, this);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MinerBlockEntity entity) {
        if (level.isClientSide) {
            return;
        }

        ItemStack gpu = entity.itemHandler.getStackInSlot(0);
        ItemStack ram = entity.itemHandler.getStackInSlot(1);
        ItemStack fuel = entity.itemHandler.getStackInSlot(2);
        ItemStack output = entity.itemHandler.getStackInSlot(3);

        if (!gpu.isEmpty() && !ram.isEmpty() && fuel.is(ModItems.SILICIUM_HYPERCHARGED.get())) {
            Item coinItem = level.random.nextBoolean() ? ModItems.BITCOIN.get() : ModItems.ETHEREUM.get();
            if (output.isEmpty()) {
                entity.itemHandler.setStackInSlot(3, new ItemStack(coinItem));
                fuel.shrink(1);
                setChanged(level, pos, state);
            } else if (output.is(coinItem) && output.getCount() < output.getMaxStackSize()) {
                output.grow(1);
                fuel.shrink(1);
                setChanged(level, pos, state);
            }
        }
    }
}
