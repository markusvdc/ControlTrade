package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.client.gui.screens.social.SocialInteractionsScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonLinks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin {
	@Shadow
	private boolean inWorld;

	@Inject(method = "init", at = @At("RETURN"))
	private void smarttrade$addInGameOptions(CallbackInfo callbackInfo) {
		if (!this.inWorld || !SmartTradeConfig.compactGameMenus()) {
			return;
		}
		OptionsScreen screen = (OptionsScreen) (Object) this;
		Minecraft minecraft = Minecraft.getInstance();
		AbstractWidget credits = find(screen, "options.credits_and_attribution");
		AbstractWidget telemetry = find(screen, "options.telemetry");
		int left = Math.min(credits.getX(), telemetry.getX());
		int right = Math.max(credits.getX(), telemetry.getX());
		int top = Math.max(credits.getY(), telemetry.getY()) + 24;
		ScreenInvoker invoker = (ScreenInvoker) this;
		invoker.smarttrade$addRenderableWidget(Button.builder(
			Component.translatable("smarttrade.menu.advancements"),
			button -> minecraft.gui.setScreen(new AdvancementsScreen(
				minecraft.player.connection.getAdvancements(),
				screen
			))
		).bounds(left, top, 150, 20).build());
		invoker.smarttrade$addRenderableWidget(Button.builder(
			Component.translatable("smarttrade.menu.report"),
			button -> minecraft.gui.setScreen(new SocialInteractionsScreen(screen))
		).bounds(right, top, 150, 20).build());
		invoker.smarttrade$addRenderableWidget(Button.builder(
			Component.translatable("smarttrade.menu.feedback"),
			ConfirmLinkScreen.confirmLink(
				screen,
				SharedConstants.getCurrentVersion().stable()
					? CommonLinks.RELEASE_FEEDBACK
					: CommonLinks.SNAPSHOT_FEEDBACK
			)
		).bounds(left, top + 24, 150, 20).build());
		invoker.smarttrade$addRenderableWidget(Button.builder(
			Component.translatable("smarttrade.menu.inform"),
			ConfirmLinkScreen.confirmLink(screen, CommonLinks.SNAPSHOT_BUGS_FEEDBACK)
		).bounds(right, top + 24, 150, 20).build());
	}

	private static AbstractWidget find(OptionsScreen screen, String translationKey) {
		Component expected = Component.translatable(translationKey);
		return screen.children().stream()
			.filter(AbstractWidget.class::isInstance)
			.map(AbstractWidget.class::cast)
			.filter(widget -> widget.getMessage().equals(expected))
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("Missing options button: " + translationKey));
	}
}
