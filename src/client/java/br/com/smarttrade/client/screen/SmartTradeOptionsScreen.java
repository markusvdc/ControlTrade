package br.com.smarttrade.client.screen;

import br.com.smarttrade.client.screen.component.ActionButtons;
import br.com.smarttrade.client.screen.component.AlphabeticalOrder;
import br.com.smarttrade.client.screen.component.GlobalOptionEntry;
import br.com.smarttrade.client.screen.component.OptionTooltip;
import br.com.smarttrade.client.screen.component.SummaryPanel;
import br.com.smarttrade.config.SmartTradeConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SmartTradeOptionsScreen extends Screen {
	private static final int MAX_CONTENT_WIDTH = 540;
	private static final int SIDE_MARGIN = 16;
	private static final int OPTIONS_TOP = 137;
	private static final int OPTION_HEIGHT = 30;

	private final Screen parent;
	private final SummaryPanel summaryPanel = new SummaryPanel();
	private Component status = Component.empty();
	private int statusColor = 0xFF9CD67A;
	private boolean showAdditionalInformation;
	private boolean maximumVillagerReputation;
	private boolean soulSpeedOnlyInNether;
	private boolean fixedHugeMushroomHeight;
	private boolean compactHorseHealthHud;
	private boolean equestrianHud;
	private boolean automaticDoorClosing;
	private boolean disableChatHistoryNavigation;
	private boolean compactItemCounts;
	private boolean expandedItemStacks;
	private GlobalOptionEntry jadeReputationEntry;
	private GlobalOptionEntry maximumReputationEntry;
	private GlobalOptionEntry soulSpeedEntry;
	private GlobalOptionEntry mushroomHeightEntry;
	private GlobalOptionEntry horseHealthHudEntry;
	private GlobalOptionEntry equestrianHudEntry;
	private GlobalOptionEntry automaticDoorClosingEntry;
	private GlobalOptionEntry chatHistoryNavigationEntry;
	private GlobalOptionEntry compactItemCountsEntry;
	private GlobalOptionEntry expandedItemStacksEntry;

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
		this.compactHorseHealthHud = SmartTradeConfig.compactHorseHealthHud();
		this.equestrianHud = SmartTradeConfig.equestrianHud();
		this.automaticDoorClosing = SmartTradeConfig.automaticDoorClosing();
		this.disableChatHistoryNavigation = SmartTradeConfig.disableChatHistoryNavigation();
		this.compactItemCounts = SmartTradeConfig.compactItemCounts();
		this.expandedItemStacks = SmartTradeConfig.expandedItemStacks();
		this.jadeReputationEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.jade_reputation"),
			OptionTooltip.translated("smarttrade.options.jade_reputation"),
			this.showAdditionalInformation,
			selected -> this.showAdditionalInformation = selected
		);
		this.maximumReputationEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.maximum_reputation"),
			OptionTooltip.translated("smarttrade.options.maximum_reputation"),
			this.maximumVillagerReputation,
			selected -> this.maximumVillagerReputation = selected
		);
		this.soulSpeedEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.soul_speed_nether"),
			OptionTooltip.translated("smarttrade.options.soul_speed_nether"),
			this.soulSpeedOnlyInNether,
			selected -> this.soulSpeedOnlyInNether = selected
		);
		this.mushroomHeightEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.fixed_mushroom_height"),
			OptionTooltip.translated("smarttrade.options.fixed_mushroom_height"),
			this.fixedHugeMushroomHeight,
			selected -> this.fixedHugeMushroomHeight = selected
		);
		this.horseHealthHudEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.compact_horse_health_hud"),
			OptionTooltip.translated("smarttrade.options.compact_horse_health_hud"),
			this.compactHorseHealthHud,
			selected -> this.compactHorseHealthHud = selected
		);
		this.equestrianHudEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.equestrian_hud"),
			OptionTooltip.translated("smarttrade.options.equestrian_hud"),
			this.equestrianHud,
			selected -> this.equestrianHud = selected
		);
		this.automaticDoorClosingEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.automatic_door_closing"),
			OptionTooltip.translated("smarttrade.options.automatic_door_closing"),
			this.automaticDoorClosing,
			selected -> this.automaticDoorClosing = selected
		);
		this.chatHistoryNavigationEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.disable_chat_history_navigation"),
			OptionTooltip.translated("smarttrade.options.disable_chat_history_navigation"),
			this.disableChatHistoryNavigation,
			selected -> this.disableChatHistoryNavigation = selected
		);
		this.compactItemCountsEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.compact_item_counts"),
			OptionTooltip.translated("smarttrade.options.compact_item_counts"),
			this.compactItemCounts,
			selected -> this.compactItemCounts = selected
		);
		this.expandedItemStacksEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.expanded_item_stacks"),
			OptionTooltip.translated("smarttrade.options.expanded_item_stacks"),
			this.expandedItemStacks,
			selected -> this.expandedItemStacks = selected
		);
		List<GlobalOptionEntry> entries = new ArrayList<>(List.of(
			this.jadeReputationEntry,
			this.maximumReputationEntry,
			this.soulSpeedEntry,
			this.mushroomHeightEntry,
			this.horseHealthHudEntry,
			this.equestrianHudEntry,
			this.automaticDoorClosingEntry,
			this.chatHistoryNavigationEntry,
			this.compactItemCountsEntry,
			this.expandedItemStacksEntry
		));
		Comparator<Component> alphabeticalOrder = AlphabeticalOrder.components(this.minecraft);
		entries.sort(Comparator.comparing(GlobalOptionEntry::getMessage, alphabeticalOrder));
		for (int index = 0; index < entries.size(); index++) {
			GlobalOptionEntry entry = entries.get(index);
			entry.setY(OPTIONS_TOP + OPTION_HEIGHT * index);
			this.addRenderableWidget(entry);
		}
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
				&& this.compactHorseHealthHud
				&& this.equestrianHud
				&& this.automaticDoorClosing
				&& this.disableChatHistoryNavigation
				&& this.compactItemCounts
				&& this.expandedItemStacks
		);
		this.jadeReputationEntry.setSelected(selected);
		this.maximumReputationEntry.setSelected(selected);
		this.soulSpeedEntry.setSelected(selected);
		this.mushroomHeightEntry.setSelected(selected);
		this.horseHealthHudEntry.setSelected(selected);
		this.equestrianHudEntry.setSelected(selected);
		this.automaticDoorClosingEntry.setSelected(selected);
		this.chatHistoryNavigationEntry.setSelected(selected);
		this.compactItemCountsEntry.setSelected(selected);
		this.expandedItemStacksEntry.setSelected(selected);
	}

	private void applyOptions() {
		boolean saved = SmartTradeConfig.saveGlobalOptions(
			this.showAdditionalInformation,
			this.maximumVillagerReputation,
			this.soulSpeedOnlyInNether,
			this.fixedHugeMushroomHeight,
			this.compactHorseHealthHud,
			this.equestrianHud,
			this.automaticDoorClosing,
			this.disableChatHistoryNavigation,
			this.compactItemCounts,
			this.expandedItemStacks
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
			this.mushroomHeightEntry,
			this.horseHealthHudEntry,
			this.equestrianHudEntry,
			this.automaticDoorClosingEntry,
			this.chatHistoryNavigationEntry,
			this.compactItemCountsEntry,
			this.expandedItemStacksEntry
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
