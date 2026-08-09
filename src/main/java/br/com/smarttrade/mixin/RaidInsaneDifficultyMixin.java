package br.com.smarttrade.mixin;

import net.minecraft.world.entity.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Raid.class)
public abstract class RaidInsaneDifficultyMixin {
	@Shadow
	private int numGroups;

	@ModifyVariable(method = "getDefaultNumSpawns", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private int smarttrade$reuseLastVanillaWave(int wave) {
		return Math.min(wave, 7);
	}

	@Redirect(
		method = "getDefaultNumSpawns",
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/entity/raid/Raid;numGroups:I")
	)
	private int smarttrade$reuseLastVanillaBonusWave(Raid raid) {
		return Math.min(this.numGroups, 7);
	}
}
