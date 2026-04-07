package fr.jachou.cryptoworld.screen;

import fr.jachou.cryptoworld.market.CoinType;
import fr.jachou.cryptoworld.market.ExchangeId;
import fr.jachou.cryptoworld.menu.ServerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import java.util.HashMap;
import java.util.Map;

public class ServerScreen extends AbstractContainerScreen<ServerMenu> {
    private final Map<Integer, Button> actionButtons = new HashMap<>();

    public ServerScreen(ServerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 230;
        this.imageHeight = 186;
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    protected void init() {
        super.init();
        addTabButton(ServerMenu.TAB_OVERVIEW, 8, "screen.cryptoworld.tab.overview");
        addTabButton(ServerMenu.TAB_POWER, 52, "screen.cryptoworld.tab.power");
        addTabButton(ServerMenu.TAB_EXCHANGE, 96, "screen.cryptoworld.tab.exchange");
        addTabButton(ServerMenu.TAB_YIELD, 150, "screen.cryptoworld.tab.yield");
        addTabButton(ServerMenu.TAB_CONTRACTS, 188, "screen.cryptoworld.tab.contracts");

        addActionButton(50, 20, 36, 20, 20, "<");
        addActionButton(51, 44, 36, 20, 20, ">");
        addActionButton(100, 20, 68, 64, 20, "Dep BTC");
        addActionButton(101, 88, 68, 64, 20, "Dep ETH");
        addActionButton(102, 156, 68, 64, 20, "Out BTC");
        addActionButton(103, 156, 92, 64, 20, "Out ETH");
        addActionButton(104, 20, 92, 64, 20, "Buy BTC");
        addActionButton(105, 88, 92, 64, 20, "Sell BTC");
        addActionButton(106, 20, 116, 64, 20, "Buy ETH");
        addActionButton(107, 88, 116, 64, 20, "Sell ETH");
        addActionButton(108, 20, 140, 64, 20, "Lmt BTC");
        addActionButton(109, 88, 140, 64, 20, "Ask BTC");
        addActionButton(110, 156, 116, 64, 20, "Lmt ETH");
        addActionButton(111, 156, 140, 64, 20, "Ask ETH");
        addActionButton(112, 156, 36, 64, 20, "Cancel");

        addActionButton(120, 20, 68, 100, 20, "Pay Bill");

        addActionButton(130, 20, 68, 64, 20, "Stake");
        addActionButton(131, 88, 68, 64, 20, "Lend BTC");
        addActionButton(132, 156, 68, 64, 20, "Lend ETH");
        addActionButton(133, 20, 96, 96, 20, "Exit Oldest");
        addActionButton(134, 122, 96, 96, 20, "Claim");

        addActionButton(140, 20, 68, 84, 20, "Mint Skin");
        addActionButton(141, 110, 68, 84, 20, "Mint Badge");
        updateButtonVisibility();
    }

    private void addTabButton(int id, int xOffset, String key) {
        addRenderableWidget(Button.builder(Component.translatable(key), button ->
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id))
                .bounds(this.leftPos + xOffset, this.topPos + 6, 40, 18)
                .build());
    }

    private void addActionButton(int id, int xOffset, int yOffset, int width, int height, String label) {
        Button button = Button.builder(Component.literal(label), widget ->
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id))
                .bounds(this.leftPos + xOffset, this.topPos + yOffset, width, height)
                .build();
        actionButtons.put(id, button);
        addRenderableWidget(button);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        updateButtonVisibility();
    }

    private void updateButtonVisibility() {
        int tab = menu.getCurrentTab();
        setVisible(50, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(51, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(100, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(101, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(102, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(103, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(104, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(105, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(106, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(107, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(108, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(109, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(110, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(111, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(112, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(120, tab == ServerMenu.TAB_POWER);
        setVisible(130, tab == ServerMenu.TAB_YIELD);
        setVisible(131, tab == ServerMenu.TAB_YIELD);
        setVisible(132, tab == ServerMenu.TAB_YIELD);
        setVisible(133, tab == ServerMenu.TAB_YIELD);
        setVisible(134, tab == ServerMenu.TAB_YIELD);
        setVisible(140, tab == ServerMenu.TAB_CONTRACTS);
        setVisible(141, tab == ServerMenu.TAB_CONTRACTS);
    }

    private void setVisible(int id, boolean visible) {
        Button button = actionButtons.get(id);
        if (button != null) {
            button.visible = visible;
            button.active = visible;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        int x = this.leftPos;
        int y = this.topPos;
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF101922);
        guiGraphics.fill(x + 4, y + 4, x + this.imageWidth - 4, y + this.imageHeight - 4, 0xFF1A2632);
        guiGraphics.fill(x + 12, y + 30, x + 168, y + 84, 0xFF14202B);
        guiGraphics.fill(x + 174, y + 30, x + 218, y + 76, 0xFF263442);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 8, 0xE7EDF7, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 26, this.inventoryLabelY, 0xC7D2E0, false);

        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.wallet_line",
                menu.getWalletBalance(CoinType.BITCOIN), menu.getWalletBalance(CoinType.ETHEREUM)), 14, 34, 0xD7E5F2, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.price_line",
                menu.getFairPrice(CoinType.BITCOIN), menu.getFairPrice(CoinType.ETHEREUM)), 14, 46, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.orders_line",
                menu.getActiveOrders(), menu.getActivePositions(), menu.getMintedCollectibles()), 14, 58, 0x93A4B7, false);

        int tab = menu.getCurrentTab();
        if (tab == ServerMenu.TAB_OVERVIEW) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_rigs", menu.getRigCount()), 14, 76, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_power", menu.getAvailablePower(), menu.getGridCap()), 14, 88, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_battery", menu.getBatteryStored()), 14, 100, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_arbitrage",
                    ExchangeId.fromId(menu.getBestBuyExchangeId()).name(),
                    ExchangeId.fromId(menu.getBestSellExchangeId()).name(),
                    menu.getArbitrageSpread()), 14, 112, 0x93A4B7, false);
        } else if (tab == ServerMenu.TAB_POWER) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_generation",
                    menu.getSolarCount(), menu.getWindCount()), 14, 76, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_storage",
                    menu.getBatteryStored(), menu.getGridCap()), 14, 88, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_bill", menu.getBillDue()), 14, 100, 0x93A4B7, false);
        } else if (tab == ServerMenu.TAB_EXCHANGE) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.exchange_selected", menu.getSelectedExchange().name()), 14, 76, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.exchange_health", menu.getExchangeHealth()), 14, 88, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.exchange_arbitrage", menu.getArbitrageSpread()), 14, 100, 0x93A4B7, false);
        } else if (tab == ServerMenu.TAB_YIELD) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.yield_staking", menu.getStakeApyPercent()), 14, 76, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.yield_lending", menu.getLendApyPercent()), 14, 88, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.yield_positions", menu.getActivePositions()), 14, 100, 0x93A4B7, false);
        } else if (tab == ServerMenu.TAB_CONTRACTS) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.contracts_skin"), 14, 76, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.contracts_badge"), 14, 88, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.contracts_minted", menu.getMintedCollectibles()), 14, 100, 0x93A4B7, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
