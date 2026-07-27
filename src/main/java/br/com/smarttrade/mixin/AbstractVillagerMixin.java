package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.SmartTradeOffers;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
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
		}
	}

	@Inject(method = "notifyTrade", at = @At("TAIL"))
	private void smarttrade$logEggTrade(MerchantOffer offer, CallbackInfo callback) {
		if ((Object) this instanceof Villager villager) {
			SmartTradeOffers.logTrade(villager, offer);
		}
	}
}
