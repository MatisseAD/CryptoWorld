package fr.jachou.cryptoworld.screen;

import fr.jachou.cryptoworld.market.CoinType;
import fr.jachou.cryptoworld.menu.MinerMenu;
import fr.jachou.cryptoworld.rig.PoolId;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class MinerScreen extends AbstractContainerScreen<MinerMenu> {
    private Button poolButton;

    public MinerScreen(MinerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 176;
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    protected void init() {
        super.init();
        poolButton = Button.builder(Component.empty(), button ->
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0))
                .bounds(this.leftPos + 90, this.topPos + 72, 72, 20)
                .build();
        addRenderableWidget(poolButton);
        refreshPoolButton();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        refreshPoolButton();
    }

    private void refreshPoolButton() {
        if (poolButton != null) {
            poolButton.setMessage(Component.translatable("screen.cryptoworld.pool_button", menu.getPoolId().name()));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;

        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF111A22);
        guiGraphics.fill(x + 4, y + 4, x + this.imageWidth - 4, y + this.imageHeight - 4, 0xFF1B2734);

        guiGraphics.fill(x + 12, y + 16, x + 64, y + 80, 0xFF243548);
        guiGraphics.fill(x + 76, y + 16, x + 164, y + 84, 0xFF17222D);
        guiGraphics.fill(x + 136, y + 34, x + 160, y + 58, 0xFF2A3946);

        guiGraphics.fill(x + 84, y + 34, x + 128, y + 44, 0xFF0E151E);
        int progress = menu.getProgressScaled(42);
        if (progress > 0) {
            guiGraphics.fill(x + 85, y + 35, x + 85 + progress, y + 43, 0xFFFFB74D);
        }

        guiGraphics.fill(x + 84, y + 50, x + 92, y + 82, 0xFF0E151E);
        int backupPower = menu.getBackupPowerScaled(30);
        if (backupPower > 0) {
            guiGraphics.fill(x + 85, y + 81 - backupPower, x + 91, y + 81, 0xFF5EE6FF);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 6, 0xE7EDF7, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0xC7D2E0, false);

        CoinType selectedCoin = CoinType.fromId(menu.getSelectedCoinId());
        Component status = selectedCoin == null
                ? Component.translatable("screen.cryptoworld.miner_status_idle")
                : Component.translatable("screen.cryptoworld.miner_status_active", selectedCoin.getItem().getDescription());
        guiGraphics.drawString(this.font, status, 80, 18, 0xDCE6F2, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_draw_label", menu.getPowerDraw()), 80, 50, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.heat_label", menu.getHeat(), menu.getHeatState().name()), 80, 60, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.durability_label", menu.getDurabilityAverage()), 80, 90, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable(menu.isLinked()
                ? "screen.cryptoworld.linked_controller"
                : "screen.cryptoworld.autonomous_mode"), 80, 102, 0x93A4B7, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderCustomTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovering(84, 34, 44, 10, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.cryptoworld.tooltip.progress"));
            tooltip.add(Component.translatable("screen.cryptoworld.tooltip.progress_value", menu.getProgressScaled(100)));
            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (isHovering(84, 50, 8, 32, mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(this.font,
                    List.of(Component.translatable("screen.cryptoworld.tooltip.backup_power", menu.getBackupPower())),
                    mouseX,
                    mouseY);
        }
    }
}
