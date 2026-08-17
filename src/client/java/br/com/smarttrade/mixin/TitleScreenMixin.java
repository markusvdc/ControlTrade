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
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
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
	private static final int HALF_BUTTON_WIDTH = 98;
	private static final int BUTTON_HEIGHT = 20;
	private static final int COLUMN_GAP = 4;
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
		Component originalModMenuMessage = ModMenuApi.createModsButtonText();
		Component modMenuMessage = uppercaseModsButtonText(originalModMenuMessage);

		for (GuiEventListener child : children) {
			if (!(child instanceof AbstractWidget widget)) {
				continue;
			}
			Component message = widget.getMessage();
			if (widget instanceof FriendsButton
				|| widget instanceof SpriteIconButton
				|| message.equals(originalModMenuMessage)) {
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
			modMenuMessage,
			button -> minecraft.gui.setScreen(ModMenuApi.createModsScreen(screen))
		).build();
		Button resources = Button.builder(
			Component.translatable("smarttrade.menu.resources"),
			button -> minecraft.gui.setScreen(new PackSelectionScreen(
				minecraft.getResourcePackRepository(),
				repository -> {
					minecraft.options.updateResourcePacks(repository);
					minecraft.gui.setScreen(screen);
				},
				minecraft.getResourcePackDirectory(),
				Component.translatable("resourcePack.title")
			))
		).build();
		invoker.smarttrade$addRenderableWidget(friends);
		invoker.smarttrade$addRenderableWidget(resources);
		invoker.smarttrade$addRenderableWidget(mods);

		AbstractWidget singleplayer = find(retainedButtons, "menu.singleplayer");
		AbstractWidget multiplayer = find(retainedButtons, "menu.multiplayer");
		AbstractWidget realms = find(retainedButtons, "menu.online");
		AbstractWidget options = find(retainedButtons, "menu.options");
		AbstractWidget quit = find(retainedButtons, "menu.quit");
		List<AbstractWidget> ordered = List.of(singleplayer, multiplayer, resources, mods, friends, realms, options, quit);
		Component[] labels = {
			Component.translatable("smarttrade.menu.player"),
			Component.translatable("smarttrade.menu.players"),
			Component.translatable("smarttrade.menu.resources"),
			modMenuMessage,
			Component.translatable("smarttrade.menu.friends"),
			Component.translatable("smarttrade.menu.realms"),
			Component.translatable("smarttrade.menu.options"),
			Component.translatable("smarttrade.menu.quit")
		};
		int left = (screen.width - BUTTON_WIDTH) / 2;
		int top = screen.height / 4 + 48;
		for (int index = 0; index < ordered.size(); index++) {
			AbstractWidget button = ordered.get(index);
			button.setMessage(labels[index]);
			button.setHeight(BUTTON_HEIGHT);
			if (index < 2) {
				button.setWidth(BUTTON_WIDTH);
				button.setPosition(left, top + index * ROW_SPACING);
				continue;
			}
			int pairedIndex = index - 2;
			int column = pairedIndex % 2;
			int row = pairedIndex / 2 + 2;
			button.setWidth(HALF_BUTTON_WIDTH);
			button.setPosition(
				left + column * (HALF_BUTTON_WIDTH + COLUMN_GAP),
				top + row * ROW_SPACING
			);
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

	private static Component uppercaseModsButtonText(Component original) {
		MutableComponent uppercase = Component.translatable("smarttrade.menu.modmenu").withStyle(original.getStyle());
		original.getSiblings().forEach(sibling -> uppercase.append(sibling.copy()));
		return uppercase;
	}

	private static AbstractWidget find(List<AbstractWidget> buttons, String translationKey) {
		Component expected = Component.translatable(translationKey);
		return buttons.stream()
			.filter(button -> button.getMessage().equals(expected))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Missing title-screen button: " + translationKey));
	}
}
