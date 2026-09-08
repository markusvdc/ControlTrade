package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.DawnStockOffer;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferDawnStockMixin implements DawnStockOffer {
	@Shadow @Final private int maxUses;
	@Shadow private int uses;
	@Unique private boolean smarttrade$doubleStock;

	@Override
	public void smarttrade$setDoubleStock(boolean doubled) {
		this.smarttrade$doubleStock = doubled;
	}

	@Override
	public int smarttrade$getEffectiveStock() {
		return this.smarttrade$doubleStock ? (int)Math.min(Integer.MAX_VALUE, (long)this.maxUses * 2) : this.maxUses;
	}

	@Override
	public void smarttrade$mergeUsedStock(int additionalUses) {
		this.uses = (int)Math.min(Integer.MAX_VALUE, (long)this.uses + Math.max(0, additionalUses));
	}

	// getMaxUses and the disk codec retain the original offer identity for
	// ControlTrade. Only stock checks and the network use the effective limit.
	@Redirect(method = {"isOutOfStock", "setToOutOfStock", "updateDemand"},
		at = @At(value = "FIELD", target = "Lnet/minecraft/world/item/trading/MerchantOffer;maxUses:I"))
	private int smarttrade$effectiveStock(MerchantOffer offer) {
		return this.smarttrade$getEffectiveStock();
	}

	@Redirect(method = "writeToStream", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/world/item/trading/MerchantOffer;getMaxUses()I"))
	private static int smarttrade$sendEffectiveStock(MerchantOffer offer) {
		return ((DawnStockOffer)offer).smarttrade$getEffectiveStock();
	}

	@Inject(method = "copy", at = @At("RETURN"))
	private void smarttrade$copyStockMode(CallbackInfoReturnable<MerchantOffer> callback) {
		((DawnStockOffer)callback.getReturnValue()).smarttrade$setDoubleStock(this.smarttrade$doubleStock);
	}
}
