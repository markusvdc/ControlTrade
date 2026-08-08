package br.com.smarttrade;

import br.com.smarttrade.config.SmartTradeConfig;
import net.fabricmc.api.ModInitializer;

public final class SmartTrade implements ModInitializer {
	public static final String MOD_ID = "smarttrade";

	@Override
	public void onInitialize() {
		SmartTradeConfig.load();
	}
}
