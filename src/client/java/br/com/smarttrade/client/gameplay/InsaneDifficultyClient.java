package br.com.smarttrade.client.gameplay;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.client.Minecraft;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;

public final class InsaneDifficultyClient {
	private InsaneDifficultyClient() {
	}

	public static boolean isSelected(Minecraft minecraft) {
		IntegratedServer server = minecraft.getSingleplayerServer();
		return server != null && InsaneDifficulty.isSelected(server.overworld());
	}

	public static void setSelected(Minecraft minecraft, boolean selected) {
		IntegratedServer server = minecraft.getSingleplayerServer();
		if (server != null) {
			server.execute(() -> InsaneDifficulty.setSelected(server.overworld(), selected));
		}
	}

	public static void unlockDifficulty(Minecraft minecraft) {
		if (minecraft.hasSingleplayerServer() && minecraft.getConnection() != null) {
			minecraft.getConnection().send(new ServerboundLockDifficultyPacket(false));
		}
	}

	public static boolean isSelectedWorld(Minecraft minecraft, String levelId) {
		Path savesDirectory = minecraft.getLevelSource().getBaseDir().toAbsolutePath().normalize();
		Path worldDirectory = savesDirectory.resolve(levelId).normalize();
		if (!worldDirectory.startsWith(savesDirectory)) {
			return false;
		}

		Path dataFile = worldDirectory.resolve(
			"dimensions/minecraft/overworld/data/smarttrade/insane_difficulty.dat"
		);
		try {
			CompoundTag root = NbtIo.readCompressed(dataFile, NbtAccounter.unlimitedHeap());
			return root.getCompoundOrEmpty("data").getBooleanOr("enabled", false);
		} catch (IOException exception) {
			return false;
		}
	}
}
