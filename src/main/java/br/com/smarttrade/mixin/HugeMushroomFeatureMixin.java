package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.MushroomGrowthContext;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.AbstractHugeMushroomFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHugeMushroomFeature.class)
public abstract class HugeMushroomFeatureMixin {
	@Inject(method = "getTreeHeight", at = @At("HEAD"), cancellable = true)
	private void smarttrade$useFixedTreeHeight(
		RandomSource random,
		CallbackInfoReturnable<Integer> callback
	) {
		Integer fixedHeight = MushroomGrowthContext.fixedTreeHeight();
		if (fixedHeight != null) {
			callback.setReturnValue(fixedHeight);
		}
	}
}
