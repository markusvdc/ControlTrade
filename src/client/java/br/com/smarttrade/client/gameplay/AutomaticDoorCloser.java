package br.com.smarttrade.client.gameplay;

import br.com.smarttrade.SmartTrade;
import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.mixin.OpenableBlockInvoker;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;

public final class AutomaticDoorCloser {
	private static final long CLOSE_DELAY_NANOS = 5_000_000_000L;
	private static final long VERIFY_DELAY_NANOS = 500_000_000L;
	private static final int MAX_CLOSE_ATTEMPTS = 2;
	private static final Map<BlockPos, PendingClosure> PENDING = new HashMap<>();
	private static final Map<BlockPos, PendingVerification> VERIFYING = new HashMap<>();
	private static ClientLevel trackedLevel;

	private AutomaticDoorCloser() {
	}

	public static void register() {
		ClientTickEvents.END_CLIENT_TICK.register(AutomaticDoorCloser::tick);
	}

	public static void schedule(
		ClientLevel level,
		BlockPos pos,
		Block block,
		BlockHitResult hitResult
	) {
		track(level);
		PENDING.put(
			pos.immutable(),
			new PendingClosure(System.nanoTime() + CLOSE_DELAY_NANOS, block, hitResult)
		);
		SmartTrade.LOGGER.info(
			"Automatic door scheduled: block={} pos={} dimension={} delaySeconds=5",
			block,
			pos,
			level.dimension().identifier()
		);
	}

	public static void cancel(ClientLevel level, BlockPos pos) {
		track(level);
		if (PENDING.remove(pos) != null && !VERIFYING.containsKey(pos)) {
			SmartTrade.LOGGER.info(
				"Automatic door canceled: pos={} dimension={} reason=closed",
				pos,
				level.dimension().identifier()
			);
		}
	}

	private static void tick(Minecraft minecraft) {
		ClientLevel level = minecraft.level;
		if (level == null || minecraft.player == null) {
			clear();
			return;
		}

		track(level);
		if (!SmartTradeConfig.automaticDoorClosing()) {
			PENDING.clear();
			VERIFYING.clear();
			return;
		}

		long currentTime = System.nanoTime();
		Iterator<Map.Entry<BlockPos, PendingClosure>> iterator = PENDING.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<BlockPos, PendingClosure> entry = iterator.next();
			PendingClosure closure = entry.getValue();
			if (closure.closeAtNanos() > currentTime) {
				continue;
			}

			iterator.remove();
			BlockState state = level.getBlockState(entry.getKey());
			boolean sameOpenBlock = state.getBlock() == closure.block()
				&& isOpen(level, entry.getKey(), state);
			SmartTrade.LOGGER.info(
				"Automatic door deadline: pos={} dimension={} sameBlock={} open={} action={}",
				entry.getKey(),
				level.dimension().identifier(),
				state.getBlock() == closure.block(),
				isOpen(level, entry.getKey(), state),
				sameOpenBlock ? "close" : "ignore"
			);
			if (sameOpenBlock) {
				close(minecraft, entry.getKey(), closure.block(), closure.hitResult(), 1);
			}
		}

