package br.com.smarttrade.mixin;

import br.com.smarttrade.SmartTrade;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public abstract class KeyboardHandlerMixin {
	@Shadow
	@Final
	private Minecraft minecraft;

	@Inject(method = "keyPress", at = @At("HEAD"))
	private void smarttrade$logEscapeReceived(long handle, int action, KeyEvent event, CallbackInfo callback) {
		if (event.isEscape()) {
			this.smarttrade$logEscape("received", handle, action);
		}
	}

	@Inject(method = "keyPress", at = @At("RETURN"))
	private void smarttrade$logEscapeProcessed(long handle, int action, KeyEvent event, CallbackInfo callback) {
		if (event.isEscape()) {
			this.smarttrade$logEscape("processed", handle, action);
		}
	}

	private void smarttrade$logEscape(String phase, long handle, int action) {
		SmartTrade.LOGGER.info(
			"ESC {}: action={} matchingWindow={} focused={} screen={} overlay={}",
			phase,
			switch (action) {
				case 0 -> "release";
				case 1 -> "press";
				case 2 -> "repeat";
				default -> Integer.toString(action);
			},
			handle == this.minecraft.getWindow().handle(),
			this.minecraft.isWindowActive(),
			this.minecraft.gui.screen() == null
				? "none"
				: this.minecraft.gui.screen().getClass().getSimpleName(),
			this.minecraft.gui.overlay() == null
				? "none"
				: this.minecraft.gui.overlay().getClass().getSimpleName()
		);
	}
}
