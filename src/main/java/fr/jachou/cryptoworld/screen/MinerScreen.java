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
        this.imageHeight = 196;
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    protected void init() {
        super.init();
        poolButton = Button.builder(Component.empty(), button ->
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, 0))
                .bounds(this.leftPos + 84, this.topPos + 90, 76, 12)
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

        // Outer border + inner background
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF111A22);
        guiGraphics.fill(x + 4, y + 4, x + this.imageWidth - 4, y + this.imageHeight - 4, 0xFF1B2734);

        // Left panel: 3 columns (compute x2, memory/backup), PSU/Cooling row
        guiGraphics.fill(x + 8, y + 12, x + 76, y + 88, 0xFF243548);
        // Slot type dividers
        guiGraphics.fill(x + 9, y + 50, x + 75, y + 51, 0xFF1A2E40); // separator between compute and PSU rows
        guiGraphics.fill(x + 66, y + 13, x + 67, y + 87, 0xFF1A2E40); // separator for memory/backup column

        // Right info panel
        guiGraphics.fill(x + 80, y + 12, x + 168, y + 88, 0xFF17222D);

        // Output slot highlight
        guiGraphics.fill(x + 134, y + 32, x + 156, y + 54, 0xFF2A3946);

        // Progress bar background
        guiGraphics.fill(x + 86, y + 28, x + 132, y + 38, 0xFF0E151E);
        int progress = menu.getProgressScaled(44);
        if (progress > 0) {
            guiGraphics.fill(x + 87, y + 29, x + 87 + progress, y + 37, 0xFFFFB74D);
        }

        // Backup power bar background (vertical, right side of info panel)
        guiGraphics.fill(x + 156, y + 42, x + 164, y + 86, 0xFF0E151E);
        int backupPower = menu.getBackupPowerScaled(42);
        if (backupPower > 0) {
            guiGraphics.fill(x + 157, y + 85 - backupPower, x + 163, y + 85, 0xFF5EE6FF);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 5, 0xE7EDF7, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 8, this.inventoryLabelY, 0xC7D2E0, false);

        // Slot type labels inside left panel
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.slot_compute"), 10, 14, 0x6B8FAB, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.slot_psu_cool"), 10, 52, 0x6B8FAB, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.slot_mem_bak"), 68, 14, 0x5E7B92, false);

        // Right panel info
        CoinType selectedCoin = CoinType.fromId(menu.getSelectedCoinId());
        Component status = selectedCoin == null
                ? Component.translatable("screen.cryptoworld.miner_status_idle")
                : Component.translatable("screen.cryptoworld.miner_status_active", selectedCoin.getItem().getDescription());
        guiGraphics.drawString(this.font, status, 84, 14, 0xDCE6F2, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_draw_label", menu.getPowerDraw()), 84, 42, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.heat_label", menu.getHeat(), menu.getHeatState().name()), 84, 52, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.durability_label", menu.getDurabilityAverage()), 84, 62, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable(menu.isLinked()
                ? "screen.cryptoworld.linked_controller"
                : "screen.cryptoworld.autonomous_mode"), 84, 72, 0x93A4B7, false);
        // Backup power label
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.backup_bar_label"), 150, 40, 0x4BB8CC, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
        renderCustomTooltips(guiGraphics, mouseX, mouseY);
    }

    private void renderCustomTooltips(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (isHovering(86, 28, 46, 10, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(Component.translatable("screen.cryptoworld.tooltip.progress"));
            tooltip.add(Component.translatable("screen.cryptoworld.tooltip.progress_value", menu.getProgressScaled(100)));
            guiGraphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
            return;
        }
        if (isHovering(156, 42, 8, 44, mouseX, mouseY)) {
            guiGraphics.renderComponentTooltip(this.font,
                    List.of(Component.translatable("screen.cryptoworld.tooltip.backup_power", menu.getBackupPower())),
                    mouseX,
                    mouseY);
        }
    }
}
