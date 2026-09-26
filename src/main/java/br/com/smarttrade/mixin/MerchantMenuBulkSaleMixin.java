package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.BulkSaleMenu;
import br.com.smarttrade.gameplay.BulkSalePlan;
import br.com.smarttrade.gameplay.DawnStockOffer;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.MerchantContainer;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantMenu.class)
public abstract class MerchantMenuBulkSaleMixin extends AbstractContainerMenu implements BulkSaleMenu {
	@Shadow @Final private Merchant trader;
	@Shadow @Final private MerchantContainer tradeContainer;
	@Unique private DataSlot smarttrade$selling;
	@Unique private List<MerchantOffer> smarttrade$saleOffers = List.of();
	@Unique private int[] smarttrade$remainingUses = new int[0];
	@Unique private int smarttrade$offerIndex;

	protected MerchantMenuBulkSaleMixin(MenuType<?> type, int id) { super(type, id); }

	@Inject(method = "<init>(ILnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/trading/Merchant;)V", at = @At("TAIL"))
	private void smarttrade$addSaleState(int id, Inventory inventory, Merchant merchant, CallbackInfo ci) {
		this.smarttrade$selling = this.addDataSlot(DataSlot.standalone());
	}

	@Override
	public boolean clickMenuButton(Player player, int id) {
		if (id != BUTTON_ID) return super.clickMenuButton(player, id);
		if (!(player instanceof ServerPlayer serverPlayer) || !serverPlayer.level().getServer().isSingleplayer()
			|| !(this.trader instanceof Villager) || player.containerMenu != (Object)this || !this.stillValid(player)) return false;
		if (this.smarttrade$isSelling()) {
			this.smarttrade$selling.set(0);
			return true;
		}
		if (!SmartTradeConfig.bulkSelling() || !this.smarttrade$clearInputs()) return false;
		this.smarttrade$saleOffers = List.copyOf(this.trader.getOffers());
		this.smarttrade$remainingUses = this.smarttrade$saleOffers.stream().mapToInt(offer ->
			Math.max(0, ((DawnStockOffer)offer).smarttrade$getEffectiveStock() - offer.getUses())).toArray();
		this.smarttrade$offerIndex = 0;
		this.smarttrade$selling.set(1);
		return true;
	}

	@Override
	public boolean smarttrade$isSelling() { return this.smarttrade$selling != null && this.smarttrade$selling.get() != 0; }

	@Unique
	private boolean smarttrade$clearInputs() {
		return this.getCarried().isEmpty() && this.getSlot(0).getItem().isEmpty() && this.getSlot(1).getItem().isEmpty();
	}

	@Override
	public void smarttrade$tickSale(ServerPlayer player) {
		if (!this.smarttrade$isSelling()) return;
		if (!SmartTradeConfig.bulkSelling() || !this.stillValid(player) || !player.isAlive() || !this.smarttrade$clearInputs()) {
			this.smarttrade$selling.set(0);
			return;
		}
		MerchantMenu menu = (MerchantMenu)(Object)this;
		while (this.smarttrade$offerIndex < this.smarttrade$saleOffers.size()) {
			int index = this.smarttrade$offerIndex;
			MerchantOffer offer = this.smarttrade$saleOffers.get(index);
			int currentIndex = this.trader.getOffers().indexOf(offer);
			BulkSalePlan plan = currentIndex < 0 || this.smarttrade$remainingUses[index] <= 0 ? null
				: BulkSalePlan.create(this.slots.subList(3, 39).stream().map(slot -> slot.getItem()).toList(), offer);
			if (plan == null) { this.smarttrade$offerIndex++; continue; }
			// Reserve exact payments only. Every completed step leaves both payment slots empty.
			List<ItemStack> before = this.slots.subList(3, 39).stream().map(slot -> slot.getItem().copy()).toList();
			for (int i = 0; i < 36; i++) this.getSlot(i + 3).set(plan.remaining().get(i));
			menu.setSelectionHint(currentIndex);
			this.tradeContainer.setItem(0, plan.paymentA());
			this.tradeContainer.setItem(1, plan.paymentB());
			menu.updateSellItem();
			if (this.tradeContainer.getActiveOffer() != offer || this.getSlot(2).getItem().isEmpty()) {
				this.tradeContainer.setItem(0, ItemStack.EMPTY);
				this.tradeContainer.setItem(1, ItemStack.EMPTY);
				for (int i = 0; i < 36; i++) this.getSlot(i + 3).set(before.get(i));
				this.smarttrade$selling.set(0);
				this.broadcastChanges();
				return;
			}
			int previousUses = offer.getUses();
			menu.quickMoveStack(player, 2);
			this.smarttrade$remainingUses[index]--;
			if (offer.getUses() != previousUses + 1) this.smarttrade$selling.set(0);
			player.sendMerchantOffers(this.containerId, this.trader.getOffers(), menu.getTraderLevel(),
				this.trader.getVillagerXp(), menu.showProgressBar(), menu.canRestock());
			this.broadcastChanges();
			return;
		}
		this.smarttrade$selling.set(0);
		this.broadcastChanges();
	}
}
