package br.com.smarttrade.gameplay;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;

/** Standalone regression checks; bootstraps registries, never a game client or world. */
public final class BulkSalePlanTest {
	public static void main(String[] args) {
		SharedConstants.tryDetectVersion();
		Bootstrap.bootStrap();
		// 26.3 binds item components during world data loading. These plain-stack
		// fixtures need only common defaults; no world or resource reload is started.
		for (var item : List.of(Items.WHEAT, Items.POTATO, Items.EMERALD, Items.BREAD, Items.STONE)) {
			item.builtInRegistryHolder().bindComponents(DataComponents.COMMON_ITEM_COMPONENTS);
		}
		List<ItemStack> inventory = empty();
		inventory.set(0, new ItemStack(Items.WHEAT, 13));
		inventory.set(35, new ItemStack(Items.WHEAT, 10));
		MerchantOffer wheat = sale(20, 1, 12);
		BulkSalePlan plan = BulkSalePlan.create(inventory, wheat);
		check(plan != null && count(plan.remaining(), Items.WHEAT) == 3, "split stacks and hotbar");
		check(count(inventory, Items.WHEAT) == 23 && wheat.getUses() == 0, "preflight must not mutate inputs");
		wheat.setSpecialPriceDiff(-10);
		check(BulkSalePlan.create(inventory, wheat).paymentA().getCount() == 10, "discounted live price");
		wheat.setSpecialPriceDiff(5);
		check(BulkSalePlan.create(inventory, wheat) == null, "increased price requires sufficient materials");
		wheat.setSpecialPriceDiff(0);
		wheat.setToOutOfStock();
		check(BulkSalePlan.create(inventory, wheat) == null, "exhausted offer");
		MerchantOffer purchase = new MerchantOffer(new ItemCost(Items.EMERALD, 1), new ItemStack(Items.BREAD), 12, 2, 0);
		check(!BulkSalePlan.eligible(purchase), "never buy using emeralds");
		MerchantOffer mixed = new MerchantOffer(new ItemCost(Items.WHEAT, 1), Optional.of(new ItemCost(Items.EMERALD, 1)), new ItemStack(Items.EMERALD, 2), 12, 2, 0);
		check(!BulkSalePlan.eligible(mixed), "never spend emeralds in second slot");
		List<ItemStack> full = new ArrayList<>();
		for (int i = 0; i < 36; i++) full.add(new ItemStack(Items.STONE, 64));
		full.set(0, new ItemStack(Items.WHEAT, 21));
		check(BulkSalePlan.create(full, sale(20, 1, 12)) == null, "full inventory retains ingredients");
		full.set(0, new ItemStack(Items.WHEAT, 20));
		check(BulkSalePlan.create(full, sale(20, 1, 12)) != null, "consumed stack frees output slot");
		full.set(0, new ItemStack(Items.WHEAT, 21));
		full.set(1, new ItemStack(Items.EMERALD, 63));
		check(BulkSalePlan.create(full, sale(20, 1, 12)) != null, "merge emerald into existing stack");
		check(BulkSalePlan.create(full, sale(20, 2, 12)) == null, "reject partial output capacity");
		List<ItemStack> variants = empty();
		variants.set(0, new ItemStack(Items.WHEAT, 10));
		variants.set(1, new ItemStack(Items.WHEAT, 10));
		variants.get(1).set(DataComponents.CUSTOM_NAME, Component.literal("Royal wheat"));
		check(BulkSalePlan.create(variants, sale(20, 1, 12)) == null, "distinct components cannot merge as payment");
		variants.set(2, new ItemStack(Items.WHEAT, 10));
		check(BulkSalePlan.create(variants, sale(20, 1, 12)) != null, "matching components across slots");
		MerchantOffer twoCosts = new MerchantOffer(new ItemCost(Items.WHEAT, 10), Optional.of(new ItemCost(Items.POTATO, 5)), new ItemStack(Items.EMERALD), 12, 2, 0);
		check(BulkSalePlan.create(inventory, twoCosts) == null, "missing second ingredient");
		inventory.set(1, new ItemStack(Items.POTATO, 7));
		plan = BulkSalePlan.create(inventory, twoCosts);
		check(plan != null && count(plan.remaining(), Items.WHEAT) == 13 && count(plan.remaining(), Items.POTATO) == 2, "both ingredients and leftovers");
		MerchantOffer sameCosts = new MerchantOffer(new ItemCost(Items.WHEAT, 15), Optional.of(new ItemCost(Items.WHEAT, 10)), new ItemStack(Items.EMERALD), 12, 2, 0);
		check(BulkSalePlan.create(inventory, sameCosts) == null, "do not double count shared ingredient");
		System.out.println("BulkSalePlan: 16 regression checks passed.");
	}

	private static MerchantOffer sale(int input, int output, int uses) {
		return new MerchantOffer(new ItemCost(Items.WHEAT, input), new ItemStack(Items.EMERALD, output), uses, 2, 0);
	}
	private static List<ItemStack> empty() {
		List<ItemStack> result = new ArrayList<>();
		for (int i = 0; i < 36; i++) result.add(ItemStack.EMPTY);
		return result;
	}
	private static int count(List<ItemStack> inventory, net.minecraft.world.item.Item item) {
		return inventory.stream().filter(stack -> stack.is(item)).mapToInt(ItemStack::getCount).sum();
	}
	private static void check(boolean success, String description) {
		if (!success) throw new AssertionError(description);
	}
}
