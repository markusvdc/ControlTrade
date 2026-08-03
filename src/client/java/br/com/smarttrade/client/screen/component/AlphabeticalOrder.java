package br.com.smarttrade.client.screen.component;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public final class AlphabeticalOrder {
	private AlphabeticalOrder() {
	}

	public static Comparator<Component> components(Minecraft minecraft) {
		String languageCode = minecraft.getLanguageManager().getSelected();
		Collator collator = Collator.getInstance(Locale.forLanguageTag(languageCode.replace('_', '-')));
		collator.setStrength(Collator.PRIMARY);
		return (first, second) -> collator.compare(first.getString(), second.getString());
	}
}
