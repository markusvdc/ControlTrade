package br.com.smarttrade.gameplay;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public final class DawnStockRepair {
	private static final Map<Item, Integer> CONTROL_TRADE_INPUTS = Map.of(
		Items.EGG, 15, Items.COCOA_BEANS, 15, Items.HONEYCOMB, 15,
		Items.SPIDER_EYE, 15, Items.REDSTONE, 15, Items.LAPIS_LAZULI, 15,
		Items.BONE, 15, Items.ARROW, 15, Items.IRON_INGOT, 15, Items.COPPER_INGOT, 15
	);

	private DawnStockRepair() {
	}

	public static void removeDuplicateControlTradeOffers(MerchantOffers offers) {
		Map<Item, MerchantOffer> originals = new HashMap<>();
		var iterator = offers.iterator();
		while (iterator.hasNext()) {
			MerchantOffer offer = iterator.next();
			ItemStack input = offer.getBaseCostA();
			Integer count = CONTROL_TRADE_INPUTS.get(input.getItem());
			if (count == null || input.getCount() != count || offer.getMaxUses() != 12
				|| offer.getXp() != 2 || Float.compare(offer.getPriceMultiplier(), 0.05F) != 0
				|| !offer.shouldRewardExp() || !offer.getCostB().isEmpty()
				|| !ItemStack.matches(input, new ItemStack(input.getItem(), count))
				|| !ItemStack.matches(offer.getResult(), new ItemStack(Items.EMERALD))) {
				continue;
			}
			MerchantOffer original = originals.putIfAbsent(input.getItem(), offer);
			if (original != null) {
				// Retain the first offer's prices and all consumed uses, even when
				// the combined uses exhaust the stock until the next dawn.
				((DawnStockOffer)original).smarttrade$mergeUsedStock(offer.getUses());
				iterator.remove();
			}
		}
	}
}
