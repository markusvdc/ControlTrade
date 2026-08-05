package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import com.terraformersmc.modmenu.api.ModMenuApi;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PauseScreen.class, priority = 500)
public abstract class PauseScreenMixin {
	private static final int BUTTON_WIDTH = 200;
	private static final int HALF_BUTTON_WIDTH = 98;
	private static final int BUTTON_HEIGHT = 20;
	private static final int COLUMN_GAP = 4;
	private static final int ROW_SPACING = 24;

	@Shadow
	private Button disconnectButton;

	@Inject(method = "init", at = @At("RETURN"))
	private void smarttrade$organizeInGameMenu(CallbackInfo callbackInfo) {
		if (!SmartTradeConfig.compactGameMenus()) {
			return;
		}
		PauseScreen screen = (PauseScreen) (Object) this;
		if (!screen.showsPauseMenu()) {
			return;
		}
		ScreenInvoker invoker = (ScreenInvoker) this;
		List<Button> originals = new ArrayList<>();
		for (GuiEventListener child : new ArrayList<>(screen.children())) {
			if (child instanceof AbstractWidget widget && !(widget instanceof StringWidget)) {
				if (widget instanceof Button button) {
					originals.add(button);
				}
				invoker.smarttrade$removeWidget(widget);
			}
		}

		Button resumeOriginal = find(originals, "menu.returnToGame");
		Button statsOriginal = find(originals, "gui.stats");
		Button lanOriginal = find(originals, "menu.multiplayerOptions.button");
		Minecraft minecraft = Minecraft.getInstance();
		List<Button> menu = List.of(
			button("smarttrade.menu.play", onPress(resumeOriginal)),
			button("smarttrade.menu.friends", button -> OnlineOptionsScreen.confirmFriendsListEnabled(
				minecraft,
				() -> minecraft.gui.setScreen(new FriendsOverlayScreen(screen)),
				screen
			)),
			button("smarttrade.menu.modmenu", button -> minecraft.gui.setScreen(ModMenuApi.createModsScreen(screen))),
			button("smarttrade.menu.lan", onPress(lanOriginal)),
			button("smarttrade.menu.statistics", onPress(statsOriginal)),
			button("smarttrade.menu.options", button -> minecraft.gui.setScreen(
				new OptionsScreen(screen, minecraft.options, true)
			)),
			button("smarttrade.menu.save_quit", onPress(this.disconnectButton))
		);
		int left = (screen.width - BUTTON_WIDTH) / 2;
		int top = screen.height / 4 + 48;
		for (int index = 0; index < menu.size(); index++) {
			Button button = menu.get(index);
			button.setHeight(BUTTON_HEIGHT);
			if (index == 0) {
				button.setWidth(BUTTON_WIDTH);
				button.setPosition(left, top);
			} else {
				int pairedIndex = index - 1;
				int column = pairedIndex % 2;
				int row = pairedIndex / 2 + 1;
				button.setWidth(HALF_BUTTON_WIDTH);
				button.setPosition(
					left + column * (HALF_BUTTON_WIDTH + COLUMN_GAP),
					top + row * ROW_SPACING
				);
			}
			invoker.smarttrade$addRenderableWidget(button);
		}
	}

	private static Button button(String key, Button.OnPress onPress) {
		return Button.builder(Component.translatable(key), onPress).size(BUTTON_WIDTH, BUTTON_HEIGHT).build();
	}

	private static Button.OnPress onPress(Button button) {
		return ((ButtonAccessor) button).smarttrade$getOnPress();
	}

	private static Button find(List<Button> buttons, String translationKey) {
		Component expected = Component.translatable(translationKey);
		return buttons.stream().filter(button -> button.getMessage().equals(expected)).findFirst()
			.orElseThrow(() -> new IllegalStateException("Missing pause-menu button: " + translationKey));
	}
}
