package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.timeline.Timelines;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerDawnRestockMixin {
	@Unique private long smarttrade$stockDay = Long.MIN_VALUE;
	@Unique private boolean smarttrade$restockingAtDawn;
	@Shadow private int numberOfRestocksToday;

	@Unique
	private boolean smarttrade$enabled() {
		Villager villager = (Villager)(Object)this;
		return SmartTradeConfig.dawnRestock()
			&& villager.level() instanceof ServerLevel level && level.getServer().isSingleplayer();
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void smarttrade$checkDawn(CallbackInfo callback) {
		Villager villager = (Villager)(Object)this;
		if (!(villager.level() instanceof ServerLevel level)) {
			return;
		}
		long day = level.registryAccess().get(Timelines.OVERWORLD_DAY)
			.map(timeline -> (long)timeline.value().getPeriodCount(level.clockManager()))
			.orElse(Long.MIN_VALUE);
		if (day == Long.MIN_VALUE) {
			return;
		}
		if (!smarttrade$enabled() || this.smarttrade$stockDay == Long.MIN_VALUE || day < this.smarttrade$stockDay) {
			// Initial activation and clock rewinds must not grant extra stock.
			this.smarttrade$stockDay = day;
			return;
		}
		if (day == this.smarttrade$stockDay) {
			return;
		}
		// One refill even after several unloaded days; no accumulated refills.
		this.smarttrade$stockDay = day;
		this.numberOfRestocksToday = 0;
		this.smarttrade$restockingAtDawn = true;
		try {
			// Keep vanilla demand updates and synchronize an open trading screen.
			villager.restock();
		} finally {
			this.smarttrade$restockingAtDawn = false;
		}
	}

	@Inject(method = "shouldRestock", at = @At("HEAD"), cancellable = true)
	private void smarttrade$skipWorkRestocks(ServerLevel level, CallbackInfoReturnable<Boolean> callback) {
		if (smarttrade$enabled()) {
			// Also bypass vanilla catch-up, which resets offer uses on its own.
			callback.setReturnValue(false);
		}
	}

	@Inject(method = "restock", at = @At("HEAD"), cancellable = true)
	private void smarttrade$onlyRestockAtDawn(CallbackInfo callback) {
		if (smarttrade$enabled() && !this.smarttrade$restockingAtDawn) {
			callback.cancel();
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void smarttrade$saveStockDay(ValueOutput output, CallbackInfo callback) {
		if (this.smarttrade$stockDay != Long.MIN_VALUE) {
			output.putLong("smarttrade:stock_day", this.smarttrade$stockDay);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void smarttrade$loadStockDay(ValueInput input, CallbackInfo callback) {
		this.smarttrade$stockDay = input.getLongOr("smarttrade:stock_day", input.getLongOr("capfood:stock_day", Long.MIN_VALUE));
	}
}
