package br.com.smarttrade.client.screen;

import br.com.smarttrade.client.screen.component.ActionButtons;
import br.com.smarttrade.client.screen.component.SelectionList;
import br.com.smarttrade.client.screen.component.SummaryPanel;
import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SmartTradeScreen extends Screen {
	private static final int MAX_CONTENT_WIDTH = 540;
	private static final int SIDE_MARGIN = 16;

	private final Screen parent;
	private final SummaryPanel summaryPanel = new SummaryPanel();
	private SelectionList selectionList;
	private Component status = Component.empty();
	private int statusColor = 0xFF9CD67A;

	public SmartTradeScreen(Screen parent) {
		super(Component.translatable("smarttrade.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int contentWidth = Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
		int left = (this.width - contentWidth) / 2;
		int listTop = 137;
		int buttonY = this.height - 36;
		int listBottom = Math.max(listTop + 56, buttonY - 12);

		this.selectionList = new SelectionList(this.minecraft, contentWidth, listBottom - listTop, listTop, 30);
		this.selectionList.setX(left);
		this.addRenderableWidget(this.selectionList);

		ActionButtons actionButtons = new ActionButtons(
			left,
			buttonY,
			contentWidth,
			this::onClose,
			this::openOptions,
			this::toggleAllEntries,
			this::applyConfiguration,
			false
		);
		actionButtons.addTo(this::addRenderableWidget);
	}

	private void openOptions() {
		this.clearStatus();
		this.minecraft.gui.setScreen(new SmartTradeOptionsScreen(this));
	}

	private void clearStatus() {
		this.status = Component.empty();
	}

	private void applyConfiguration() {
		boolean saved = SmartTradeConfig.saveEnabledTrades(this.selectionList.selectedIds());
		this.status = Component.translatable(saved ? "smarttrade.status.applied" : "smarttrade.status.save_failed");
		this.statusColor = saved ? 0xFF9CD67A : 0xFFFF6B6B;
	}

	private void toggleAllEntries() {
		this.selectionList.setAllSelected(!this.selectionList.areAllSelected());
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		graphics.fill(0, 0, this.width, this.height, 0xD0101010);

		int contentWidth = Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
		int left = (this.width - contentWidth) / 2;
		graphics.centeredText(this.font, this.title, this.width / 2, 14, 0xFFFFFFFF);
		graphics.centeredText(
			this.font,
			Component.translatable("smarttrade.subtitle"),
			this.width / 2,
			29,
			0xFFBDBDBD
		);
		this.summaryPanel.render(graphics, this.font, left, 47, contentWidth);
		graphics.text(this.font, Component.translatable("smarttrade.list.title"), left + 4, 123, 0xFFE0E0E0, true);

		super.extractRenderState(graphics, mouseX, mouseY, delta);
		if (!this.status.getString().isEmpty()) {
			graphics.centeredText(this.font, this.status, this.width / 2, this.height - 49, this.statusColor);
		}
		this.selectionList.renderTooltip(graphics, mouseX, mouseY);
	}

	@Override
	public void onClose() {
		this.clearStatus();
		this.minecraft.gui.setScreen(this.parent);
	}
}
