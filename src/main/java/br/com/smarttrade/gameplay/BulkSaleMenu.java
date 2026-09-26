package br.com.smarttrade.gameplay;

import net.minecraft.server.level.ServerPlayer;

public interface BulkSaleMenu {
	int BUTTON_ID = 73;
	boolean smarttrade$isSelling();
	void smarttrade$tickSale(ServerPlayer player);
}
