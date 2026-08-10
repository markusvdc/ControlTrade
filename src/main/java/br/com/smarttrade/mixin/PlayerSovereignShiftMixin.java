package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.world.attribute.BedRule;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Player.class)
public abstract class PlayerSovereignShiftMixin {
	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/attribute/BedRule;canSleep(Lnet/minecraft/world/level/Level;)Z"
		)
	)
	private boolean smarttrade$keepDaytimeSleepActive(BedRule bedRule, Level level) {
		return bedRule.canSleep(level)
			|| SmartTradeConfig.sovereignShift() && Level.OVERWORLD.equals(level.dimension());
	}
}
