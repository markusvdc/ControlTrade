package br.com.smarttrade.gameplay;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public final class ExpandedItemStacks {
	private ExpandedItemStacks() {
	}

	public static int maximumFor(ItemStack stack) {
		if (
			stack.is(ConventionalItemTags.SOUP_FOODS)
				|| stack.is(Items.ENDER_PEARL)
				|| stack.is(Items.CAKE)
				|| stack.is(ConventionalItemTags.EGGS)
		) {
			return 64;
		}
		if (
			stack.is(Items.SADDLE)
				|| stack.is(ConventionalItemTags.POTIONS)
				|| stack.is(ItemTags.HARNESSES)
				|| stack.is(ItemTags.BOATS)
				|| stack.is(ItemTags.BEDS)
				|| stack.is(Items.ENCHANTED_BOOK)
				|| stack.is(ConventionalItemTags.MUSIC_DISCS)
				|| stack.is(ConventionalItemTags.BUCKETS)
		) {
			return 16;
		}
		return 0;
	}
}
