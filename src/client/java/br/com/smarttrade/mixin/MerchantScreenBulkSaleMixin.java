package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.BulkSaleMenu;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenBulkSaleMixin extends AbstractContainerScreen<MerchantMenu> {
	@Unique private Button smarttrade$sellButton;
	protected MerchantScreenBulkSaleMixin(MerchantMenu menu, Inventory inventory, Component title) { super(menu, inventory, title); }

	@Inject(method = "init", at = @At("TAIL"))
	private void smarttrade$addSellButton(CallbackInfo ci) {
		if (!SmartTradeConfig.bulkSelling() || !this.minecraft.hasSingleplayerServer()) return;
		this.smarttrade$sellButton = this.addRenderableWidget(Button.builder(Component.translatable("smarttrade.sale.sell_all"), button -> {
			this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, BulkSaleMenu.BUTTON_ID);
		}).bounds(this.leftPos + 108, this.topPos + this.imageHeight + 4, 140, 20)
			.tooltip(Tooltip.create(Component.translatable("smarttrade.sale.hint"))).build());
	}

	@Override
	protected void containerTick() {
		super.containerTick();
		if (this.smarttrade$sellButton == null) return;
		boolean running = ((BulkSaleMenu)this.menu).smarttrade$isSelling();
		this.smarttrade$sellButton.visible = SmartTradeConfig.bulkSelling() && this.menu.showProgressBar();
		this.smarttrade$sellButton.setMessage(Component.translatable(running ? "smarttrade.sale.stop" : "smarttrade.sale.sell_all"));
		this.smarttrade$sellButton.active = running || (this.menu.getCarried().isEmpty()
			&& this.menu.getSlot(0).getItem().isEmpty() && this.menu.getSlot(1).getItem().isEmpty());
	}
}
