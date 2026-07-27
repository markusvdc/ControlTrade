package br.com.smarttrade.client;

import br.com.smarttrade.client.screen.SmartTradeScreen;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public final class SmartTradeModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return SmartTradeScreen::new;
	}
}
