package br.com.smarttrade.mixin;

import br.com.smarttrade.client.gameplay.AutomaticDoorCloser;
import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
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
public abstract class ClientOpenableBlockMixin {
	@Inject(method = "useWithoutItem", at = @At("RETURN"))
	private void smarttrade$trackPlayerOpening(
		BlockState resultingState,
		Level level,
		BlockPos pos,
		Player player,
		BlockHitResult hitResult,
		CallbackInfoReturnable<InteractionResult> callback
	) {
		if (!(level instanceof ClientLevel clientLevel) || !(player instanceof LocalPlayer)) {
			return;
		}

		Block block = (Block) (Object) this;
		boolean isOpen = resultingState.getValue(BlockStateProperties.OPEN);
		boolean interactionSucceeded = callback.getReturnValue() == InteractionResult.SUCCESS;
		boolean supportedBlock = resultingState.is(BlockTags.WOODEN_DOORS)
			|| block instanceof FenceGateBlock;
		if (SmartTradeConfig.automaticDoorClosing() && supportedBlock && isOpen && interactionSucceeded) {
			AutomaticDoorCloser.schedule(clientLevel, pos, block, hitResult);
		} else if (supportedBlock && !isOpen && interactionSucceeded) {
			AutomaticDoorCloser.cancel(clientLevel, pos);
		}
	}
}
