package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.FixedHeightGrowthContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MushroomBlock.class)
public abstract class MushroomBlockMixin {
	@Inject(method = "growMushroom", at = @At("HEAD"))
	private void smarttrade$beginFixedHeightGrowth(
		ServerLevel level,
		BlockPos pos,
		BlockState state,
		RandomSource random,
		CallbackInfoReturnable<Boolean> callback
	) {
		FixedHeightGrowthContext.clear();
		if (!SmartTradeConfig.fixedHugeMushroomHeight()) {
			return;
		}

		BlockState ground = level.getBlockState(pos.below());
		if (!ground.is(Blocks.CRIMSON_NYLIUM) && !ground.is(Blocks.WARPED_NYLIUM)) {
			return;
		}

		if (state.is(Blocks.RED_MUSHROOM)) {
			FixedHeightGrowthContext.begin(5);
		} else if (state.is(Blocks.BROWN_MUSHROOM)) {
			FixedHeightGrowthContext.begin(4);
		}
	}

	@Inject(method = "growMushroom", at = @At("RETURN"))
	private void smarttrade$finishFixedHeightGrowth(
		ServerLevel level,
		BlockPos pos,
		BlockState state,
		RandomSource random,
		CallbackInfoReturnable<Boolean> callback
	) {
		FixedHeightGrowthContext.clear();
	}
}
