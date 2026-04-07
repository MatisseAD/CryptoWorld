package fr.jachou.cryptoworld.item.custom;

import fr.jachou.cryptoworld.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CryptoworldDetector extends Item {
    public CryptoworldDetector(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            BlockPos clickedPos = context.getClickedPos();
            boolean foundBlock = false;

            for (int y = clickedPos.getY(); y >= level.getMinBuildHeight(); y--) {
                BlockPos currentPos = new BlockPos(clickedPos.getX(), y, clickedPos.getZ());
                BlockState state = level.getBlockState(currentPos);

                if (isValuableBlock(state)) {
                    player.sendSystemMessage(Component.translatable(
                            "message.cryptoworld.detector_found",
                            state.getBlock().getName(),
                            currentPos.getX(),
                            currentPos.getY(),
                            currentPos.getZ()
                    ));
                    foundBlock = true;
                    break;
                }
            }

            if (!foundBlock) {
                player.sendSystemMessage(Component.translatable("message.cryptoworld.detector_none"));
            }
        }

        context.getItemInHand().hurtAndBreak(1, player, currentPlayer ->
                currentPlayer.broadcastBreakEvent(currentPlayer.getUsedItemHand()));
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, TooltipFlag isAdvanced) {
        tooltipComponents.add(Component.translatable("tooltip.cryptoworld.cryptoworld_detector"));
        super.appendHoverText(stack, level, tooltipComponents, isAdvanced);
    }

    private boolean isValuableBlock(BlockState state) {
        return state.is(ModTags.Blocks.METAL_DETECTORS);
    }
}
