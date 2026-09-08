package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.SmartTradeOffers;
import br.com.smarttrade.gameplay.DawnStockOffer;
import br.com.smarttrade.gameplay.DawnStockRepair;
import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {
	@Unique
	private int smarttrade$getOffersDepth;

	@Inject(method = "getOffers", at = @At("HEAD"))
	private void smarttrade$beginOfferQuery(CallbackInfoReturnable<MerchantOffers> callback) {
		this.smarttrade$getOffersDepth++;
	}

	@Inject(method = "getOffers", at = @At("RETURN"))
	private void smarttrade$finishOfferQuery(CallbackInfoReturnable<MerchantOffers> callback) {
		this.smarttrade$getOffersDepth--;
		if (this.smarttrade$getOffersDepth == 0 && (Object) this instanceof Villager villager) {
			SmartTradeOffers.ensurePresent(villager, callback.getReturnValue());
			boolean singleplayer = villager.level() instanceof ServerLevel level
				&& level.getServer().isSingleplayer();
			if (singleplayer) {
				DawnStockRepair.removeDuplicateControlTradeOffers(callback.getReturnValue());
			}
			for (MerchantOffer offer : callback.getReturnValue()) {
				((DawnStockOffer)offer).smarttrade$setDoubleStock(singleplayer && SmartTradeConfig.dawnRestock());
			}
		}
	}
}
