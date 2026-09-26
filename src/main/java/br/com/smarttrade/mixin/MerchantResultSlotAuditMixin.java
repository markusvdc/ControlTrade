package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.BulkSaleMenu;
import br.com.smarttrade.gameplay.DawnStockOffer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Records completed server-side trades once, including manual and bulk sales. */
@Mixin(MerchantResultSlot.class)
public abstract class MerchantResultSlotAuditMixin {
	@Unique private static final Logger SMARTTRADE_AUDIT = LoggerFactory.getLogger("ControlTrade.TradeAudit");
	@Shadow @Final private MerchantContainer slots;
	@Shadow @Final private Merchant merchant;
	@Unique private MerchantOffer smarttrade$offer;
	@Unique private ItemStack smarttrade$inputA = ItemStack.EMPTY;
	@Unique private ItemStack smarttrade$inputB = ItemStack.EMPTY;
	@Unique private ItemStack smarttrade$result = ItemStack.EMPTY;
	@Unique private int smarttrade$usesBefore;
	@Unique private int smarttrade$priceA;
	@Unique private int smarttrade$priceB;
	@Unique private int smarttrade$demand;
	@Unique private int smarttrade$specialPrice;

	@Inject(method = "onTake", at = @At("HEAD"))
	private void smarttrade$captureTrade(Player player, ItemStack stack, CallbackInfo ci) {
		this.smarttrade$offer = null;
		if (!(player instanceof ServerPlayer)) return;
		MerchantOffer offer = this.slots.getActiveOffer();
		if (offer == null) return;
		this.smarttrade$offer = offer;
		this.smarttrade$inputA = this.slots.getItem(0).copy();
		this.smarttrade$inputB = this.slots.getItem(1).copy();
		this.smarttrade$result = offer.getResult().copy();
		this.smarttrade$usesBefore = offer.getUses();
		this.smarttrade$priceA = offer.getCostA().getCount();
		this.smarttrade$priceB = offer.getCostB().getCount();
		this.smarttrade$demand = offer.getDemand();
		this.smarttrade$specialPrice = offer.getSpecialPriceDiff();
	}

	@Inject(method = "onTake", at = @At("RETURN"))
	private void smarttrade$recordTrade(Player player, ItemStack stack, CallbackInfo ci) {
		MerchantOffer offer = this.smarttrade$offer;
		this.smarttrade$offer = null;
		if (!(player instanceof ServerPlayer serverPlayer) || offer == null || offer.getUses() <= this.smarttrade$usesBefore) return;
		int consumedA = this.smarttrade$inputA.getCount() - this.slots.getItem(0).getCount();
		int consumedB = this.smarttrade$inputB.getCount() - this.slots.getItem(1).getCount();
		String mode = player.containerMenu instanceof BulkSaleMenu sale && sale.smarttrade$isSelling() ? "SELL_ALL" : "MANUAL";
		String trader = this.merchant instanceof Entity entity
			? BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()) + "/" + entity.getUUID()
			: this.merchant.getClass().getName();
		int limit = offer instanceof DawnStockOffer stock ? stock.smarttrade$getEffectiveStock() : offer.getMaxUses();
		SMARTTRADE_AUDIT.info(
			"[TRADE] mode={} player={} trader={} dimension={} tick={} offer={} paidA={} paidB={} received={} priceA={} priceB={} uses={}->{} baseLimit={} effectiveLimit={} exhausted={} demand={} specialPrice={} paymentsBefore=[{},{}] paymentsAfter=[{},{}]",
			mode, player.getUUID(), trader, serverPlayer.level().dimension().identifier(), serverPlayer.level().getGameTime(),
			this.merchant.getOffers().indexOf(offer), smarttrade$item(this.smarttrade$inputA, consumedA),
			smarttrade$item(this.smarttrade$inputB, consumedB), smarttrade$item(this.smarttrade$result, this.smarttrade$result.getCount()),
			this.smarttrade$priceA, this.smarttrade$priceB, this.smarttrade$usesBefore, offer.getUses(), offer.getMaxUses(), limit,
			offer.isOutOfStock(), this.smarttrade$demand, this.smarttrade$specialPrice,
			this.smarttrade$inputA.getCount(), this.smarttrade$inputB.getCount(), this.slots.getItem(0).getCount(), this.slots.getItem(1).getCount()
		);
	}

	@Unique
	private static String smarttrade$item(ItemStack stack, int count) {
		return stack.isEmpty() || count == 0 ? "none" : BuiltInRegistries.ITEM.getKey(stack.getItem()) + "x" + count;
	}
}
