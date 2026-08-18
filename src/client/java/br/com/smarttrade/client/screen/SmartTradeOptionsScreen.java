package br.com.smarttrade.client.screen;

import br.com.smarttrade.client.screen.component.ActionButtons;
import br.com.smarttrade.client.screen.component.AlphabeticalOrder;
import br.com.smarttrade.client.screen.component.CategoryDivider;
import br.com.smarttrade.client.screen.component.GlobalOptionEntry;
import br.com.smarttrade.client.screen.component.OptionTooltip;
import br.com.smarttrade.client.screen.component.SummaryPanel;
import br.com.smarttrade.config.SmartTradeConfig;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class SmartTradeOptionsScreen extends Screen {
	private static final int MAX_CONTENT_WIDTH = 540;
	private static final int SIDE_MARGIN = 16;
	private static final int OPTIONS_TOP = 137;
	private static final int OPTION_HEIGHT = 30;
	private static final int VISIBLE_ROW_COUNT = 13;
	private static final int SCROLLBAR_WIDTH = 6;
	private static final int SCROLLBAR_GAP = 6;

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
	private boolean randomExperienceOrbColors;
	private boolean compactInformationOverlay;
	private boolean compactGameMenus;
	private boolean sovereignShift;
	private boolean sovereignSeal;
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
	private GlobalOptionEntry randomExperienceOrbColorsEntry;
	private GlobalOptionEntry compactInformationOverlayEntry;
	private GlobalOptionEntry compactGameMenusEntry;
	private GlobalOptionEntry sovereignShiftEntry;
	private GlobalOptionEntry sovereignSealEntry;
	private List<AbstractWidget> optionRows = List.of();
	private int optionRowHeight;
	private int optionsBottom;
	private int scrollRow;

	public SmartTradeOptionsScreen(Screen parent) {
		super(Component.translatable("smarttrade.options.title"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		int contentWidth = Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
		int left = (this.width - contentWidth) / 2;
		int buttonY = this.height - 36;
		int rowHeight = Math.min(OPTION_HEIGHT, (buttonY - 12 - OPTIONS_TOP) / VISIBLE_ROW_COUNT);
		int rowWidth = contentWidth;
		this.optionRowHeight = rowHeight;
		this.optionsBottom = buttonY - 12;
		this.scrollRow = 0;
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
		this.randomExperienceOrbColors = SmartTradeConfig.randomExperienceOrbColors();
		this.compactInformationOverlay = SmartTradeConfig.compactInformationOverlay();
		this.compactGameMenus = SmartTradeConfig.compactGameMenus();
		this.sovereignShift = SmartTradeConfig.sovereignShift();
		this.sovereignSeal = SmartTradeConfig.sovereignSeal();
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
		this.randomExperienceOrbColorsEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.random_experience_orb_colors"),
			OptionTooltip.translated("smarttrade.options.random_experience_orb_colors"),
			this.randomExperienceOrbColors,
			selected -> this.randomExperienceOrbColors = selected
		);
		this.compactInformationOverlayEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.compact_information_overlay"),
			OptionTooltip.translated("smarttrade.options.compact_information_overlay"),
			this.compactInformationOverlay,
			selected -> this.compactInformationOverlay = selected
		);
		this.compactGameMenusEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.compact_game_menus"),
			OptionTooltip.translated("smarttrade.options.compact_game_menus"),
			this.compactGameMenus,
			selected -> this.compactGameMenus = selected
		);
		this.sovereignShiftEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.sovereign_shift"),
			OptionTooltip.translated("smarttrade.options.sovereign_shift"),
			this.sovereignShift,
			selected -> this.sovereignShift = selected
		);
		this.sovereignSealEntry = new GlobalOptionEntry(
			left,
			0,
			contentWidth,
			OPTION_HEIGHT,
			Component.translatable("smarttrade.options.sovereign_seal"),
			OptionTooltip.translated("smarttrade.options.sovereign_seal"),
			this.sovereignSeal,
			selected -> this.sovereignSeal = selected
		);
		List<GlobalOptionEntry> qualityEntries = new ArrayList<>(List.of(
			this.jadeReputationEntry,
			this.horseHealthHudEntry,
			this.equestrianHudEntry,
			this.automaticDoorClosingEntry,
			this.chatHistoryNavigationEntry,
			this.compactItemCountsEntry,
			this.expandedItemStacksEntry,
			this.compactInformationOverlayEntry,
			this.compactGameMenusEntry,
			this.sovereignSealEntry
		));
		List<GlobalOptionEntry> fantasyEntries = new ArrayList<>(List.of(
			this.maximumReputationEntry,
			this.soulSpeedEntry,
			this.mushroomHeightEntry,
			this.randomExperienceOrbColorsEntry,
			this.sovereignShiftEntry
		));
		Comparator<Component> alphabeticalOrder = AlphabeticalOrder.components(this.minecraft);
		qualityEntries.sort(Comparator.comparing(GlobalOptionEntry::getMessage, alphabeticalOrder));
		fantasyEntries.sort(Comparator.comparing(GlobalOptionEntry::getMessage, alphabeticalOrder));
		List<AbstractWidget> rows = new ArrayList<>();
		rows.add(new CategoryDivider(left, 0, rowWidth, rowHeight,
			Component.translatable("smarttrade.options.category.quality")));
		for (GlobalOptionEntry entry : qualityEntries) {
			entry.setHeight(rowHeight);
			entry.setWidth(rowWidth);
			rows.add(entry);
		}
		rows.add(new CategoryDivider(left, 0, rowWidth, rowHeight,
			Component.translatable("smarttrade.options.category.fantasy")));
		for (GlobalOptionEntry entry : fantasyEntries) {
			entry.setHeight(rowHeight);
			entry.setWidth(rowWidth);
			rows.add(entry);
		}
		this.optionRows = List.copyOf(rows);
		for (AbstractWidget row : this.optionRows) {
			this.addRenderableWidget(row);
		}
		this.layoutOptionRows();
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

	private void layoutOptionRows() {
		for (int index = 0; index < this.optionRows.size(); index++) {
			AbstractWidget row = this.optionRows.get(index);
			int y = OPTIONS_TOP + (index - this.scrollRow) * this.optionRowHeight;
			boolean visible = y >= OPTIONS_TOP && y + this.optionRowHeight <= this.optionsBottom;
			row.setY(y);
			row.visible = visible;
			row.active = visible && !(row instanceof CategoryDivider);
		}
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		int contentWidth = Math.min(MAX_CONTENT_WIDTH, this.width - SIDE_MARGIN * 2);
		int left = (this.width - contentWidth) / 2;
		if (mouseX < left || mouseX >= left + contentWidth + SCROLLBAR_GAP + SCROLLBAR_WIDTH
			|| mouseY < OPTIONS_TOP || mouseY >= this.optionsBottom) {
			return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
		}
		int maxScrollRow = Math.max(0, this.optionRows.size() - VISIBLE_ROW_COUNT);
		int nextScrollRow = Math.max(0, Math.min(maxScrollRow, this.scrollRow - (int)Math.signum(verticalAmount)));
		if (nextScrollRow == this.scrollRow) {
			return false;
		}
		this.scrollRow = nextScrollRow;
		this.layoutOptionRows();
		return true;
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
				&& this.randomExperienceOrbColors
				&& this.compactInformationOverlay
				&& this.compactGameMenus
				&& this.sovereignShift
				&& this.sovereignSeal
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
		this.randomExperienceOrbColorsEntry.setSelected(selected);
		this.compactInformationOverlayEntry.setSelected(selected);
		this.compactGameMenusEntry.setSelected(selected);
		this.sovereignShiftEntry.setSelected(selected);
		this.sovereignSealEntry.setSelected(selected);
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
			this.expandedItemStacks,
			this.randomExperienceOrbColors,
			this.compactInformationOverlay,
			this.compactGameMenus,
			this.sovereignShift,
			this.sovereignSeal
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
		this.renderOptionsScrollbar(graphics, left, contentWidth);
		if (!this.status.getString().isEmpty()) {
			graphics.centeredText(this.font, this.status, this.width / 2, this.height - 49, this.statusColor);
		}
		this.renderGlobalOptionTooltip(graphics, mouseX, mouseY);
	}

	private void renderOptionsScrollbar(GuiGraphicsExtractor graphics, int left, int contentWidth) {
		int maxScrollRow = Math.max(0, this.optionRows.size() - VISIBLE_ROW_COUNT);
		if (maxScrollRow == 0) {
			return;
		}
		int trackHeight = this.optionsBottom - OPTIONS_TOP;
		int thumbHeight = Math.max(24, trackHeight * VISIBLE_ROW_COUNT / this.optionRows.size());
		int travel = trackHeight - thumbHeight;
		int thumbY = OPTIONS_TOP + travel * this.scrollRow / maxScrollRow;
		int scrollbarX = left + contentWidth + SCROLLBAR_GAP;
		graphics.fill(scrollbarX, OPTIONS_TOP, scrollbarX + SCROLLBAR_WIDTH, this.optionsBottom, 0xFF080808);
		graphics.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + thumbHeight, 0xFFC0C0C0);
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
			this.expandedItemStacksEntry,
			this.randomExperienceOrbColorsEntry,
			this.compactInformationOverlayEntry,
			this.compactGameMenusEntry
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
