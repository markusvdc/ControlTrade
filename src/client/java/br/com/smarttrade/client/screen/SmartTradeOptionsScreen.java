package br.com.smarttrade.client.screen;

import br.com.smarttrade.client.screen.component.ActionButtons;
import br.com.smarttrade.client.screen.component.GlobalOptionEntry;
import br.com.smarttrade.client.screen.component.SummaryPanel;
import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SmartTradeOptionsScreen extends Screen {
	private static final int MAX_CONTENT_WIDTH = 540;
	private static final int SIDE_MARGIN = 16;

	private final Screen parent;
	private final SummaryPanel summaryPanel = new SummaryPanel();
	private Component status = Component.empty();
	private int statusColor = 0xFF9CD67A;
	private boolean showAdditionalInformation;
	private boolean maximumVillagerReputation;
	private boolean soulSpeedOnlyInNether;
	private boolean fixedHugeMushroomHeight;
	private GlobalOptionEntry jadeReputationEntry;
	private GlobalOptionEntry maximumReputationEntry;
	private GlobalOptionEntry soulSpeedEntry;
	private GlobalOptionEntry mushroomHeightEntry;

	public SmartTradeOptionsScreen(Screen parent) {
		super(Component.translatable("smarttrade.options.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int contentWidth = Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
		int left = (this.width - contentWidth) / 2;
		int buttonY = this.height - 36;
		this.showAdditionalInformation = SmartTradeConfig.showAdditionalInformation();
		this.maximumVillagerReputation = SmartTradeConfig.maximumVillagerReputation();
		this.soulSpeedOnlyInNether = SmartTradeConfig.soulSpeedOnlyInNether();
		this.fixedHugeMushroomHeight = SmartTradeConfig.fixedHugeMushroomHeight();
		this.jadeReputationEntry = this.addRenderableWidget(new GlobalOptionEntry(
			left,
			137,
			contentWidth,
			34,
			Component.translatable("smarttrade.options.jade_reputation"),
			Component.translatable("smarttrade.options.jade_reputation.tooltip"),
			this.showAdditionalInformation,
			selected -> this.showAdditionalInformation = selected
		));
		this.maximumReputationEntry = this.addRenderableWidget(new GlobalOptionEntry(
			left,
			175,
			contentWidth,
			34,
			Component.translatable("smarttrade.options.maximum_reputation"),
			Component.translatable("smarttrade.options.maximum_reputation.tooltip"),
			this.maximumVillagerReputation,
			selected -> this.maximumVillagerReputation = selected
		));
		this.soulSpeedEntry = this.addRenderableWidget(new GlobalOptionEntry(
			left,
			213,
			contentWidth,
			34,
			Component.translatable("smarttrade.options.soul_speed_nether"),
			Component.translatable("smarttrade.options.soul_speed_nether.tooltip"),
			this.soulSpeedOnlyInNether,
			selected -> this.soulSpeedOnlyInNether = selected
		));
		this.mushroomHeightEntry = this.addRenderableWidget(new GlobalOptionEntry(
			left,
			251,
			contentWidth,
			34,
			Component.translatable("smarttrade.options.fixed_mushroom_height"),
			Component.translatable("smarttrade.options.fixed_mushroom_height.tooltip"),
			this.fixedHugeMushroomHeight,
			selected -> this.fixedHugeMushroomHeight = selected
		));
		ActionButtons actionButtons = new ActionButtons(
			left,
			buttonY,
			contentWidth,
			this::onClose,
			() -> {
			},
			this::toggleAllOptions,
			this::applyOptions,
			true
		);
		actionButtons.addTo(this::addRenderableWidget);
	}

	private void toggleAllOptions() {
		boolean selected = !(
			this.showAdditionalInformation
				&& this.maximumVillagerReputation
				&& this.soulSpeedOnlyInNether
				&& this.fixedHugeMushroomHeight
		);
		this.jadeReputationEntry.setSelected(selected);
		this.maximumReputationEntry.setSelected(selected);
		this.soulSpeedEntry.setSelected(selected);
		this.mushroomHeightEntry.setSelected(selected);
	}

	private void applyOptions() {
		boolean saved = SmartTradeConfig.saveGlobalOptions(
			this.showAdditionalInformation,
			this.maximumVillagerReputation,
			this.soulSpeedOnlyInNether,
			this.fixedHugeMushroomHeight
		);
		this.status = Component.translatable(
			saved ? "smarttrade.options.status.applied" : "smarttrade.status.save_failed"
		);
		this.statusColor = saved ? 0xFF9CD67A : 0xFFFF6B6B;
	}

	@Override
	public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		graphics.fill(0, 0, this.width, this.height, 0xD0101010);

		int contentWidth = Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
		int left = (this.width - contentWidth) / 2;
		graphics.centeredText(
			this.font,
			Component.translatable("smarttrade.title"),
			this.width / 2,
			14,
			0xFFFFFFFF
		);
		graphics.centeredText(
			this.font,
			Component.translatable("smarttrade.options.subtitle"),
			this.width / 2,
			29,
			0xFFBDBDBD
		);
		this.summaryPanel.render(graphics, this.font, left, 47, contentWidth);
		graphics.text(this.font, this.title, left + 4, 123, 0xFFE0E0E0, true);

		super.extractRenderState(graphics, mouseX, mouseY, delta);
		if (!this.status.getString().isEmpty()) {
			graphics.centeredText(this.font, this.status, this.width / 2, this.height - 49, this.statusColor);
		}
		this.renderGlobalOptionTooltip(graphics, mouseX, mouseY);
	}

	private void renderGlobalOptionTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		GlobalOptionEntry[] entries = {
			this.jadeReputationEntry,
			this.maximumReputationEntry,
			this.soulSpeedEntry,
			this.mushroomHeightEntry
		};
		for (GlobalOptionEntry entry : entries) {
			if (entry.isHovered()) {
				entry.renderTooltip(graphics, mouseX, mouseY);
				return;
			}
		}
		for (GlobalOptionEntry entry : entries) {
			if (entry.isFocused()) {
				entry.renderTooltip(graphics, mouseX, mouseY);
				return;
			}
		}
	}

	@Override
	public void onClose() {
		this.status = Component.empty();
		this.minecraft.gui.setScreen(this.parent);
	}
}
