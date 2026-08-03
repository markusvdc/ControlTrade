package br.com.smarttrade.client.screen.component;

import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.screens.inventory.tooltip.BelowOrAboveWidgetTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.client.gui.screens.inventory.tooltip.MenuTooltipPositioner;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

public final class GlobalOptionEntry extends AbstractButton {
	private static final int VANILLA_TOOLTIP_MAX_WIDTH = 170;
	private static final int TOOLTIP_MAX_WIDTH = VANILLA_TOOLTIP_MAX_WIDTH * 5 / 2;

	private final Consumer<Boolean> onValueChange;
	private final OptionTooltip tooltip;
	private boolean selected;

	public GlobalOptionEntry(
		int x,
		int y,
		int width,
		int height,
		Component label,
		OptionTooltip tooltip,
		boolean selected,
		Consumer<Boolean> onValueChange
	) {
		super(x, y, width, height, label);
		this.selected = selected;
		this.onValueChange = onValueChange;
		this.tooltip = tooltip;
	}

	@Override
	public void onPress(InputWithModifiers input) {
		setSelected(!this.selected);
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		this.onValueChange.accept(selected);
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		int x = this.getX();
		int y = this.getY();
		boolean highlighted = this.isHoveredOrFocused();

		graphics.fill(x, y + 1, x + this.width, y + this.height - 2, highlighted ? 0xCC333333 : 0x99202020);
		graphics.fill(x, y + 1, x + 1, y + this.height - 2, this.selected ? 0xFF79C64A : 0xFF555555);
		if (highlighted) {
			graphics.outline(x, y + 1, this.width, this.height - 3, 0xFFFFFFFF);
		}

		drawCheckbox(graphics, x + 8, y + 9);
		graphics.text(Minecraft.getInstance().font, this.getMessage(), x + 29, y + 11, 0xFFFFFFFF, true);
	}

	private void drawCheckbox(GuiGraphicsExtractor graphics, int x, int y) {
		graphics.fill(x, y, x + 11, y + 11, 0xFF111111);
		graphics.fill(x + 1, y + 1, x + 10, y + 10, 0xFF8B8B8B);
		graphics.fill(x + 2, y + 2, x + 9, y + 9, 0xFF252525);
		if (this.selected) {
			graphics.fill(x + 3, y + 5, x + 5, y + 8, 0xFF8EE36B);
			graphics.fill(x + 5, y + 7, x + 7, y + 9, 0xFF8EE36B);
			graphics.fill(x + 7, y + 3, x + 9, y + 8, 0xFF8EE36B);
		}
	}

	public void renderTooltip(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean focusedByKeyboard = this.isFocused() && minecraft.getLastInputType().isKeyboard();
		if (!this.isHovered() && !focusedByKeyboard) {
			return;
		}

		Font font = minecraft.font;
		List<FormattedCharSequence> wrappedLines = this.tooltip.split(font, TOOLTIP_MAX_WIDTH);
		List<ClientTooltipComponent> lines = java.util.stream.IntStream.range(0, wrappedLines.size())
			.mapToObj(index -> new GlobalOptionTooltipLine(
				wrappedLines.get(index),
				index == 0,
				index == wrappedLines.size() - 1
			))
			.map(ClientTooltipComponent.class::cast)
			.toList();
		ClientTooltipPositioner positioner = focusedByKeyboard && !this.isHovered()
			? new BelowOrAboveWidgetTooltipPositioner(this.getRectangle())
			: new MenuTooltipPositioner(this.getRectangle());
		graphics.tooltip(font, lines, mouseX, mouseY, positioner, null);
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput output) {
		this.defaultButtonNarrationText(output);
		output.add(NarratedElementType.HINT, this.tooltip.narration());
	}
}
