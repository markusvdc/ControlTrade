package br.com.smarttrade.client.screen.component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Items;

public final class SelectionList extends AbstractWidget {
	private static final int SCROLLBAR_WIDTH = 6;
	private static final int SCROLLBAR_GAP = 6;
	private static final int TOOLTIP_MAX_WIDTH = 425;

	private final List<SelectionEntry> entries;
	private final int rowHeight;
	private boolean draggingScrollbar;
	private double scrollAmount;

	public SelectionList(Minecraft minecraft, int width, int height, int y, int rowHeight) {
		super(0, y, width, height, Component.translatable("smarttrade.list.title"));
		this.rowHeight = rowHeight;
		this.entries = sortWithinCategories(minecraft, List.of(
			SelectionEntry.category(minecraft, "smarttrade.category.farmer"),
			new SelectionEntry(minecraft, Items.EGG, 20, SmartTradeConfig.isTradeEnabled(Items.EGG)),
			new SelectionEntry(minecraft, Items.COCOA_BEANS, 20, SmartTradeConfig.isTradeEnabled(Items.COCOA_BEANS)),
			new SelectionEntry(minecraft, Items.HONEYCOMB, 10, SmartTradeConfig.isTradeEnabled(Items.HONEYCOMB)),

			SelectionEntry.category(minecraft, "smarttrade.category.cleric"),
			new SelectionEntry(minecraft, Items.SPIDER_EYE, 15, SmartTradeConfig.isTradeEnabled(Items.SPIDER_EYE)),
			new SelectionEntry(minecraft, Items.ENDER_PEARL, 3, SmartTradeConfig.isTradeEnabled(Items.ENDER_PEARL)),
			new SelectionEntry(minecraft, Items.REDSTONE, 20, SmartTradeConfig.isTradeEnabled(Items.REDSTONE)),
			new SelectionEntry(minecraft, Items.LAPIS_LAZULI, 20, SmartTradeConfig.isTradeEnabled(Items.LAPIS_LAZULI)),

			SelectionEntry.category(minecraft, "smarttrade.category.butcher"),
			new SelectionEntry(minecraft, Items.BONE, 20, SmartTradeConfig.isTradeEnabled(Items.BONE)),

			SelectionEntry.category(minecraft, "smarttrade.category.fletcher"),
			new SelectionEntry(minecraft, Items.ARROW, 15, SmartTradeConfig.isTradeEnabled(Items.ARROW))
		));
	}

	private static List<SelectionEntry> sortWithinCategories(Minecraft minecraft, List<SelectionEntry> entries) {
		List<SelectionEntry> sorted = new ArrayList<>(entries);
		Comparator<SelectionEntry> comparator = Comparator.comparing(
			SelectionEntry::name,
			AlphabeticalOrder.components(minecraft)
		);
		int categoryStart = 0;
		while (categoryStart < sorted.size()) {
			int nextCategory = categoryStart + 1;
			while (nextCategory < sorted.size() && !sorted.get(nextCategory).isCategory()) {
				nextCategory++;
			}
			sorted.subList(categoryStart + 1, nextCategory).sort(comparator);
			categoryStart = nextCategory;
		}
		return List.copyOf(sorted);
	}

	@Override
	protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		int x = getX();
		int y = getY();
		int contentWidth = this.width - SCROLLBAR_WIDTH - SCROLLBAR_GAP;
		boolean needsScrollbar = getMaxScroll() > 0;

		graphics.enableScissor(x, y, x + this.width, y + this.height);
		graphics.fill(x, y, x + this.width, y + this.height, 0xB8101010);
		for (int index = 0; index < this.entries.size(); index++) {
			int rowY = getRowY(index);
			if (isRowVisible(rowY)) {
				SelectionEntry entry = this.entries.get(index);
				boolean hovered = !entry.isCategory()
					&& mouseX >= x
					&& mouseX < x + contentWidth
					&& mouseY >= rowY
					&& mouseY < rowY + this.rowHeight;
				entry.renderBackground(graphics, x, rowY, contentWidth, this.rowHeight, hovered);
			}
		}
		if (needsScrollbar) {
			drawScrollbarTrack(graphics);
		}
		graphics.disableScissor();

		graphics.nextStratum();
		graphics.enableScissor(x, y, x + this.width, y + this.height);
		for (int index = 0; index < this.entries.size(); index++) {
			int rowY = getRowY(index);
			if (isRowVisible(rowY)) {
				this.entries.get(index).renderContent(graphics, x, rowY);
			}
		}
		if (needsScrollbar) {
			drawScrollbarThumb(graphics);
		}
		graphics.disableScissor();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		if (event.button() != 0 || !isMouseOver(event.x(), event.y())) {
			return false;
		}
		int contentWidth = this.width - SCROLLBAR_WIDTH - SCROLLBAR_GAP;
		if (event.x() >= getX() + contentWidth && getMaxScroll() > 0) {
			this.draggingScrollbar = true;
			setScrollFromMouse(event.y());
			return true;
		}

