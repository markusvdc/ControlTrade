package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.FixedHeightGrowthContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TrunkPlacer.class)
public abstract class TrunkPlacerMixin {
	@Inject(method = "getTreeHeight", at = @At("HEAD"), cancellable = true)
	private void smarttrade$useFixedTreeHeight(
		RandomSource random,
		CallbackInfoReturnable<Integer> callback
	) {
		Integer fixedHeight = FixedHeightGrowthContext.fixedTreeHeight();
		if (fixedHeight != null) {
			callback.setReturnValue(fixedHeight);
		}
	}
}
