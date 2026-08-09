package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.FixedHeightGrowthContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SaplingBlock.class)
public abstract class SaplingBlockMixin {
	@Inject(method = "advanceTree", at = @At("HEAD"))
	private void smarttrade$beginFixedTreeHeight(
		ServerLevel level,
		BlockPos pos,
		BlockState state,
		RandomSource random,
		CallbackInfo callback
	) {
		FixedHeightGrowthContext.clear();
		if (
			SmartTradeConfig.fixedHugeMushroomHeight()
				&& (
					state.is(Blocks.JUNGLE_SAPLING)
						|| state.is(Blocks.ACACIA_SAPLING)
						|| state.is(Blocks.CHERRY_SAPLING)
						|| state.is(Blocks.BIRCH_SAPLING)
				)
		) {
			FixedHeightGrowthContext.begin(6);
		}
	}

	@Inject(method = "advanceTree", at = @At("RETURN"))
	private void smarttrade$finishFixedTreeHeight(
		ServerLevel level,
		BlockPos pos,
		BlockState state,
		RandomSource random,
		CallbackInfo callback
	) {
		FixedHeightGrowthContext.clear();
	}
}
