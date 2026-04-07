package fr.jachou.cryptoworld.item.custom;

import fr.jachou.cryptoworld.blockentity.SkinnableMachine;
import fr.jachou.cryptoworld.collectible.CollectibleKind;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CollectibleItem extends Item {
    private static final String TAG_KIND = "CollectibleKind";
    private static final String TAG_COLLECTION = "Collection";
    private static final String TAG_RARITY = "Rarity";
    private static final String TAG_EFFECT = "Effect";
    private static final String TAG_SEED = "Seed";

    public CollectibleItem(Properties properties) {
        super(properties);
    }

    public static ItemStack createCollectible(Item item, CollectibleKind kind, String collection,
                                              String rarity, String effect, long seed) {
        ItemStack stack = new ItemStack(item);
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_KIND, kind.name());
        tag.putString(TAG_COLLECTION, collection);
        tag.putString(TAG_RARITY, rarity);
        tag.putString(TAG_EFFECT, effect);
        tag.putLong(TAG_SEED, seed);
        return stack;
    }

    public static CollectibleKind getKind(ItemStack stack) {
        return CollectibleKind.fromName(stack.getOrCreateTag().getString(TAG_KIND));
    }

    public static String getEffect(ItemStack stack) {
        return stack.getOrCreateTag().getString(TAG_EFFECT);
    }

    public static String getCollection(ItemStack stack) {
        return stack.getOrCreateTag().getString(TAG_COLLECTION);
    }

    public static long getSeed(ItemStack stack) {
        return stack.getOrCreateTag().getLong(TAG_SEED);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();

        if (player == null || getKind(stack) != CollectibleKind.MACHINE_SKIN || !(blockEntity instanceof SkinnableMachine skinnableMachine)) {
            return InteractionResult.PASS;
        }

        String skinId = getCollection(stack) + "_" + getEffect(stack);
        if (skinnableMachine.applySkin(skinId)) {
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.sendSystemMessage(Component.translatable("message.cryptoworld.skin_applied", skinId));
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        return InteractionResult.PASS;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.cryptoworld.collectible_kind", getKind(stack).name()));
        tooltipComponents.add(Component.translatable("tooltip.cryptoworld.collectible_collection", getCollection(stack)));
        tooltipComponents.add(Component.translatable("tooltip.cryptoworld.collectible_effect", getEffect(stack)));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }
}
