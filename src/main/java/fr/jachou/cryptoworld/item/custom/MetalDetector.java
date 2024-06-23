package fr.jachou.cryptoworld.item.custom;

import fr.jachou.cryptoworld.block.ModBlocks;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MetalDetector extends Item {
    public MetalDetector(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (!pContext.getLevel().isClientSide()) {
            BlockPos blockPos = pContext.getClickedPos();
            Player player = pContext.getPlayer();

            boolean foundBlock = false;

            for (int i = 0; i <= blockPos.getY() + 64; i++) {
                BlockState state = pContext.getLevel().getBlockState(blockPos.below(i));

                if (isValuableBlock(state)) {
                    outputValuableCoordinates(blockPos.below(i), player, state.getBlock());
                    foundBlock = true;

                    break;
                }
            }

            if (!foundBlock) {
                player.sendSystemMessage(Component.literal("No valuable block found"));
            }
        }

        pContext.getItemInHand().hurtAndBreak(1, pContext.getPlayer(), (player) -> {
            player.broadcastBreakEvent(player.getUsedItemHand());
        });


        return InteractionResult.SUCCESS;
    }

    private void outputValuableCoordinates(BlockPos blockPos, Player player, Block block) {
        player.sendSystemMessage(Component.literal("Found " + I18n.get(block.getDescriptionId()) + " at " + blockPos.getX() + " " + blockPos.getY() + " " + blockPos.getZ()));
    }

    private boolean isValuableBlock(BlockState state) {
        return state.is(ModBlocks.SILICIUM_ORE.get());
    }
}
