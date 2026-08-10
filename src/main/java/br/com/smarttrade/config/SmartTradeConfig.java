package br.com.smarttrade.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public final class SmartTradeConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final Path CONFIG_PATH =
		FabricLoader.getInstance().getConfigDir().resolve("smarttrade.json");
	private static final int CURRENT_VERSION = 22;
	private static final Set<String> AVAILABLE_TRADES = Set.of(
		"minecraft:egg",
		"minecraft:cocoa_beans",
		"minecraft:honeycomb",
		"minecraft:spider_eye",
		"minecraft:ender_pearl",
		"minecraft:redstone",
		"minecraft:lapis_lazuli",
		"minecraft:bone",
		"minecraft:arrow"
	);

	private static volatile Set<String> enabledTrades = AVAILABLE_TRADES;
	private static volatile boolean showAdditionalInformation;
	private static volatile boolean maximumVillagerReputation;
	private static volatile boolean soulSpeedOnlyInNether;
	private static volatile boolean fixedHugeMushroomHeight;
	private static volatile boolean compactHorseHealthHud;
	private static volatile boolean equestrianHud;
	private static volatile boolean automaticDoorClosing;
	private static volatile boolean disableChatHistoryNavigation;
	private static volatile boolean compactItemCounts;
	private static volatile boolean expandedItemStacks;
	private static volatile boolean randomExperienceOrbColors;
	private static volatile boolean compactInformationOverlay;
	private static volatile boolean compactGameMenus;
	private static volatile boolean insaneDifficulty;
	private static volatile boolean sovereignShift;

	private SmartTradeConfig() {
	}

	public static synchronized void load() {
		if (!Files.exists(CONFIG_PATH)) {
			return;
		}

		try {
			String json = Files.readString(CONFIG_PATH, StandardCharsets.UTF_8);
			ConfigData data = GSON.fromJson(json, ConfigData.class);
			if (data != null && data.enabledTrades != null) {
				LinkedHashSet<String> migrated = new LinkedHashSet<>(data.enabledTrades);
				if (data.version < 8) {
					migrated.add("minecraft:arrow");
				}
				enabledTrades = sanitize(migrated);
			} else {
				LinkedHashSet<String> migrated = new LinkedHashSet<>(AVAILABLE_TRADES);
				if (data != null && Boolean.FALSE.equals(data.eggTradeEnabled)) {
					migrated.remove("minecraft:egg");
				}
				enabledTrades = Set.copyOf(migrated);
			}
			if (data != null && data.showAdditionalInformation != null) {
				showAdditionalInformation = data.showAdditionalInformation;
			} else if (data != null && data.showVillagerReputationInJade != null) {
				showAdditionalInformation = data.showVillagerReputationInJade;
			} else {
				showAdditionalInformation = false;
			}
			maximumVillagerReputation =
				data != null && Boolean.TRUE.equals(data.maximumVillagerReputation);
			soulSpeedOnlyInNether =
				data != null && Boolean.TRUE.equals(data.soulSpeedOnlyInNether);
			fixedHugeMushroomHeight =
				data != null && Boolean.TRUE.equals(data.fixedHugeMushroomHeight);
			compactHorseHealthHud =
				data != null && Boolean.TRUE.equals(data.compactHorseHealthHud);
			equestrianHud =
				data != null && Boolean.TRUE.equals(data.equestrianHud);
			automaticDoorClosing =
				data != null && Boolean.TRUE.equals(data.automaticDoorClosing);
			disableChatHistoryNavigation =
				data != null && Boolean.TRUE.equals(data.disableChatHistoryNavigation);
			compactItemCounts =
				data != null && Boolean.TRUE.equals(data.compactItemCounts);
			expandedItemStacks =
				data != null && Boolean.TRUE.equals(data.expandedItemStacks);
			randomExperienceOrbColors =
				data != null && Boolean.TRUE.equals(data.randomExperienceOrbColors);
			compactInformationOverlay =
				data != null && Boolean.TRUE.equals(data.compactInformationOverlay);
			compactGameMenus =
				data != null && Boolean.TRUE.equals(data.compactGameMenus);
			insaneDifficulty =
				data != null && Boolean.TRUE.equals(data.insaneDifficulty);
			sovereignShift =
				data != null && Boolean.TRUE.equals(data.sovereignShift);
		} catch (IOException | JsonParseException exception) {
			enabledTrades = AVAILABLE_TRADES;
			showAdditionalInformation = false;
			maximumVillagerReputation = false;
			soulSpeedOnlyInNether = false;
			fixedHugeMushroomHeight = false;
			compactHorseHealthHud = false;
			equestrianHud = false;
			automaticDoorClosing = false;
			disableChatHistoryNavigation = false;
			compactItemCounts = false;
			expandedItemStacks = false;
			randomExperienceOrbColors = false;
			compactInformationOverlay = false;
			compactGameMenus = false;
			insaneDifficulty = false;
			sovereignShift = false;
		}
	}

	public static synchronized boolean save() {
		return saveTradeIds(enabledTrades);
	}

	public static synchronized boolean saveEnabledTrades(Collection<Identifier> itemIds) {
		return saveTradeIds(itemIds.stream().map(Identifier::toString).toList());
	}

	public static synchronized boolean saveGlobalOptions(
		boolean showAdditionalInfo,
		boolean useMaximumVillagerReputation,
		boolean restrictSoulSpeedToNether,
		boolean useFixedHugeMushroomHeight,
		boolean useCompactHorseHealthHud,
		boolean useEquestrianHud,
		boolean useAutomaticDoorClosing,
		boolean disableHistoryNavigation,
		boolean useCompactItemCounts,
		boolean useExpandedItemStacks,
		boolean useRandomExperienceOrbColors,
		boolean useCompactInformationOverlay,
		boolean useCompactGameMenus,
		boolean enableInsaneDifficulty,
		boolean enableSovereignShift
	) {
		showAdditionalInformation = showAdditionalInfo;
		maximumVillagerReputation = useMaximumVillagerReputation;
		soulSpeedOnlyInNether = restrictSoulSpeedToNether;
		fixedHugeMushroomHeight = useFixedHugeMushroomHeight;
		compactHorseHealthHud = useCompactHorseHealthHud;
		equestrianHud = useEquestrianHud;
		automaticDoorClosing = useAutomaticDoorClosing;
		disableChatHistoryNavigation = disableHistoryNavigation;
		compactItemCounts = useCompactItemCounts;
		expandedItemStacks = useExpandedItemStacks;
		randomExperienceOrbColors = useRandomExperienceOrbColors;
		compactInformationOverlay = useCompactInformationOverlay;
		compactGameMenus = useCompactGameMenus;
		insaneDifficulty = enableInsaneDifficulty;
		sovereignShift = enableSovereignShift;
		return saveTradeIds(enabledTrades);
	}

	private static boolean saveTradeIds(Collection<String> itemIds) {
		Set<String> sanitized = sanitize(itemIds);
		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			Path temporaryPath = CONFIG_PATH.resolveSibling(CONFIG_PATH.getFileName() + ".tmp");
			Files.writeString(
				temporaryPath,
				GSON.toJson(new ConfigData(
					CURRENT_VERSION,
					sanitized.stream().sorted().toList(),
					null,
					null,
					showAdditionalInformation,
					maximumVillagerReputation,
					soulSpeedOnlyInNether,
					fixedHugeMushroomHeight,
					compactHorseHealthHud,
					equestrianHud,
					automaticDoorClosing,
					disableChatHistoryNavigation,
					compactItemCounts,
					expandedItemStacks,
					randomExperienceOrbColors,
					compactInformationOverlay,
					compactGameMenus,
					insaneDifficulty,
					sovereignShift
				)),
				StandardCharsets.UTF_8
			);
			Files.move(temporaryPath, CONFIG_PATH, StandardCopyOption.REPLACE_EXISTING);
			enabledTrades = sanitized;
			return true;
		} catch (IOException exception) {
			return false;
		}
	}

	public static boolean isTradeEnabled(Item item) {
		return enabledTrades.contains(BuiltInRegistries.ITEM.getKey(item).toString());
	}

	public static boolean showAdditionalInformation() {
		return showAdditionalInformation;
	}

	public static boolean maximumVillagerReputation() {
		return maximumVillagerReputation;
	}

	public static boolean soulSpeedOnlyInNether() {
		return soulSpeedOnlyInNether;
	}

	public static boolean fixedHugeMushroomHeight() {
		return fixedHugeMushroomHeight;
	}

	public static boolean compactHorseHealthHud() {
		return compactHorseHealthHud;
	}

	public static boolean equestrianHud() {
		return equestrianHud;
	}

	public static boolean automaticDoorClosing() {
		return automaticDoorClosing;
	}

	public static boolean disableChatHistoryNavigation() {
		return disableChatHistoryNavigation;
	}

	public static boolean compactItemCounts() {
		return compactItemCounts;
	}

	public static boolean expandedItemStacks() {
		return expandedItemStacks;
	}

	public static boolean randomExperienceOrbColors() {
		return randomExperienceOrbColors;
	}

	public static boolean compactInformationOverlay() {
		return compactInformationOverlay;
	}

	public static boolean compactGameMenus() {
		return compactGameMenus;
	}

	public static boolean insaneDifficulty() {
		return insaneDifficulty;
	}

	public static boolean sovereignShift() {
		return sovereignShift;
	}

	private static Set<String> sanitize(Collection<String> itemIds) {
		LinkedHashSet<String> sanitized = new LinkedHashSet<>();
		if (itemIds != null) {
			for (String itemId : itemIds) {
				if (AVAILABLE_TRADES.contains(itemId)) {
					sanitized.add(itemId);
				}
			}
		}
		return Set.copyOf(sanitized);
	}

	private record ConfigData(
		int version,
		List<String> enabledTrades,
		Boolean eggTradeEnabled,
		Boolean showVillagerReputationInJade,
		Boolean showAdditionalInformation,
		Boolean maximumVillagerReputation,
		Boolean soulSpeedOnlyInNether,
		Boolean fixedHugeMushroomHeight,
		Boolean compactHorseHealthHud,
		Boolean equestrianHud,
		Boolean automaticDoorClosing,
		Boolean disableChatHistoryNavigation,
		Boolean compactItemCounts,
		Boolean expandedItemStacks,
		Boolean randomExperienceOrbColors,
		Boolean compactInformationOverlay,
		Boolean compactGameMenus,
		Boolean insaneDifficulty,
		Boolean sovereignShift
	) {
	}
}
