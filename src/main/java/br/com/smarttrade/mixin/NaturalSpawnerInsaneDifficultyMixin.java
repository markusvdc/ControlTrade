package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NaturalSpawner.class)
public abstract class NaturalSpawnerInsaneDifficultyMixin {
	private static final int WITCH_SPAWN_WEIGHT_MULTIPLIER = 12;
	private static final int ENDERMAN_SPAWN_WEIGHT_MULTIPLIER = 6;

	@Inject(method = "mobsAt", at = @At("RETURN"), cancellable = true)
	private static void smarttrade$increaseInsaneMonsterSpawnWeights(
		CallbackInfoReturnable<WeightedList<MobSpawnSettings.SpawnerData>> callback
	) {
		if (!SmartTradeConfig.insaneDifficulty()) {
			return;
		}

		WeightedList.Builder<MobSpawnSettings.SpawnerData> modifiedSpawns = WeightedList.builder();
		for (Weighted<MobSpawnSettings.SpawnerData> entry : callback.getReturnValue().unwrap()) {
			EntityType<?> type = entry.value().type();
			int multiplier = type == EntityTypes.WITCH
				? WITCH_SPAWN_WEIGHT_MULTIPLIER
				: type == EntityTypes.ENDERMAN ? ENDERMAN_SPAWN_WEIGHT_MULTIPLIER : 1;
			modifiedSpawns.add(entry.value(), entry.weight() * multiplier);
		}
		callback.setReturnValue(modifiedSpawns.build());
	}
}
