package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerReputationMixin {
	private static final int MAXIMUM_REPUTATION = 150;

	@Inject(method = "getPlayerReputation", at = @At("HEAD"), cancellable = true)
	private void smarttrade$useMaximumReputation(
		Player player,
		CallbackInfoReturnable<Integer> callback
	) {
		if (SmartTradeConfig.maximumVillagerReputation()) {
			callback.setReturnValue(MAXIMUM_REPUTATION);
		}
	}
}
