package br.com.smarttrade.mixin;

import br.com.smarttrade.client.gameplay.InsaneDifficultyClient;
import br.com.smarttrade.config.SmartTradeConfig;
import java.util.Arrays;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.layouts.EqualSpacingLayout;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.DifficultyButtons;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundChangeDifficultyPacket;
import net.minecraft.network.protocol.game.ServerboundLockDifficultyPacket;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DifficultyButtons.class)
public abstract class DifficultyButtonsMixin {
	@Inject(method = "create", at = @At("HEAD"), cancellable = true)
	private static void smarttrade$createWithInsaneDifficulty(
		Minecraft minecraft,
		Level level,
		Screen screen,
		CallbackInfoReturnable<DifficultyButtons> callback
	) {
		if (!SmartTradeConfig.insaneDifficulty()) {
			return;
		}
		if (level.getLevelData().isDifficultyLocked() && !level.getLevelData().isHardcore()) {
			InsaneDifficultyClient.unlockDifficulty(minecraft);
		}

		DifficultyChoice initial = InsaneDifficultyClient.isSelected(minecraft)
			? DifficultyChoice.INSANE
			: DifficultyChoice.from(level.getDifficulty());
		CycleButton<DifficultyChoice> difficultyButton = CycleButton.builder(DifficultyChoice::displayName, initial)
			.withValues(Arrays.asList(DifficultyChoice.values()))
			.create(
				0,
				0,
				150,
				20,
				Component.translatable("options.difficulty"),
				(button, choice) -> {
					InsaneDifficultyClient.setSelected(minecraft, choice == DifficultyChoice.INSANE);
					minecraft.getConnection().send(new ServerboundChangeDifficultyPacket(choice.vanillaDifficulty()));
				}
			);
		LockIconButton lockButton = new LockIconButton(0, 0, button -> minecraft.gui.setScreen(new ConfirmScreen(result -> {
			minecraft.gui.setScreen(screen);
			if (result) {
				minecraft.getConnection().send(new ServerboundLockDifficultyPacket(true));
				((LockIconButton)button).setLocked(true);
				button.active = false;
				difficultyButton.active = false;
			}
		}, Component.translatable("difficulty.lock.title"), Component.translatable(
			"difficulty.lock.question",
			difficultyButton.getValue().displayName()
		))));
		lockButton.visible = false;
		lockButton.active = false;
		difficultyButton.active = !level.getLevelData().isHardcore() && minecraft.hasSingleplayerServer();
		EqualSpacingLayout layout = new EqualSpacingLayout(150, 0, EqualSpacingLayout.Orientation.HORIZONTAL);
		layout.addChild(difficultyButton);
		callback.setReturnValue(new DifficultyButtons(layout, unsafeCast(difficultyButton), lockButton, level));
	}

	@Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
	private void smarttrade$refreshInsaneDifficulty(Minecraft minecraft, CallbackInfo callback) {
		if (!SmartTradeConfig.insaneDifficulty()) {
			return;
		}
		DifficultyButtons buttons = (DifficultyButtons)(Object)this;
		CycleButton<DifficultyChoice> difficultyButton = unsafeCast(buttons.difficultyButton());
		DifficultyChoice selected = InsaneDifficultyClient.isSelected(minecraft)
			? DifficultyChoice.INSANE
			: DifficultyChoice.from(buttons.level().getDifficulty());
		difficultyButton.setValue(selected);
		if (buttons.level().getLevelData().isDifficultyLocked() && !buttons.level().getLevelData().isHardcore()) {
			InsaneDifficultyClient.unlockDifficulty(minecraft);
		}
		buttons.lockButton().visible = false;
		buttons.lockButton().active = false;
		difficultyButton.active = !buttons.level().getLevelData().isHardcore() && minecraft.hasSingleplayerServer();
		callback.cancel();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static <T> CycleButton<T> unsafeCast(CycleButton<?> button) {
		return (CycleButton)button;
	}

	private enum DifficultyChoice {
		PEACEFUL(Difficulty.PEACEFUL),
		EASY(Difficulty.EASY),
		NORMAL(Difficulty.NORMAL),
		HARD(Difficulty.HARD),
		INSANE(Difficulty.HARD);

		private final Difficulty vanillaDifficulty;

		DifficultyChoice(Difficulty vanillaDifficulty) {
			this.vanillaDifficulty = vanillaDifficulty;
		}

		private Difficulty vanillaDifficulty() {
			return this.vanillaDifficulty;
		}

		private Component displayName() {
			return this == INSANE
				? Component.translatable("smarttrade.difficulty.insane")
				: this.vanillaDifficulty.getDisplayName();
		}

		private static DifficultyChoice from(Difficulty difficulty) {
			return switch (difficulty) {
				case PEACEFUL -> PEACEFUL;
				case EASY -> EASY;
				case NORMAL -> NORMAL;
				case HARD -> HARD;
			};
		}
	}
}