		int index = (int) ((event.y() - getY() + this.scrollAmount) / this.rowHeight);
		if (index >= 0 && index < this.entries.size()) {
			SelectionEntry entry = this.entries.get(index);
			if (!entry.isCategory()) {
				entry.toggle();
				playDownSound(Minecraft.getInstance().getSoundManager());
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseDragged(MouseButtonEvent event, double offsetX, double offsetY) {
		if (!this.draggingScrollbar || event.button() != 0) {
			return false;
		}
		setScrollFromMouse(event.y());
		return true;
	}

	@Override
	public boolean mouseReleased(MouseButtonEvent event) {
		if (event.button() == 0 && this.draggingScrollbar) {
			this.draggingScrollbar = false;
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
		if (!isMouseOver(mouseX, mouseY)) {
			return false;
		}
		this.scrollAmount = Mth.clamp(this.scrollAmount - verticalAmount * this.rowHeight, 0.0, getMaxScroll());
		return true;
	}

	public void setAllSelected(boolean selected) {
		this.entries.forEach(entry -> entry.setSelected(selected));
	}

	public boolean areAllSelected() {
		return this.entries.stream()
			.filter(entry -> !entry.isCategory())
			.allMatch(SelectionEntry::isSelected);
	}

	public boolean isSelected(net.minecraft.world.item.Item item) {
		return this.entries.stream()
			.filter(entry -> !entry.isCategory() && entry.represents(item))
			.findFirst()
			.map(SelectionEntry::isSelected)
			.orElse(false);
	}

	public Set<Identifier> selectedIds() {
		return this.entries.stream()
			.filter(entry -> !entry.isCategory() && entry.isSelected())
			.map(SelectionEntry::itemId)
			.collect(Collectors.toUnmodifiableSet());
	}

	public void renderTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		int contentWidth = this.width - SCROLLBAR_WIDTH - SCROLLBAR_GAP;
		if (mouseX < getX() || mouseX >= getX() + contentWidth || mouseY < getY() || mouseY >= getY() + this.height) {
			return;
		}

		int index = (int) ((mouseY - getY() + this.scrollAmount) / this.rowHeight);
		if (index < 0 || index >= this.entries.size()) {
			return;
		}

		SelectionEntry entry = this.entries.get(index);
		if (entry.isCategory()) {
			return;
		}

		List<FormattedCharSequence> wrappedLines = entry.tooltip().split(
			Minecraft.getInstance().font,
			TOOLTIP_MAX_WIDTH
		);
		List<ClientTooltipComponent> lines = java.util.stream.IntStream.range(0, wrappedLines.size())
			.mapToObj(lineIndex -> new GlobalOptionTooltipLine(
				wrappedLines.get(lineIndex),
				lineIndex == 0,
				lineIndex == wrappedLines.size() - 1
			))
			.map(ClientTooltipComponent.class::cast)
			.toList();
		graphics.tooltip(
			Minecraft.getInstance().font,
			lines,
			mouseX,
			mouseY,
			DefaultTooltipPositioner.INSTANCE,
			null
		);
	}

	private int getRowY(int index) {
		return getY() + index * this.rowHeight - (int) this.scrollAmount;
	}

	private boolean isRowVisible(int rowY) {
		return rowY + this.rowHeight > getY() && rowY < getY() + this.height;
	}

	private int getMaxScroll() {
		return Math.max(0, this.entries.size() * this.rowHeight - this.height);
	}

	private void setScrollFromMouse(double mouseY) {
		int travel = this.height - getThumbHeight();
		if (travel <= 0) {
			this.scrollAmount = 0;
			return;
		}
		double relative = Mth.clamp((mouseY - getY() - getThumbHeight() / 2.0) / travel, 0.0, 1.0);
		this.scrollAmount = relative * getMaxScroll();
	}

	private int getThumbHeight() {
		int contentHeight = Math.max(this.rowHeight, this.entries.size() * this.rowHeight);
		return Math.max(24, this.height * this.height / contentHeight);
	}

	private int getThumbY() {
		int travel = this.height - getThumbHeight();
		return getY() + (getMaxScroll() == 0 ? 0 : (int) (travel * this.scrollAmount / getMaxScroll()));
	}

	private void drawScrollbarTrack(GuiGraphicsExtractor graphics) {
		int scrollbarX = getX() + this.width - SCROLLBAR_WIDTH;
		graphics.fill(scrollbarX, getY(), scrollbarX + SCROLLBAR_WIDTH, getY() + this.height, 0xFF080808);
	}

	private void drawScrollbarThumb(GuiGraphicsExtractor graphics) {
		int scrollbarX = getX() + this.width - SCROLLBAR_WIDTH;
		int thumbY = getThumbY();
		graphics.fill(scrollbarX, thumbY, scrollbarX + SCROLLBAR_WIDTH, thumbY + getThumbHeight(), 0xFFC0C0C0);
		graphics.fill(scrollbarX, thumbY, scrollbarX + 1, thumbY + getThumbHeight(), 0xFFFFFFFF);
		graphics.fill(
			scrollbarX + SCROLLBAR_WIDTH - 1,
			thumbY,
			scrollbarX + SCROLLBAR_WIDTH,
			thumbY + getThumbHeight(),
			0xFF707070
		);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		defaultButtonNarrationText(output);
	}
}
