package br.com.smarttrade.gameplay;

import br.com.smarttrade.SmartTrade;
import br.com.smarttrade.config.SmartTradeConfig;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public final class AutomaticDoorCloser {
	private static final int CLOSE_DELAY_TICKS = 100;
	private static final Map<ServerLevel, Map<BlockPos, PendingClosure>> PENDING = new HashMap<>();

	private AutomaticDoorCloser() {
	}

	public static void register() {
		ServerTickEvents.END_LEVEL_TICK.register(AutomaticDoorCloser::tick);
	}

	public static void schedule(
		ServerLevel level,
		BlockPos pos,
		Block block,
		ServerPlayer player,
		BlockHitResult hitResult
	) {
		PENDING.computeIfAbsent(level, ignored -> new HashMap<>()).put(
			pos.immutable(),
			new PendingClosure(level.getGameTime() + CLOSE_DELAY_TICKS, block, player.getUUID(), hitResult)
		);
		SmartTrade.LOGGER.info(
			"Automatic door scheduled: pos={} dimension={} closeAt={} currentTime={}",
			pos,
			level.dimension().identifier(),
			level.getGameTime() + CLOSE_DELAY_TICKS,
			level.getGameTime()
		);
	}

	public static void cancel(ServerLevel level, BlockPos pos) {
		Map<BlockPos, PendingClosure> closures = PENDING.get(level);
		if (closures == null) {
			return;
		}

		closures.remove(pos);
		if (closures.isEmpty()) {
			PENDING.remove(level);
		}
	}

	private static void tick(ServerLevel level) {
		Map<BlockPos, PendingClosure> closures = PENDING.get(level);
		if (closures == null) {
			return;
		}

		if (!SmartTradeConfig.automaticDoorClosing()) {
			PENDING.remove(level);
			return;
		}

		long gameTime = level.getGameTime();
		Iterator<Map.Entry<BlockPos, PendingClosure>> iterator = closures.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<BlockPos, PendingClosure> entry = iterator.next();
			PendingClosure closure = entry.getValue();
			if (closure.closeAt() > gameTime) {
				continue;
			}

			iterator.remove();
			BlockState currentState = level.getBlockState(entry.getKey());
			if (currentState.getBlock() != closure.block()
				|| !currentState.hasProperty(BlockStateProperties.OPEN)
				|| !currentState.getValue(BlockStateProperties.OPEN)) {
				SmartTrade.LOGGER.info(
					"Automatic door skipped: pos={} dimension={} blockChanged={} open={}",
					entry.getKey(),
					level.dimension().identifier(),
					currentState.getBlock() != closure.block(),
					currentState.hasProperty(BlockStateProperties.OPEN)
						&& currentState.getValue(BlockStateProperties.OPEN)
				);
				continue;
			}

			ServerPlayer player = level.getServer().getPlayerList().getPlayer(closure.playerId());
			if (player != null) {
				SmartTrade.LOGGER.info(
					"Automatic door closing: pos={} dimension={} gameTime={}",
					entry.getKey(),
					level.dimension().identifier(),
					gameTime
				);
				currentState.useWithoutItem(level, player, closure.hitResult());
			} else {
				SmartTrade.LOGGER.info(
					"Automatic door skipped: pos={} dimension={} playerUnavailable=true",
					entry.getKey(),
					level.dimension().identifier()
				);
			}
		}

		if (closures.isEmpty()) {
			PENDING.remove(level);
		}
	}

	private record PendingClosure(long closeAt, Block block, UUID playerId, BlockHitResult hitResult) {
	}
}
