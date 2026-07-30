package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.client.gui.Hud;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hud.class)
public abstract class HorseHealthHudMixin {
	private static final int COMPACT_HEARTS = 10;
	private static final float COMPACT_HEALTH_HALVES = COMPACT_HEARTS * 2.0F;

	@Inject(method = "getVehicleMaxHearts", at = @At("RETURN"), cancellable = true)
	private void smarttrade$limitHorseHearts(
		LivingEntity vehicle,
		CallbackInfoReturnable<Integer> callback
	) {
		if (SmartTradeConfig.compactHorseHealthHud() && vehicle instanceof AbstractHorse) {
			callback.setReturnValue(COMPACT_HEARTS);
		}
	}

	@Redirect(
		method = "extractVehicleHealth",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getHealth()F")
	)
	private float smarttrade$scaleHorseHealth(LivingEntity vehicle) {
		if (!SmartTradeConfig.compactHorseHealthHud() || !(vehicle instanceof AbstractHorse)) {
			return vehicle.getHealth();
		}

		float proportion = vehicle.getHealth() / vehicle.getMaxHealth();
		return Mth.clamp(proportion, 0.0F, 1.0F) * COMPACT_HEALTH_HALVES;
	}
}
