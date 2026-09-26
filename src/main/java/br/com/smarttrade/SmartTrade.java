package br.com.smarttrade;

import br.com.smarttrade.config.SmartTradeConfig;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import br.com.smarttrade.gameplay.BulkSaleMenu;

public final class SmartTrade implements ModInitializer {
	public static final String MOD_ID = "smarttrade";

	@Override
	public void onInitialize() {
		SmartTradeConfig.load();
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			if (!server.isSingleplayer()) return;
			for (var player : server.getPlayerList().getPlayers()) {
				if (player.containerMenu instanceof BulkSaleMenu sale) sale.smarttrade$tickSale(player);
			}
		});
	}
}
