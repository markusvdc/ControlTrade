package br.com.smarttrade.gameplay;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/** Preflights one complete sale without touching the live inventory. */
public record BulkSalePlan(List<ItemStack> remaining, ItemStack paymentA, ItemStack paymentB) {
	public static boolean eligible(MerchantOffer offer) {
		return !offer.isOutOfStock() && offer.getResult().is(Items.EMERALD)
			&& !offer.getBaseCostA().is(Items.EMERALD) && !offer.getCostB().is(Items.EMERALD);
	}

	public static BulkSalePlan create(List<ItemStack> inventory, MerchantOffer offer) {
		if (!eligible(offer)) return null;
		// Try each component variant independently; never merge differently named items.
		for (ItemStack candidate : inventory) {
			if (!offer.getItemCostA().test(candidate)) continue;
			List<ItemStack> remaining = new ArrayList<>(inventory.stream().map(ItemStack::copy).toList());
			ItemStack a = withdraw(remaining, offer.getItemCostA(), offer.getCostA().getCount(), candidate);
			if (a.isEmpty()) continue;
			if (offer.getItemCostB().isEmpty()) {
				if (fits(remaining, offer.getResult())) return new BulkSalePlan(remaining, a, ItemStack.EMPTY);
				continue;
			}
			for (ItemStack second : remaining) {
				List<ItemStack> both = new ArrayList<>(remaining.stream().map(ItemStack::copy).toList());
				ItemStack b = withdraw(both, offer.getItemCostB().orElseThrow(), offer.getCostB().getCount(), second);
				if (!b.isEmpty() && offer.satisfiedBy(a, b) && fits(both, offer.getResult())) {
					return new BulkSalePlan(both, a, b);
				}
			}
		}
		return null;
	}

	private static ItemStack withdraw(List<ItemStack> inventory, ItemCost cost, int amount, ItemStack candidate) {
		if (candidate.isEmpty() || amount <= 0 || !cost.test(candidate)) return ItemStack.EMPTY;
		ItemStack payment = candidate.copyWithCount(amount);
		int needed = amount;
		for (ItemStack stack : inventory) {
			if (!cost.test(stack) || !ItemStack.isSameItemSameComponents(stack, payment)) continue;
			int take = Math.min(needed, stack.getCount());
			stack.shrink(take);
			needed -= take;
			if (needed == 0) return payment;
		}
		return ItemStack.EMPTY;
	}

	private static boolean fits(List<ItemStack> inventory, ItemStack result) {
		int needed = result.getCount();
		for (ItemStack stack : inventory) {
			if (stack.isEmpty()) needed -= result.getMaxStackSize();
			else if (ItemStack.isSameItemSameComponents(stack, result)) {
				needed -= Math.max(0, stack.getMaxStackSize() - stack.getCount());
			}
			if (needed <= 0) return true;
		}
		return false;
	}
}