		Iterator<Map.Entry<BlockPos, PendingVerification>> verificationIterator =
			VERIFYING.entrySet().iterator();
		while (verificationIterator.hasNext()) {
			Map.Entry<BlockPos, PendingVerification> entry = verificationIterator.next();
			PendingVerification verification = entry.getValue();
			if (verification.verifyAtNanos() > currentTime) {
				continue;
			}

			BlockState state = level.getBlockState(entry.getKey());
			boolean sameBlock = state.getBlock() == verification.block();
			boolean open = sameBlock && isOpen(level, entry.getKey(), state);
			SmartTrade.LOGGER.info(
				"Automatic door verification: pos={} dimension={} attempt={} sameBlock={} open={} action={}",
				entry.getKey(),
				level.dimension().identifier(),
				verification.attempt(),
				sameBlock,
				open,
				open && verification.attempt() < MAX_CLOSE_ATTEMPTS ? "retry" : "finish"
			);
			if (open && verification.attempt() < MAX_CLOSE_ATTEMPTS) {
				int nextAttempt = verification.attempt() + 1;
				requestServerClose(
					minecraft,
					entry.getKey(),
					verification.block(),
					verification.hitResult(),
					nextAttempt
				);
				entry.setValue(new PendingVerification(
					System.nanoTime() + VERIFY_DELAY_NANOS,
					verification.block(),
					verification.hitResult(),
					nextAttempt
				));
			} else {
				verificationIterator.remove();
			}
		}
	}

	private static void close(
		Minecraft minecraft,
		BlockPos pos,
		Block block,
		BlockHitResult hitResult,
		int attempt
	) {
		requestServerClose(minecraft, pos, block, hitResult, attempt);
		VERIFYING.put(
			pos.immutable(),
			new PendingVerification(
				System.nanoTime() + VERIFY_DELAY_NANOS,
				block,
				hitResult,
				attempt
			)
		);
	}

	private static void requestServerClose(
		Minecraft minecraft,
		BlockPos pos,
		Block expectedBlock,
		BlockHitResult hitResult,
		int attempt
	) {
		var server = minecraft.getSingleplayerServer();
		if (server == null) {
			SmartTrade.LOGGER.info(
				"Automatic door server close skipped: pos={} attempt={} reason=no_integrated_server",
				pos,
				attempt
			);
			return;
		}

		var dimension = minecraft.level.dimension();
		var playerId = minecraft.player.getUUID();
		server.execute(() -> {
			ServerLevel serverLevel = server.getLevel(dimension);
			ServerPlayer serverPlayer = server.getPlayerList().getPlayer(playerId);
			if (serverLevel == null || serverPlayer == null) {
				SmartTrade.LOGGER.info(
					"Automatic door server close skipped: pos={} attempt={} reason=world_or_player_missing",
					pos,
					attempt
				);
				return;
			}

			BlockState state = serverLevel.getBlockState(pos);
			boolean canClose = state.getBlock() == expectedBlock
				&& isOpen(serverLevel, pos, state);
			if (!canClose) {
				SmartTrade.LOGGER.info(
					"Automatic door server close skipped: pos={} attempt={} sameBlock={} open={}",
					pos,
					attempt,
					state.getBlock() == expectedBlock,
					state.hasProperty(BlockStateProperties.OPEN)
						&& state.getValue(BlockStateProperties.OPEN)
				);
				return;
			}

			InteractionResult result = state.getValue(BlockStateProperties.OPEN)
				? ((OpenableBlockInvoker) state.getBlock())
					.smarttrade$useWithoutItem(state, serverLevel, pos, null, hitResult)
				: InteractionResult.PASS;
			boolean synchronizedDoorHalf = synchronizeDoorHalf(serverLevel, pos, state);
			SmartTrade.LOGGER.info(
				"Automatic door server close: pos={} dimension={} attempt={} result={} synchronizedDoorHalf={}",
				pos,
				dimension.identifier(),
				attempt,
				result,
				synchronizedDoorHalf
			);
		});
	}

	private static boolean isOpen(LevelReader level, BlockPos pos, BlockState state) {
		if (state.hasProperty(BlockStateProperties.OPEN)
			&& state.getValue(BlockStateProperties.OPEN)) {
			return true;
		}
		if (!(state.getBlock() instanceof DoorBlock)) {
			return false;
		}

		BlockState otherHalf = level.getBlockState(otherDoorHalfPos(pos, state));
		return otherHalf.getBlock() == state.getBlock()
			&& otherHalf.hasProperty(BlockStateProperties.OPEN)
			&& otherHalf.getValue(BlockStateProperties.OPEN);
	}

	private static boolean synchronizeDoorHalf(ServerLevel level, BlockPos pos, BlockState state) {
		if (!(state.getBlock() instanceof DoorBlock)) {
			return false;
		}

		BlockPos otherPos = otherDoorHalfPos(pos, state);
		BlockState otherState = level.getBlockState(otherPos);
		if (otherState.getBlock() != state.getBlock()
			|| !otherState.hasProperty(BlockStateProperties.OPEN)
			|| !otherState.getValue(BlockStateProperties.OPEN)) {
			return false;
		}

		level.setBlock(otherPos, otherState.setValue(BlockStateProperties.OPEN, false), 10);
		return true;
	}

	private static BlockPos otherDoorHalfPos(BlockPos pos, BlockState state) {
		return state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER
			? pos.above()
			: pos.below();
	}

	private static void track(ClientLevel level) {
		if (trackedLevel != level) {
			trackedLevel = level;
			PENDING.clear();
			VERIFYING.clear();
		}
	}

	private static void clear() {
		trackedLevel = null;
		PENDING.clear();
		VERIFYING.clear();
	}

	private record PendingClosure(
		long closeAtNanos,
		Block block,
		BlockHitResult hitResult
	) {
	}

	private record PendingVerification(
		long verifyAtNanos,
		Block block,
		BlockHitResult hitResult,
		int attempt
	) {
	}
}
