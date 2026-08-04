package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Hud.class)
public abstract class HorseHealthHudMixin {
	private static final int COMPACT_HEARTS = 10;
	private static final float COMPACT_HEALTH_HALVES = COMPACT_HEARTS * 2.0F;

	@Shadow
	private Minecraft minecraft;

	@Shadow
	private LivingEntity getPlayerVehicleWithHealth() {
		return null;
	}

	@Shadow
	private int getVehicleMaxHearts(LivingEntity vehicle) {
		return 0;
	}

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

	@ModifyVariable(method = "extractVehicleHealth", at = @At("STORE"), name = "yLine1")
	private int smarttrade$moveMountHealthUp(int yLine) {
		if (SmartTradeConfig.equestrianHud() && this.minecraft.gameMode.canHurtPlayer()) {
			return yLine - 10;
		}
		return yLine;
	}

	@Redirect(
		method = "extractPlayerHealth",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/Hud;getVehicleMaxHearts(Lnet/minecraft/world/entity/LivingEntity;)I"
		)
	)
	private int smarttrade$alwaysRenderFood(Hud hud, LivingEntity vehicle) {
		return SmartTradeConfig.equestrianHud() ? 0 : this.getVehicleMaxHearts(vehicle);
	}

	@ModifyVariable(method = "getAirBubbleYLine", at = @At("HEAD"), ordinal = 0, argsOnly = true)
	private int smarttrade$moveAirUp(int heartCount) {
		if (!SmartTradeConfig.equestrianHud()) {
			return heartCount;
		}
		LivingEntity vehicle = this.getPlayerVehicleWithHealth();
		return vehicle == null ? heartCount : this.getVehicleMaxHearts(vehicle);
	}

	@Redirect(
		method = "nextContextualInfoState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/player/LocalPlayer;jumpableVehicle()Lnet/minecraft/world/entity/PlayerRideableJumping;"
		)
	)
	private PlayerRideableJumping smarttrade$switchContextualBar(LocalPlayer player) {
		PlayerRideableJumping jumpableVehicle = player.jumpableVehicle();
		if (!SmartTradeConfig.equestrianHud()) {
			return jumpableVehicle;
		}
		if (!this.minecraft.gameMode.hasExperience()
			|| this.minecraft.options.keyJump.isDown()
			|| player.getJumpRidingScale() > 0.0F) {
			return jumpableVehicle;
		}
		return null;
	}

	@Redirect(
		method = "extractHotbarAndDecorations",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/multiplayer/MultiPlayerGameMode;hasExperience()Z"
		)
	)
	private boolean smarttrade$renderExperienceLevel(MultiPlayerGameMode gameMode) {
		if (!SmartTradeConfig.equestrianHud()) {
			return gameMode.hasExperience();
		}
		LocalPlayer player = this.minecraft.player;
		PlayerRideableJumping jumpableVehicle = player.jumpableVehicle();
		return gameMode.hasExperience()
			&& (jumpableVehicle == null
				|| (!this.minecraft.options.keyJump.isDown() && player.getJumpRidingScale() <= 0.0F));
	}
}
