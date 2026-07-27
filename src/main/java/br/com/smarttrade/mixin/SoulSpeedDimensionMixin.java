package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Enchantment.class)
public abstract class SoulSpeedDimensionMixin {
	@Shadow
	public abstract void stopLocationBasedEffects(
		int enchantmentLevel,
		EnchantedItemInUse item,
		LivingEntity entity
	);

	@Inject(method = "runLocationChangedEffects", at = @At("HEAD"), cancellable = true)
	private void smarttrade$restrictLocationEffectsToNether(
		ServerLevel level,
		int enchantmentLevel,
		EnchantedItemInUse item,
		LivingEntity entity,
		CallbackInfo callback
	) {
		if (smarttrade$shouldDisable(level)) {
			this.stopLocationBasedEffects(enchantmentLevel, item, entity);
			callback.cancel();
		}
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void smarttrade$restrictTickEffectsToNether(
		ServerLevel level,
		int enchantmentLevel,
		EnchantedItemInUse item,
		Entity entity,
		CallbackInfo callback
	) {
		if (smarttrade$shouldDisable(level)) {
			callback.cancel();
		}
	}

	@Unique
	private boolean smarttrade$shouldDisable(ServerLevel level) {
		if (!SmartTradeConfig.soulSpeedOnlyInNether() || level.dimension() == Level.NETHER) {
			return false;
		}

		var enchantmentRegistry = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
		return Enchantments.SOUL_SPEED.identifier().equals(
			enchantmentRegistry.getKey((Enchantment) (Object) this)
		);
	}
}
