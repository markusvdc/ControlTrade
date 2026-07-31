package br.com.smarttrade.mixin;

import br.com.smarttrade.SmartTrade;
import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.AutomaticDoorCloser;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({DoorBlock.class, FenceGateBlock.class})
public abstract class OpenableBlockMixin {
	@Inject(method = "useWithoutItem", at = @At("RETURN"))
	private void smarttrade$trackPlayerOpening(
		BlockState initialState,
		Level level,
		BlockPos pos,
		Player player,
		BlockHitResult hitResult,
		CallbackInfoReturnable<InteractionResult> callback
	) {
		if (!(level instanceof ServerLevel serverLevel) || !(player instanceof ServerPlayer serverPlayer)) {
			return;
		}

		Block block = (Block) (Object) this;
		BlockState currentState = serverLevel.getBlockState(pos);
		boolean wasOpen = initialState.getValue(BlockStateProperties.OPEN);
		boolean isSameOpenBlock = currentState.getBlock() == block
			&& currentState.getValue(BlockStateProperties.OPEN);
		SmartTrade.LOGGER.info(
			"Automatic door interaction: block={} pos={} dimension={} wasOpen={} isOpen={} enabled={}",
			BuiltInRegistries.BLOCK.getKey(block),
			pos,
			serverLevel.dimension().identifier(),
			wasOpen,
			isSameOpenBlock,
			SmartTradeConfig.automaticDoorClosing()
		);

		if (SmartTradeConfig.automaticDoorClosing() && !wasOpen && isSameOpenBlock) {
			AutomaticDoorCloser.schedule(serverLevel, pos, block, serverPlayer, hitResult);
		} else if (wasOpen && !isSameOpenBlock) {
			AutomaticDoorCloser.cancel(serverLevel, pos);
		}
	}
}
