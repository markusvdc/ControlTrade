package br.com.smarttrade.mixin;

import br.com.smarttrade.client.gameplay.InsaneDifficultyClient;
import br.com.smarttrade.config.SmartTradeConfig;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.storage.LevelSummary;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldSelectionList.WorldListEntry.class)
public abstract class WorldListEntryMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Shadow
	@Final
	private LevelSummary summary;

	@Shadow
	@Final
	private StringWidget idAndLastPlayedText;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void smarttrade$showInsaneDifficulty(
		WorldSelectionList outerList,
		WorldSelectionList list,
		LevelSummary summary,
		CallbackInfo callback
	) {
		if (!SmartTradeConfig.insaneDifficulty()) {
			return;
		}

		String label = InsaneDifficultyClient.isSelectedWorld(this.minecraft, this.summary.getLevelId())
			? Component.translatable("smarttrade.difficulty.insane").getString()
			: this.summary.getSettings().difficultySettings().difficulty().getDisplayName().getString();
		long lastPlayed = this.summary.getLastPlayed();
		if (lastPlayed != -1L) {
			ZonedDateTime time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(lastPlayed), ZoneId.systemDefault());
			label = label + " (" + WorldSelectionList.DATE_FORMAT.format(time) + ")";
		}
		this.idAndLastPlayedText.setMessage(Component.literal(label).withColor(-8355712));
		this.idAndLastPlayedText.setTooltip(null);
	}
}
