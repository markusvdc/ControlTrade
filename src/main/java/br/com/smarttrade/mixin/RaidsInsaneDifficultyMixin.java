package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.entity.raid.Raids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Raids.class)
public abstract class RaidsInsaneDifficultyMixin {
	@Inject(method = "getOrCreateRaid", at = @At("RETURN"))
	private void smarttrade$useTwelveWaves(
		ServerLevel level,
		BlockPos pos,
		CallbackInfoReturnable<Raid> callback
	) {
		if (InsaneDifficulty.isActive(level)) {
			((RaidAccessor)(Object)callback.getReturnValue()).smarttrade$setNumGroups(12);
		}
	}
}
