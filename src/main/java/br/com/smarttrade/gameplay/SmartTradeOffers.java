package br.com.smarttrade.gameplay;

import br.com.smarttrade.SmartTrade;
import br.com.smarttrade.config.SmartTradeConfig;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class SmartTradeOffers {
	private static final int VANILLA_NOVICE_OFFER_COUNT = 2;
	private static final int EMERALD_RESULT = 1;
	private static final int MAX_USES = 12;
	private static final int VILLAGER_XP = 2;
	private static final float PRICE_MULTIPLIER = 0.05F;
	private static final Map<Item, Integer> LEGACY_INPUT_COUNTS = Map.of(
		Items.SUGAR_CANE, 32,
		Items.COCOA_BEANS, 32,
		Items.HONEYCOMB, 12,
		Items.SPIDER_EYE, 24,
		Items.ENDER_PEARL, 4,
		Items.REDSTONE, 24,
		Items.LAPIS_LAZULI, 24,
		Items.BONE, 32
	);
	private static final List<TradeDefinition> DEFINITIONS = List.of(
		new TradeDefinition(VillagerProfession.FARMER, Items.EGG, 16),
		new TradeDefinition(VillagerProfession.FARMER, Items.SUGAR_CANE, 16),
		new TradeDefinition(VillagerProfession.FARMER, Items.COCOA_BEANS, 16),
		new TradeDefinition(VillagerProfession.FARMER, Items.HONEYCOMB, 8),
		new TradeDefinition(VillagerProfession.CLERIC, Items.SPIDER_EYE, 12),
		new TradeDefinition(VillagerProfession.CLERIC, Items.ENDER_PEARL, 2),
		new TradeDefinition(VillagerProfession.CLERIC, Items.REDSTONE, 16),
		new TradeDefinition(VillagerProfession.CLERIC, Items.LAPIS_LAZULI, 16),
		new TradeDefinition(VillagerProfession.BUTCHER, Items.BONE, 16),
		new TradeDefinition(VillagerProfession.FLETCHER, Items.ARROW, 12)
	);

	private SmartTradeOffers() {
	}

	public static void ensurePresent(Villager villager, MerchantOffers offers) {
		ResourceKey<VillagerProfession> profession = professionOf(villager);
		if (profession == null) {
			return;
		}

		int customOffset = 0;
		for (TradeDefinition definition : DEFINITIONS) {
			if (definition.profession() != profession) {
				continue;
			}

			removeLegacyOffer(villager, offers, definition);
			boolean enabled = SmartTradeConfig.isTradeEnabled(definition.input());
			int existingIndex = findIndex(offers, definition);
			if (enabled && existingIndex < 0) {
				int insertionIndex = Math.min(VANILLA_NOVICE_OFFER_COUNT + customOffset, offers.size());
				offers.add(insertionIndex, definition.createOffer());
				SmartTrade.LOGGER.info(
					"Trade added: villager={}, profession={}, level={}, trade={}, index={}, "
						+ "input={}x{}, output={}xemerald, maxUses={}, xp={}, priceMultiplier={}, offers={}",
					villager.getUUID(),
					profession.identifier(),
					villager.getVillagerData().level(),
					definition.id(),
					insertionIndex,
					definition.inputCount(),
					itemId(definition.input()),
					EMERALD_RESULT,
					MAX_USES,
					VILLAGER_XP,
					PRICE_MULTIPLIER,
					offers.size()
				);
			}
			if (enabled) {
				customOffset++;
			}
		}

		logValidation(villager, offers, profession);
	}

	private static void removeLegacyOffer(
		Villager villager,
		MerchantOffers offers,
		TradeDefinition definition
	) {
		Integer legacyCount = LEGACY_INPUT_COUNTS.get(definition.input());
		if (legacyCount == null) {
			return;
		}

		for (int index = offers.size() - 1; index >= 0; index--) {
			MerchantOffer offer = offers.get(index);
			if (!definition.matchesWithInputCount(offer, legacyCount)) {
				continue;
			}

			offers.remove(index);
			SmartTrade.LOGGER.info(
				"Legacy trade migrated: villager={}, profession={}, level={}, trade={}, "
					+ "removedIndex={}, oldInput={}x{}, newInput={}x{}",
				villager.getUUID(),
				definition.profession().identifier(),
				villager.getVillagerData().level(),
				definition.id(),
				index,
				legacyCount,
				itemId(definition.input()),
				definition.inputCount(),
				itemId(definition.input())
			);
		}
	}

	public static void logTrade(Villager villager, MerchantOffer offer) {
		TradeDefinition definition = findDefinition(offer);
		if (definition == null) {
			return;
		}

		SmartTrade.LOGGER.info(
			"Trade completed: villager={}, profession={}, level={}, trade={}, baseCost={}x{}, "
				+ "currentCost={}x{}, output={}xemerald, uses={}/{}, xp={}, demand={}, "
				+ "specialPrice={}, priceMultiplier={}, outOfStock={}",
			villager.getUUID(),
			professionId(villager),
			villager.getVillagerData().level(),
			definition.id(),
			offer.getBaseCostA().getCount(),
			itemId(definition.input()),
			offer.getCostA().getCount(),
			itemId(definition.input()),
			offer.getResult().getCount(),
			offer.getUses(),
			offer.getMaxUses(),
			offer.getXp(),
			offer.getDemand(),
			offer.getSpecialPriceDiff(),
			offer.getPriceMultiplier(),
			offer.isOutOfStock()
		);
	}

	public static Map<String, Integer> snapshotUses(MerchantOffers offers) {
		Map<String, Integer> uses = new LinkedHashMap<>();
		for (MerchantOffer offer : offers) {
			TradeDefinition definition = findDefinition(offer);
			if (definition != null) {
				uses.put(definition.id(), offer.getUses());
			}
		}
		return uses;
	}

	public static void logRestocks(Villager villager, Map<String, Integer> usesBefore, MerchantOffers offers) {
		for (MerchantOffer offer : offers) {
			TradeDefinition definition = findDefinition(offer);
			if (definition == null) {
				continue;
			}

			Integer previousUses = usesBefore.get(definition.id());
			if (previousUses == null || previousUses == offer.getUses()) {
				continue;
			}

			SmartTrade.LOGGER.info(
				"Trade restocked: villager={}, profession={}, level={}, trade={}, "
					+ "usesBefore={}/{}, usesAfter={}/{}, demand={}, specialPrice={}",
				villager.getUUID(),
				professionId(villager),
				villager.getVillagerData().level(),
				definition.id(),
				previousUses,
				offer.getMaxUses(),
				offer.getUses(),
				offer.getMaxUses(),
				offer.getDemand(),
				offer.getSpecialPriceDiff()
			);
		}
	}

	private static void logValidation(
		Villager villager,
		MerchantOffers offers,
		ResourceKey<VillagerProfession> profession
	) {
		for (TradeDefinition definition : DEFINITIONS) {
			if (definition.profession() != profession) {
				continue;
			}

			int occurrences = countOccurrences(offers, definition);
			int index = findIndex(offers, definition);
			boolean enabled = SmartTradeConfig.isTradeEnabled(definition.input());
			String message =
				"Trade validation: villager={}, profession={}, level={}, trade={}, enabled={}, "
					+ "index={}, noviceBlockEnd={}, occurrences={}, input={}x{}, output={}xemerald, "
					+ "maxUses={}, xp={}, offers={}";
			Object[] values = {
				villager.getUUID(),
				profession.identifier(),
				villager.getVillagerData().level(),
				definition.id(),
				enabled,
				index,
				VANILLA_NOVICE_OFFER_COUNT,
				occurrences,
				definition.inputCount(),
				itemId(definition.input()),
				EMERALD_RESULT,
				MAX_USES,
				VILLAGER_XP,
				offers.size()
			};
			if (occurrences > 1 || (enabled && occurrences != 1)) {
				SmartTrade.LOGGER.warn(message, values);
			} else {
				SmartTrade.LOGGER.info(message, values);
			}
		}
	}

	private static ResourceKey<VillagerProfession> professionOf(Villager villager) {
		for (TradeDefinition definition : DEFINITIONS) {
			if (villager.getVillagerData().profession().is(definition.profession())) {
				return definition.profession();
			}
		}
		return null;
	}

	private static String professionId(Villager villager) {
		ResourceKey<VillagerProfession> profession = professionOf(villager);
		return profession == null ? "unknown" : profession.identifier().toString();
	}

	private static int countOccurrences(MerchantOffers offers, TradeDefinition definition) {
		int occurrences = 0;
		for (MerchantOffer offer : offers) {
			if (definition.matches(offer)) {
				occurrences++;
			}
		}
		return occurrences;
	}

	private static int findIndex(MerchantOffers offers, TradeDefinition definition) {
		for (int index = 0; index < offers.size(); index++) {
			if (definition.matches(offers.get(index))) {
				return index;
			}
		}
		return -1;
	}

	private static TradeDefinition findDefinition(MerchantOffer offer) {
		for (TradeDefinition definition : DEFINITIONS) {
			if (definition.matches(offer)) {
				return definition;
			}
		}
		return null;
	}

	private static String itemId(Item item) {
		return BuiltInRegistries.ITEM.getKey(item).toString();
	}

	private record TradeDefinition(
		ResourceKey<VillagerProfession> profession,
		Item input,
		int inputCount
	) {
		private String id() {
			return profession.identifier() + "/" + itemId(input);
		}

		private MerchantOffer createOffer() {
			return new MerchantOffer(
				new ItemCost(input, inputCount),
				new ItemStack(Items.EMERALD, EMERALD_RESULT),
				MAX_USES,
				VILLAGER_XP,
				PRICE_MULTIPLIER
			);
		}

		private boolean matches(MerchantOffer offer) {
			return matchesWithInputCount(offer, inputCount);
		}

		private boolean matchesWithInputCount(MerchantOffer offer, int expectedInputCount) {
			ItemStack cost = offer.getBaseCostA();
			ItemStack result = offer.getResult();
			return cost.is(input)
				&& cost.getCount() == expectedInputCount
				&& offer.getCostB().isEmpty()
				&& result.is(Items.EMERALD)
				&& result.getCount() == EMERALD_RESULT
				&& offer.getMaxUses() == MAX_USES
				&& offer.getXp() == VILLAGER_XP;
		}
	}
}
