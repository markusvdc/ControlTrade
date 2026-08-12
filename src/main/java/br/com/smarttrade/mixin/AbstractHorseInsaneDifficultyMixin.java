package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public abstract class AbstractHorseInsaneDifficultyMixin {
	@ModifyArgs(
		method = "hurtServer",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/entity/animal/Animal;hurtServer(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/damagesource/DamageSource;F)Z"
		)
	)
	private void smarttrade$scaleDamage(Args args) {
		ServerLevel level = args.get(0);
		DamageSource source = args.get(1);
		if (InsaneDifficulty.isActive(level) && source.scalesWithDifficulty()) {
			args.set(2, (float)args.get(2) * 1.5F);
		}
	}

	@Inject(method = "hurtServer", at = @At("RETURN"))
	private void smarttrade$extendDamageCooldown(
		ServerLevel level,
		DamageSource source,
		float damage,
		CallbackInfoReturnable<Boolean> callback
	) {
		if (callback.getReturnValue() && InsaneDifficulty.isActive(level)) {
			((AbstractHorse)(Object)this).invulnerableTime = 50;
		}
	}
}
