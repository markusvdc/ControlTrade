package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import com.mojang.realmsclient.gui.screens.RealmsNotificationsScreen;
import com.terraformersmc.modmenu.api.ModMenuApi;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.FriendsButton;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.friends.FriendsOverlayScreen;
import net.minecraft.client.gui.screens.options.OnlineOptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TitleScreen.class, priority = 500)
public abstract class TitleScreenMixin {
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int ROW_SPACING = 24;

	@Shadow
	@Mutable
	private RealmsNotificationsScreen realmsNotificationsScreen;

	@ModifyArg(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V"
		),
		index = 1
	)
	private String smarttrade$hideVersionText(String versionText) {
		return SmartTradeConfig.compactGameMenus() ? "" : versionText;
	}

	@Inject(method = "init", at = @At("RETURN"))
	private void smarttrade$organizeOffGameMenu(CallbackInfo callbackInfo) {
		if (!SmartTradeConfig.compactGameMenus()) {
			return;
		}

		TitleScreen screen = (TitleScreen) (Object) this;
		ScreenInvoker invoker = (ScreenInvoker) this;
		List<? extends GuiEventListener> children = new ArrayList<>(screen.children());
		List<AbstractWidget> retainedButtons = new ArrayList<>();
		Component modMenuMessage = ModMenuApi.createModsButtonText();

		for (GuiEventListener child : children) {
			if (!(child instanceof AbstractWidget widget)) {
				continue;
			}
			Component message = widget.getMessage();
			if (widget instanceof FriendsButton
				|| widget instanceof SpriteIconButton
				|| message.equals(modMenuMessage)) {
				invoker.smarttrade$removeWidget(widget);
				continue;
			}
			if (isMenuButton(message)) {
				retainedButtons.add(widget);
			}
		}

		Minecraft minecraft = Minecraft.getInstance();
		Button friends = Button.builder(
			Component.translatable("smarttrade.menu.friends"),
			button -> OnlineOptionsScreen.confirmFriendsListEnabled(
				minecraft,
				() -> minecraft.gui.setScreen(new FriendsOverlayScreen(screen)),
				screen
			)
		).build();
		friends.active = !minecraft.isDemo();
		Button mods = Button.builder(
			Component.translatable("smarttrade.menu.modmenu"),
			button -> minecraft.gui.setScreen(ModMenuApi.createModsScreen(screen))
		).build();
		invoker.smarttrade$addRenderableWidget(friends);
		invoker.smarttrade$addRenderableWidget(mods);

		AbstractWidget singleplayer = find(retainedButtons, "menu.singleplayer");
		AbstractWidget multiplayer = find(retainedButtons, "menu.multiplayer");
		AbstractWidget realms = find(retainedButtons, "menu.online");
		AbstractWidget options = find(retainedButtons, "menu.options");
		AbstractWidget quit = find(retainedButtons, "menu.quit");
		List<AbstractWidget> ordered = List.of(singleplayer, multiplayer, friends, mods, realms, options, quit);
		Component[] labels = {
			Component.translatable("smarttrade.menu.player"),
			Component.translatable("smarttrade.menu.players"),
			Component.translatable("smarttrade.menu.friends"),
			Component.translatable("smarttrade.menu.modmenu"),
			Component.translatable("smarttrade.menu.realms"),
			Component.translatable("smarttrade.menu.options"),
			Component.translatable("smarttrade.menu.quit")
		};
		int left = (screen.width - BUTTON_WIDTH) / 2;
		int top = screen.height / 4 + 48;
		for (int index = 0; index < ordered.size(); index++) {
			AbstractWidget button = ordered.get(index);
			button.setMessage(labels[index]);
			button.setWidth(BUTTON_WIDTH);
			button.setHeight(BUTTON_HEIGHT);
			button.setPosition(left, top + index * ROW_SPACING);
		}

		this.realmsNotificationsScreen = null;
	}

	private static boolean isMenuButton(Component message) {
		return message.equals(Component.translatable("menu.singleplayer"))
			|| message.equals(Component.translatable("menu.multiplayer"))
			|| message.equals(Component.translatable("menu.online"))
			|| message.equals(Component.translatable("menu.options"))
			|| message.equals(Component.translatable("menu.quit"));
	}

	private static AbstractWidget find(List<AbstractWidget> buttons, String translationKey) {
		Component expected = Component.translatable(translationKey);
		return buttons.stream()
			.filter(button -> button.getMessage().equals(expected))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Missing title-screen button: " + translationKey));
	}
}
