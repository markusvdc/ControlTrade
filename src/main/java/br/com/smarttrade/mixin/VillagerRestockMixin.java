package br.com.smarttrade.mixin;

import br.com.smarttrade.SmartTrade;
import br.com.smarttrade.gameplay.SmartTradeOffers;
import br.com.smarttrade.gameplay.VillagerRestockAccess;
import java.util.Map;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public abstract class VillagerRestockMixin implements VillagerRestockAccess {
	@Shadow
	private int numberOfRestocksToday;

	@Unique
	private Map<String, Integer> smarttrade$usesBeforeRestock = Map.of();

	@Override
	public int smarttrade$getRestocksToday() {
		return this.numberOfRestocksToday;
	}

	@Inject(method = "restock", at = @At("HEAD"))
	private void smarttrade$captureEggUses(CallbackInfo callback) {
		Villager villager = (Villager) (Object) this;
		this.smarttrade$usesBeforeRestock = SmartTradeOffers.snapshotUses(villager.getOffers());
	}

	@Inject(method = "restock", at = @At("TAIL"))
	private void smarttrade$logEggRestock(CallbackInfo callback) {
		Villager villager = (Villager) (Object) this;
		SmartTradeOffers.logRestocks(villager, this.smarttrade$usesBeforeRestock, villager.getOffers());
		SmartTrade.LOGGER.info(
			"Villager restock counter: villager={}, restocksToday={}/2",
			villager.getUUID(),
			this.numberOfRestocksToday
		);
		this.smarttrade$usesBeforeRestock = Map.of();
	}
}
