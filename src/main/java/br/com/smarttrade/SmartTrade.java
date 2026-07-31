package br.com.smarttrade;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.AutomaticDoorCloser;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class SmartTrade implements ModInitializer {
	public static final String MOD_ID = "smarttrade";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SmartTradeConfig.load();
		AutomaticDoorCloser.register();
	}
}
