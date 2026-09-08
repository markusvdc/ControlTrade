package br.com.smarttrade.gameplay;

public interface DawnStockOffer {
	void smarttrade$setDoubleStock(boolean doubled);
	int smarttrade$getEffectiveStock();
	void smarttrade$mergeUsedStock(int additionalUses);
}
