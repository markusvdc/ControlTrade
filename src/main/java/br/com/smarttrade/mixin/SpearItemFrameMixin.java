package br.com.smarttrade.mixin;

import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class SpearItemFrameMixin {
	@Inject(method = "use", at = @At("HEAD"), cancellable = true)
	private void smarttrade$placeSpearInItemFrame(
		Level level,
		Player player,
		InteractionHand hand,
		CallbackInfoReturnable<InteractionResult> callback
	) {
		ItemStack spear = player.getItemInHand(hand);
		if (!SmartTradeConfig.sovereignSeal() || !player.isShiftKeyDown() || !spear.is(ItemTags.SPEARS)) {
			return;
		}

		HitResult hitResult = ProjectileUtil.getHitResultOnViewVector(
			player,
			entity -> entity instanceof ItemFrame,
			player.entityInteractionRange()
		);
		if (!(hitResult instanceof EntityHitResult entityHit)
			|| !(entityHit.getEntity() instanceof ItemFrame frame)
			|| !frame.getItem().isEmpty()) {
			return;
		}

		if (!level.isClientSide() && !frame.isRemoved()) {
			frame.setItem(spear);
			frame.gameEvent(GameEvent.BLOCK_CHANGE, player);
			spear.consume(1, player);
		}

		callback.setReturnValue(InteractionResult.SUCCESS);
	}
}
