package br.com.smarttrade.client.gameplay;

import br.com.smarttrade.config.SmartTradeConfig;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;

public final class AutomaticDoorCloser {
	private static final long CLOSE_DELAY_NANOS = 5_000_000_000L;
	private static final Map<BlockPos, PendingClosure> PENDING = new HashMap<>();
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
	}

	public static void cancel(ClientLevel level, BlockPos pos) {
		track(level);
		PENDING.remove(pos);
	}

	private static void tick(Minecraft minecraft) {
		ClientLevel level = minecraft.level;
		if (level == null || minecraft.player == null || minecraft.gameMode == null) {
			clear();
			return;
		}

		track(level);
		if (!SmartTradeConfig.automaticDoorClosing()) {
			PENDING.clear();
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
				&& state.hasProperty(BlockStateProperties.OPEN)
				&& state.getValue(BlockStateProperties.OPEN);
			if (sameOpenBlock) {
				minecraft.gameMode.useItemOn(
					minecraft.player,
					InteractionHand.MAIN_HAND,
					closure.hitResult()
				);
			}
		}
	}

	private static void track(ClientLevel level) {
		if (trackedLevel != level) {
			trackedLevel = level;
			PENDING.clear();
		}
	}

	private static void clear() {
		trackedLevel = null;
		PENDING.clear();
	}

	private record PendingClosure(
		long closeAtNanos,
		Block block,
		BlockHitResult hitResult
	) {
	}
}
