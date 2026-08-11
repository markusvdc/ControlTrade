package br.com.smarttrade.client.gameplay;

import br.com.smarttrade.config.SmartTradeConfig;
import com.mojang.blaze3d.platform.InputConstants;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import org.lwjgl.glfw.GLFW;

public final class CompactInformationOverlay {
	private static final Identifier ID = Identifier.fromNamespaceAndPath("smarttrade", "compact_information_overlay");
	private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("hh:mm a", Locale.ROOT);
	private static boolean tabWasDown;
	private static boolean visible;

	private CompactInformationOverlay() {
	}

	public static void register() {
		HudElementRegistry.addLast(ID, (graphics, tickCounter) -> render(graphics));
	}

	private static void render(GuiGraphicsExtractor graphics) {
		Minecraft minecraft = Minecraft.getInstance();
		boolean tabIsDown = InputConstants.isKeyDown(minecraft.getWindow(), GLFW.GLFW_KEY_TAB);
		if (tabIsDown && !tabWasDown && SmartTradeConfig.compactInformationOverlay()) {
			visible = !visible;
		}
		tabWasDown = tabIsDown;

		if (!SmartTradeConfig.compactInformationOverlay()) {
			visible = false;
		}
		if (!visible || minecraft.player == null || minecraft.level == null) {
			return;
		}

		Font font = minecraft.font;
		List<OverlayLine> lines = createLines(minecraft);
		for (int index = 0; index < lines.size(); index++) {
			OverlayLine line = lines.get(index);
			int y = 4 + index * 11;
			graphics.fill(2, y - 2, 6 + font.width(line.text()), y + 9, 0x90000000);
			graphics.text(font, line.text(), 4, y, line.color(), false);
		}
	}

	private static List<OverlayLine> createLines(Minecraft minecraft) {
		List<OverlayLine> lines = new ArrayList<>(7);
		Runtime runtime = Runtime.getRuntime();
		long usedMemory = runtime.totalMemory() - runtime.freeMemory();
		long maximumMemory = runtime.maxMemory();
		int memoryPercent = maximumMemory == 0 ? 0 : (int) (usedMemory * 100 / maximumMemory);
		BlockPos position = minecraft.player.blockPosition();
		int fpsLimit = minecraft.options.framerateLimit().get();

		lines.add(line(Component.translatable("smarttrade.overlay.time", LocalTime.now().format(TIME_FORMAT))));
		lines.add(line(Component.translatable(
			"smarttrade.overlay.minecraft_clock",
			minecraftTime(minecraft.level.getOverworldClockTime())
		)));
		lines.add(line(Component.translatable(
			"smarttrade.overlay.memory",
			usedMemory / 1024 / 1024,
			maximumMemory / 1024 / 1024,
			memoryPercent
		), memoryColor(memoryPercent)));
		lines.add(line(Component.translatable(
			"smarttrade.overlay.fps",
			minecraft.getFps(),
			fpsLimit >= 260 ? Component.translatable("smarttrade.overlay.unlimited") : fpsLimit
		)));
		lines.add(line(Component.translatable("smarttrade.overlay.block", position.getX(), position.getY(), position.getZ())));
		lines.add(line(Component.translatable(
			"smarttrade.overlay.direction",
			directionName(minecraft.player.getDirection()),
			directionAxis(minecraft.player.getDirection())
		)));
		lines.add(line(Component.translatable("smarttrade.overlay.biome", biomeName(minecraft, position))));
		return lines;
	}

	private static String minecraftTime(long dayTime) {
		long timeOfDay = Math.floorMod(dayTime, 24_000L);
		int totalMinutes = (int) ((timeOfDay + 6_000L) % 24_000L * 1_440L / 24_000L);
		int hour = totalMinutes / 60;
		int minute = totalMinutes % 60;
		String period = hour < 12 ? "AM" : "PM";
		int hour12 = hour % 12;
		if (hour12 == 0) {
			hour12 = 12;
		}
		return String.format(Locale.ROOT, "%02d:%02d %s", hour12, minute, period);
	}

	private static OverlayLine line(Component component) {
		return line(component, 0xFFFFFFFF);
	}

	private static OverlayLine line(Component component, int color) {
		return new OverlayLine(component.getString().toUpperCase(Locale.ROOT), color);
	}

	private static int memoryColor(int memoryPercent) {
		if (memoryPercent <= 75) {
			return 0xFF9CD67A;
		}
		return 0xFFFF6B6B;
	}

	private static Component directionName(Direction direction) {
		return Component.translatable("smarttrade.overlay.direction." + direction.getSerializedName());
	}

	private static String directionAxis(Direction direction) {
		return switch (direction) {
			case NORTH -> "-Z";
			case SOUTH -> "+Z";
			case WEST -> "-X";
			case EAST -> "+X";
			default -> "";
		};
	}

	private static Component biomeName(Minecraft minecraft, BlockPos position) {
		Holder<Biome> biome = minecraft.level.getBiome(position);
		Identifier identifier = biome.unwrapKey().map(ResourceKey::identifier).orElse(null);
		if (identifier == null) {
			return Component.translatable("smarttrade.overlay.unknown");
		}
		String fallback = identifier.getPath().replace('_', ' ');
		return Component.translatableWithFallback("biome." + identifier.getNamespace() + "." + identifier.getPath(), fallback);
	}

	private record OverlayLine(String text, int color) {
	}
}
