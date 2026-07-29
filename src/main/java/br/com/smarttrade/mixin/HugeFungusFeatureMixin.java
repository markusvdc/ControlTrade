package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.FixedHeightGrowthContext;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.feature.HugeFungusFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HugeFungusFeature.class)
public abstract class HugeFungusFeatureMixin {
	@Redirect(
		method = "place",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/util/Mth;nextInt(Lnet/minecraft/util/RandomSource;II)I"
		)
	)
	private int smarttrade$useFixedFungusHeight(RandomSource random, int minimum, int maximum) {
		Integer fixedHeight = FixedHeightGrowthContext.fixedTreeHeight();
		return fixedHeight != null ? fixedHeight : Mth.nextInt(random, minimum, maximum);
	}
}
