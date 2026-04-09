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

    // Layout constants
    private static final int PANEL_LEFT   = 10;
    private static final int PANEL_RIGHT  = 168;
    private static final int INFO_TOP     = 26;
    private static final int INFO_BOTTOM  = 70;
    private static final int CONTENT_TOP  = 74;
    private static final int CONTENT_BOT  = 158;
    private static final int SIDE_LEFT    = 172;
    private static final int SIDE_RIGHT   = 220;
    private static final int SIDE_BOTTOM  = 80;

    public ServerScreen(ServerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 230;
        this.imageHeight = 256;
        this.inventoryLabelY = this.imageHeight - 92;
    }

    @Override
    protected void init() {
        super.init();

        // Tab buttons (top row)
        addTabButton(ServerMenu.TAB_OVERVIEW,  4,   "screen.cryptoworld.tab.overview");
        addTabButton(ServerMenu.TAB_POWER,     48,  "screen.cryptoworld.tab.power");
        addTabButton(ServerMenu.TAB_EXCHANGE,  92,  "screen.cryptoworld.tab.exchange");
        addTabButton(ServerMenu.TAB_YIELD,     146, "screen.cryptoworld.tab.yield");
        addTabButton(ServerMenu.TAB_CONTRACTS, 188, "screen.cryptoworld.tab.contracts");

        // Exchange tab: selector + cancel + buy/sell
        addActionButton(50,  12,  90, 18, 14, "<");
        addActionButton(51,  32,  90, 18, 14, ">");
        addActionButton(112, 56,  90, 76, 14, "Cancel Order");
        addActionButton(104, 12, 108, 72, 14, "Buy BTC");
        addActionButton(105, 88, 108, 72, 14, "Sell BTC");
        addActionButton(106, 12, 126, 72, 14, "Buy ETH");
        addActionButton(107, 88, 126, 72, 14, "Sell ETH");

        // Power tab
        addActionButton(120, 12, 108, 110, 14, "Pay Bill");

        // Yield tab
        addActionButton(130, 12,  108, 72, 14, "Stake BTC");
        addActionButton(131, 88,  108, 72, 14, "Lend BTC");
        addActionButton(132, 12,  126, 72, 14, "Lend ETH");
        addActionButton(133, 88,  126, 72, 14, "Exit Oldest");
        addActionButton(134, 12,  144, 72, 14, "Claim All");

        // Contracts tab
        addActionButton(140, 12, 108, 72, 14, "Mint Skin");
        addActionButton(141, 88, 108, 72, 14, "Mint Badge");

        // Overview tab: withdraw coins to inventory
        addActionButton(102, 12, 132, 72, 14, "Get BTC");
        addActionButton(103, 88, 132, 72, 14, "Get ETH");

        updateButtonVisibility();
    }

    private void addTabButton(int id, int xOffset, String key) {
        addRenderableWidget(Button.builder(Component.translatable(key), button ->
                        this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id))
                .bounds(this.leftPos + xOffset, this.topPos + 4, 40, 16)
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

        // Exchange buttons
        setVisible(50,  tab == ServerMenu.TAB_EXCHANGE);
        setVisible(51,  tab == ServerMenu.TAB_EXCHANGE);
        setVisible(112, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(104, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(105, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(106, tab == ServerMenu.TAB_EXCHANGE);
        setVisible(107, tab == ServerMenu.TAB_EXCHANGE);

        // Power buttons
        setVisible(120, tab == ServerMenu.TAB_POWER);

        // Yield buttons
        setVisible(130, tab == ServerMenu.TAB_YIELD);
        setVisible(131, tab == ServerMenu.TAB_YIELD);
        setVisible(132, tab == ServerMenu.TAB_YIELD);
        setVisible(133, tab == ServerMenu.TAB_YIELD);
        setVisible(134, tab == ServerMenu.TAB_YIELD);

        // Contracts buttons
        setVisible(140, tab == ServerMenu.TAB_CONTRACTS);
        setVisible(141, tab == ServerMenu.TAB_CONTRACTS);

        // Overview buttons
        setVisible(102, tab == ServerMenu.TAB_OVERVIEW);
        setVisible(103, tab == ServerMenu.TAB_OVERVIEW);
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

        // Outer border + inner fill
        guiGraphics.fill(x, y, x + this.imageWidth, y + this.imageHeight, 0xFF101922);
        guiGraphics.fill(x + 4, y + 4, x + this.imageWidth - 4, y + this.imageHeight - 4, 0xFF1A2632);

        // Common info panel (wallet / prices / orders)
        guiGraphics.fill(x + PANEL_LEFT, y + INFO_TOP, x + PANEL_RIGHT, y + INFO_BOTTOM, 0xFF14202B);

        // Tab content panel
        guiGraphics.fill(x + PANEL_LEFT, y + CONTENT_TOP, x + PANEL_RIGHT, y + CONTENT_BOT, 0xFF111C27);

        // Side slot panel (grid upgrades + badge)
        guiGraphics.fill(x + SIDE_LEFT, y + INFO_TOP, x + SIDE_RIGHT, y + SIDE_BOTTOM, 0xFF1E3044);

        // Thin divider between info and content panels
        guiGraphics.fill(x + PANEL_LEFT, y + INFO_BOTTOM, x + PANEL_RIGHT, y + CONTENT_TOP, 0xFF0D1820);

        // Active tab indicator
        renderTabIndicator(guiGraphics, x, y);
    }

    private void renderTabIndicator(GuiGraphics guiGraphics, int x, int y) {
        int tab = menu.getCurrentTab();
        int[] tabX = {4, 48, 92, 146, 188};
        if (tab >= 0 && tab < tabX.length) {
            guiGraphics.fill(x + tabX[tab], y + 20, x + tabX[tab] + 40, y + 22, 0xFF4CB8E8);
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, 8, 5, 0xE7EDF7, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, 10, this.inventoryLabelY, 0xC7D2E0, false);

        // Common info: always visible
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.wallet_line",
                menu.getWalletBalance(CoinType.BITCOIN), menu.getWalletBalance(CoinType.ETHEREUM)),
                PANEL_LEFT + 4, INFO_TOP + 4, 0xD7E5F2, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.price_line",
                menu.getFairPrice(CoinType.BITCOIN), menu.getFairPrice(CoinType.ETHEREUM)),
                PANEL_LEFT + 4, INFO_TOP + 16, 0x93A4B7, false);
        guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.orders_line",
                menu.getActiveOrders(), menu.getActivePositions(), menu.getMintedCollectibles()),
                PANEL_LEFT + 4, INFO_TOP + 28, 0x93A4B7, false);

        // Tab-specific content
        int tab = menu.getCurrentTab();
        int cx = PANEL_LEFT + 4;
        int cy = CONTENT_TOP + 4;

        if (tab == ServerMenu.TAB_OVERVIEW) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_rigs", menu.getRigCount()), cx, cy, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_power", menu.getAvailablePower(), menu.getGridCap()), cx, cy + 12, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_battery", menu.getBatteryStored()), cx, cy + 24, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_arbitrage",
                    ExchangeId.fromId(menu.getBestBuyExchangeId()).name(),
                    ExchangeId.fromId(menu.getBestSellExchangeId()).name(),
                    menu.getArbitrageSpread()), cx, cy + 36, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.overview_withdraw"), cx, cy + 50, 0x6B8FAB, false);

        } else if (tab == ServerMenu.TAB_POWER) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_generation", menu.getSolarCount(), menu.getWindCount()), cx, cy, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_storage", menu.getBatteryStored(), menu.getGridCap()), cx, cy + 12, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.power_bill", menu.getBillDue()), cx, cy + 24, 0x93A4B7, false);

        } else if (tab == ServerMenu.TAB_EXCHANGE) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.exchange_selected", menu.getSelectedExchange().name()), cx, cy, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.exchange_health", menu.getExchangeHealth()), cx, cy + 12, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.exchange_arbitrage", menu.getArbitrageSpread()), cx, cy + 24, 0x93A4B7, false);

        } else if (tab == ServerMenu.TAB_YIELD) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.yield_staking", menu.getStakeApyPercent()), cx, cy, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.yield_lending", menu.getLendApyPercent()), cx, cy + 12, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.yield_positions", menu.getActivePositions()), cx, cy + 24, 0x93A4B7, false);

        } else if (tab == ServerMenu.TAB_CONTRACTS) {
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.contracts_skin"), cx, cy, 0xDCE6F2, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.contracts_badge"), cx, cy + 12, 0x93A4B7, false);
            guiGraphics.drawString(this.font, Component.translatable("screen.cryptoworld.contracts_minted", menu.getMintedCollectibles()), cx, cy + 24, 0x93A4B7, false);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
