package br.com.smarttrade.mixin;

import br.com.smarttrade.client.gameplay.InsaneDifficultyClient;
import br.com.smarttrade.config.SmartTradeConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class EntityRendererInsaneDifficultyMixin {
	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void smarttrade$hideGroundedArrowFire(
		Entity entity,
		EntityRenderState state,
		float partialTicks,
		CallbackInfo callback
	) {
		if (
			state.displayFireAnimation
				&& entity instanceof AbstractArrow
				&& ((AbstractArrowAccessor)entity).smarttrade$isInGround()
				&& SmartTradeConfig.insaneDifficulty()
				&& InsaneDifficultyClient.isSelected(Minecraft.getInstance())
		) {
			state.displayFireAnimation = false;
		}
	}
}
