package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.FixedHeightGrowthContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherFungusBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetherFungusBlock.class)
public abstract class NetherFungusBlockMixin {
	@Inject(method = "performBonemeal", at = @At("HEAD"))
	private void smarttrade$beginFixedFungusHeight(
		ServerLevel level,
		RandomSource random,
		BlockPos pos,
		BlockState state,
		CallbackInfo callback
	) {
		FixedHeightGrowthContext.clear();
		if (
			SmartTradeConfig.fixedHugeMushroomHeight()
				&& (state.is(Blocks.CRIMSON_FUNGUS) || state.is(Blocks.WARPED_FUNGUS))
		) {
			FixedHeightGrowthContext.begin(6);
		}
	}

	@Inject(method = "performBonemeal", at = @At("RETURN"))
	private void smarttrade$finishFixedFungusHeight(
		ServerLevel level,
		RandomSource random,
		BlockPos pos,
		BlockState state,
		CallbackInfo callback
	) {
		FixedHeightGrowthContext.clear();
	}
}
