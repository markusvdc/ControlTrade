package br.com.smarttrade.mixin;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.OptionInstance$OptionInstanceSliderButton")
public abstract class PauseAudioSliderMixin {
	@Inject(method = "updateMessage", at = @At("RETURN"))
	private void smarttrade$uppercasePauseAudioLabel(CallbackInfo callbackInfo) {
		AbstractWidget slider = (AbstractWidget) (Object) this;
		if (Minecraft.getInstance().gui.screen() instanceof PauseScreen && slider.getWidth() == 200) {
			slider.setMessage(Component.literal(slider.getMessage().getString().toUpperCase(Locale.ROOT)));
		}
	}
}
