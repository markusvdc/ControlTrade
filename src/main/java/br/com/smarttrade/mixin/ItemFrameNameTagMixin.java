package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrame.class)
public abstract class ItemFrameNameTagMixin {
	@Shadow
	@Final
	private static EntityDataAccessor<ItemStack> DATA_ITEM;

	@Inject(method = "interact", at = @At("HEAD"), cancellable = true)
	private void smarttrade$applyNameTag(
		Player player,
		InteractionHand hand,
		Vec3 location,
		CallbackInfoReturnable<InteractionResult> callback
	) {
		ItemFrame frame = (ItemFrame)(Object)this;
		ItemStack nameTag = player.getItemInHand(hand);
		Component customName = nameTag.get(DataComponents.CUSTOM_NAME);
		if (!SmartTradeConfig.sovereignSeal()
			|| !nameTag.is(Items.NAME_TAG)
			|| customName == null
			|| frame.getItem().isEmpty()) {
			return;
		}

		if (!player.level().isClientSide()) {
			ItemStack renamedItem = frame.getItem().copy();
			renamedItem.set(DataComponents.CUSTOM_NAME, customName);
			frame.getEntityData().set(DATA_ITEM, renamedItem);
			nameTag.consume(1, player);
		}

		callback.setReturnValue(InteractionResult.SUCCESS);
	}
}
