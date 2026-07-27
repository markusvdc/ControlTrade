package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class SoulSpeedMovementMixin {
	@Inject(method = "getBlockSpeedFactor", at = @At("RETURN"), cancellable = true)
	private void smarttrade$keepNormalSoulBlockSpeed(CallbackInfoReturnable<Float> callback) {
		LivingEntity entity = (LivingEntity) (Object) this;
		if (!SmartTradeConfig.soulSpeedOnlyInNether() || Level.NETHER.equals(entity.level().dimension())) {
			return;
		}

		boolean onSoulSpeedBlock =
			entity.level().getBlockState(entity.blockPosition()).is(BlockTags.SOUL_SPEED_BLOCKS)
				|| entity.level()
					.getBlockState(entity.getBlockPosBelowThatAffectsMyMovement())
					.is(BlockTags.SOUL_SPEED_BLOCKS);
		if (!onSoulSpeedBlock) {
			return;
		}

		var enchantments = entity.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		if (EnchantmentHelper.getEnchantmentLevel(
			enchantments.getOrThrow(Enchantments.SOUL_SPEED),
			entity
		) > 0) {
			callback.setReturnValue(1.0F);
		}
	}
}
