package br.com.smarttrade.client.gameplay;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

/** Rendering behavior adapted from Reliable Recount/O123456789 by Evan and catboybinary. */
public final class CompactItemCountRenderer {
	private static final FontDescription FONT = new FontDescription.Resource(
		Identifier.fromNamespaceAndPath("smarttrade", "3x5")
	);
	private static final int OUTLINE_COLOR = ARGB.opaque(0x3A3A3A);

	private CompactItemCountRenderer() {
	}

	public static void draw(
		GuiGraphicsExtractor graphics,
		Font font,
		String text,
		int y,
		int color,
		int itemX
	) {
		MutableComponent component = Component.literal(text).withStyle(Style.EMPTY.withFont(FONT));
		int centerX = itemX + 16 - font.width(component);
		for (int offsetY = -1; offsetY <= 1; offsetY++) {
			for (int offsetX = -1; offsetX <= 1; offsetX++) {
				if (offsetX != 0 || offsetY != 0) {
					graphics.text(font, component, centerX + offsetX, y + offsetY, OUTLINE_COLOR, false);
				}
			}
		}
		graphics.text(font, component, centerX, y, color, false);
	}
}
