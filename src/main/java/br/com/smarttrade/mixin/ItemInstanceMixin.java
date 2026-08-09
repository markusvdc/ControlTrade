package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.ExpandedItemStacks;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemInstance.class)
public interface ItemInstanceMixin {
	@Inject(method = "getMaxStackSize", at = @At("HEAD"), cancellable = true)
	private void smarttrade$useExpandedItemStackSize(CallbackInfoReturnable<Integer> callback) {
		if (!((Object) this instanceof ItemStack stack)) {
			return;
		}
		if (!SmartTradeConfig.expandedItemStacks()) {
			return;
		}

		int maximum = ExpandedItemStacks.maximumFor(stack);
		if (maximum > 0) {
			callback.setReturnValue(maximum);
		}
	}
}
