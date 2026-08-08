package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
	private static void smarttrade$mergeSuspiciousStews(
		ItemStack first,
		ItemStack second,
		CallbackInfoReturnable<Boolean> callback
	) {
		if (
			SmartTradeConfig.randomSuspiciousStews()
				&& first.is(Items.SUSPICIOUS_STEW)
				&& second.is(Items.SUSPICIOUS_STEW)
		) {
			ItemStack normalizedFirst = first.copy();
			ItemStack normalizedSecond = second.copy();
			normalizedFirst.setCount(1);
			normalizedSecond.setCount(1);
			normalizedFirst.set(
				DataComponents.SUSPICIOUS_STEW_EFFECTS,
				net.minecraft.world.item.component.SuspiciousStewEffects.EMPTY
			);
			normalizedSecond.set(
				DataComponents.SUSPICIOUS_STEW_EFFECTS,
				net.minecraft.world.item.component.SuspiciousStewEffects.EMPTY
			);
			boolean matches = ItemStack.matchesIgnoringComponents(
				normalizedFirst,
				normalizedSecond,
				component -> false
			);
			callback.setReturnValue(matches);
		}
	}
}
