package br.com.smarttrade.gameplay;

import br.com.smarttrade.config.SmartTradeConfig;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

public final class InsaneDifficulty extends SavedData {
	private static final Codec<InsaneDifficulty> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
			Codec.BOOL.optionalFieldOf("enabled", false).forGetter(InsaneDifficulty::isSelected)
		).apply(instance, InsaneDifficulty::new)
	);
	private static final SavedDataType<InsaneDifficulty> TYPE = new SavedDataType<>(
		Identifier.fromNamespaceAndPath("smarttrade", "insane_difficulty"),
		InsaneDifficulty::new,
		CODEC,
		DataFixTypes.SAVED_DATA_COMMAND_STORAGE
	);

	private boolean selected;

	private InsaneDifficulty() {
	}

	private InsaneDifficulty(boolean selected) {
		this.selected = selected;
	}

	public static boolean isActive(Level level) {
		return SmartTradeConfig.insaneDifficulty()
			&& level instanceof ServerLevel serverLevel
			&& data(serverLevel).isSelected();
	}

	public static boolean isSelected(ServerLevel level) {
		return data(level).isSelected();
	}

	public static void setSelected(ServerLevel level, boolean selected) {
		InsaneDifficulty data = data(level);
		if (data.selected != selected) {
			data.selected = selected;
			data.setDirty();
		}
	}

	private static InsaneDifficulty data(ServerLevel level) {
		return level.getServer().overworld().getDataStorage().computeIfAbsent(TYPE);
	}

	private boolean isSelected() {
		return this.selected;
	}
}
