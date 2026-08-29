package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.ExpandedItemStacks;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlaceRecipe.class)
public abstract class ServerPlaceRecipeMixin {
	@Inject(method = "clampToMaxStackSize", at = @At("HEAD"), cancellable = true)
	private static void smarttrade$useExpandedIngredientStackSizes(
		int amount,
		List<Holder<Item>> ingredients,
		CallbackInfoReturnable<Integer> callback
	) {
		if (!SmartTradeConfig.expandedItemStacks()) {
			return;
		}

		int clampedAmount = amount;
		for (Holder<Item> ingredient : ingredients) {
			ItemStack stack = new ItemStack(ingredient);
			int expandedMaximum = ExpandedItemStacks.maximumFor(stack);
			clampedAmount = Math.min(clampedAmount, expandedMaximum > 0 ? expandedMaximum : stack.getMaxStackSize());
		}
		callback.setReturnValue(clampedAmount);
	}
}
