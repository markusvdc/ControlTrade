package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(LivingEntity.class)
public abstract class LivingEntityInsaneDifficultyMixin {
	private static final float SPEAR_DAMAGE_MULTIPLIER = 0.70F;

	@ModifyConstant(method = "hurtServer", constant = @Constant(floatValue = 10.0F))
	private float smarttrade$useFullHorseInvulnerabilityTime(float vanillaThreshold) {
		LivingEntity entity = (LivingEntity)(Object)this;
		return entity instanceof AbstractHorse && InsaneDifficulty.isActive(entity.level())
			? 0.0F
			: vanillaThreshold;
	}

	@ModifyVariable(method = "hurtServer", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private float smarttrade$reduceSpearDamage(
		float damage,
		ServerLevel level,
		DamageSource source
	) {
		return InsaneDifficulty.isActive(level) && source.is(DamageTypes.SPEAR)
			? damage * SPEAR_DAMAGE_MULTIPLIER
			: damage;
	}
}
