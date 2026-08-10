package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerSovereignShiftMixin {
	@Redirect(
		method = "startSleepInBed",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
		)
	)
	private boolean smarttrade$allowDaytimeSleep(BedRule bedRule, Level level) {
		return bedRule.canSleep(level)
			|| SmartTradeConfig.sovereignShift() && Level.OVERWORLD.equals(level.dimension());
	}
}
