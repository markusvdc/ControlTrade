package br.com.smarttrade.client.screen.component;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractOptionSliderButton;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;

public final class PauseAudioSlider extends AbstractOptionSliderButton {
	private static final double MINIMUM_DRAG_DISTANCE = 3.0;

	private final SoundSource source;
	private double pressX;
	private double savedValue;
	private boolean mouseInteraction;

	public PauseAudioSlider(Options options, SoundSource source, int x, int y, int width) {
		super(options, x, y, width, 20, options.getSoundSourceVolume(source));
		this.source = source;
		this.savedValue = this.value;
		this.updateMessage();
	}

	@Override
	public void onClick(MouseButtonEvent event, boolean doubleClick) {
		this.pressX = event.x();
		this.savedValue = this.options.getSoundSourceVolume(this.source);
		this.mouseInteraction = true;
		super.onClick(event, doubleClick);
	}

	@Override
	public void onRelease(MouseButtonEvent event) {
		if (this.mouseInteraction) {
			if (Math.abs(event.x() - this.pressX) >= MINIMUM_DRAG_DISTANCE) {
				this.saveValue();
			} else {
				this.value = this.savedValue;
				this.updateMessage();
			}
			this.mouseInteraction = false;
		}
		super.onRelease(event);
	}

	@Override
	protected void updateMessage() {
		Component caption = Component.translatable("soundCategory." + this.source.getName());
		Component valueLabel = this.value == 0.0
			? CommonComponents.OPTION_OFF
			: Component.literal((int) Math.round(this.value * 100.0) + "%");
		String label = Component.translatable("options.generic_value", caption, valueLabel).getString();
		this.setMessage(Component.literal(label.toUpperCase(Locale.ROOT)));
	}

	@Override
	protected void applyValue() {
		if (!this.mouseInteraction) {
			this.saveValue();
		}
	}

	private void saveValue() {
		this.options.getSoundSourceOptionInstance(this.source).set(this.value);
		this.savedValue = this.value;
		Minecraft.getInstance().options.save();
	}
}
