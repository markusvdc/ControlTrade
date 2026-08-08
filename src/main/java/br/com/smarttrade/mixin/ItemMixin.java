package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.RandomSuspiciousStews;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin {
	@Inject(method = "finishUsingItem", at = @At("HEAD"))
	private void smarttrade$randomizeSuspiciousStew(
		ItemStack stack,
		Level level,
		LivingEntity user,
		CallbackInfoReturnable<ItemStack> callback
	) {
		if (
			SmartTradeConfig.randomSuspiciousStews()
				&& !level.isClientSide()
				&& stack.is(Items.SUSPICIOUS_STEW)
		) {
			RandomSuspiciousStews.chooseEffect(stack, user.getRandom());
		}
	}
}
