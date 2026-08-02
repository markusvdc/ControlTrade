package br.com.smarttrade.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BlockBehaviour.class)
public interface OpenableBlockInvoker {
	@Invoker("useWithoutItem")
	InteractionResult smarttrade$useWithoutItem(
		BlockState state,
		Level level,
		BlockPos pos,
		Player player,
		BlockHitResult hitResult
	);
}
