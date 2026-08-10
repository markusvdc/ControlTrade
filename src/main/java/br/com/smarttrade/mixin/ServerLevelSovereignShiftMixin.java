package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.clock.ClockTimeMarker;
import net.minecraft.world.clock.ClockTimeMarkers;
import net.minecraft.world.clock.ServerClockManager;
import net.minecraft.world.clock.WorldClock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ServerLevelSovereignShiftMixin {
	@Redirect(
		method = "tick",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/world/clock/ServerClockManager;moveToTimeMarker(Lnet/minecraft/core/Holder;Lnet/minecraft/resources/ResourceKey;)Z"
		)
	)
	private boolean smarttrade$moveToOppositeShift(
		ServerClockManager clockManager,
		Holder<WorldClock> clock,
		ResourceKey<ClockTimeMarker> originalMarker
	) {
		if (!SmartTradeConfig.sovereignShift()) {
			return clockManager.moveToTimeMarker(clock, originalMarker);
		}
		ServerLevel level = (ServerLevel)(Object)this;
		ResourceKey<ClockTimeMarker> destination =
			level.isDarkOutside() ? originalMarker : ClockTimeMarkers.NIGHT;
		return clockManager.moveToTimeMarker(clock, destination);
	}
}
