package br.com.smarttrade.gameplay;

import br.com.smarttrade.config.SmartTradeConfig;
import java.util.List;
import java.util.Map;
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
	private static final Map<Item, List<Integer>> LEGACY_INPUT_COUNTS = Map.of(
		Items.EGG, List.of(16),
		Items.COCOA_BEANS, List.of(16, 32),
		Items.HONEYCOMB, List.of(8, 12),
		Items.SPIDER_EYE, List.of(12, 24),
		Items.ENDER_PEARL, List.of(2, 4),
		Items.REDSTONE, List.of(16, 24),
		Items.LAPIS_LAZULI, List.of(16, 24),
		Items.BONE, List.of(16, 32),
		Items.ARROW, List.of(12)
	);
	private static final List<TradeDefinition> DEFINITIONS = List.of(
		new TradeDefinition(VillagerProfession.FARMER, Items.EGG, 20),
		new TradeDefinition(VillagerProfession.FARMER, Items.COCOA_BEANS, 20),
		new TradeDefinition(VillagerProfession.FARMER, Items.HONEYCOMB, 10),
		new TradeDefinition(VillagerProfession.CLERIC, Items.SPIDER_EYE, 15),
		new TradeDefinition(VillagerProfession.CLERIC, Items.ENDER_PEARL, 3),
		new TradeDefinition(VillagerProfession.CLERIC, Items.REDSTONE, 20),
		new TradeDefinition(VillagerProfession.CLERIC, Items.LAPIS_LAZULI, 20),
		new TradeDefinition(VillagerProfession.BUTCHER, Items.BONE, 20),
		new TradeDefinition(VillagerProfession.FLETCHER, Items.ARROW, 15)
	);

	private SmartTradeOffers() {
	}

	public static void ensurePresent(Villager villager, MerchantOffers offers) {
		ResourceKey<VillagerProfession> profession = professionOf(villager);
		if (profession == null) {
			return;
		}

		removeRetiredSugarCaneOffers(villager, offers, profession);

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
			}
			if (enabled) {
				customOffset++;
			}
		}
	}

	private static void removeRetiredSugarCaneOffers(
		Villager villager,
		MerchantOffers offers,
		ResourceKey<VillagerProfession> profession
	) {
		if (profession != VillagerProfession.FARMER) {
			return;
		}

		for (int index = offers.size() - 1; index >= 0; index--) {
			MerchantOffer offer = offers.get(index);
			ItemStack cost = offer.getBaseCostA();
			ItemStack result = offer.getResult();
			boolean retiredTrade = cost.is(Items.SUGAR_CANE)
				&& (cost.getCount() == 16 || cost.getCount() == 32)
				&& offer.getCostB().isEmpty()
				&& result.is(Items.EMERALD)
				&& result.getCount() == EMERALD_RESULT
				&& offer.getMaxUses() == MAX_USES
				&& offer.getXp() == VILLAGER_XP;
			if (!retiredTrade) {
				continue;
			}

			offers.remove(index);
		}
	}

	private static void removeLegacyOffer(
		Villager villager,
		MerchantOffers offers,
		TradeDefinition definition
	) {
		List<Integer> legacyCounts = LEGACY_INPUT_COUNTS.get(definition.input());
		if (legacyCounts == null) {
			return;
		}

		for (int index = offers.size() - 1; index >= 0; index--) {
			MerchantOffer offer = offers.get(index);
			int legacyCount = offer.getBaseCostA().getCount();
			if (!legacyCounts.contains(legacyCount)
				|| !definition.matchesWithInputCount(offer, legacyCount)) {
				continue;
			}

			offers.remove(index);
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

	private static int findIndex(MerchantOffers offers, TradeDefinition definition) {
		for (int index = 0; index < offers.size(); index++) {
			if (definition.matches(offers.get(index))) {
				return index;
			}
		}
		return -1;
	}

	private record TradeDefinition(
		ResourceKey<VillagerProfession> profession,
		Item input,
		int inputCount
	) {
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
