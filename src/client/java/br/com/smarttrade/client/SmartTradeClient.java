package br.com.smarttrade.client;

import br.com.smarttrade.client.gameplay.AutomaticDoorCloser;
import br.com.smarttrade.client.gameplay.CompactInformationOverlay;
import net.fabricmc.api.ClientModInitializer;

public final class SmartTradeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		AutomaticDoorCloser.register();
		CompactInformationOverlay.register();
	}
}
