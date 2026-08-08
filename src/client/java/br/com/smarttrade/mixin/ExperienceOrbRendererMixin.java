package br.com.smarttrade.mixin;

import br.com.smarttrade.client.gameplay.ExperienceOrbColorState;
import br.com.smarttrade.config.SmartTradeConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.entity.ExperienceOrbRenderer;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import net.minecraft.world.entity.ExperienceOrb;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbRenderer.class)
public abstract class ExperienceOrbRendererMixin {
	@Unique
	private static final int[] SMARTTRADE$COLORS = {
		0xFFBF0B,
		0xFB5607,
		0xFF006E,
		0x3A86FF
	};

	@Inject(method = "extractRenderState", at = @At("TAIL"))
	private void smarttrade$selectStableColor(
		ExperienceOrb entity,
		ExperienceOrbRenderState state,
		float partialTicks,
		CallbackInfo callback
	) {
		((ExperienceOrbColorState) state).smarttrade$setColor(
			SMARTTRADE$COLORS[Math.floorMod(entity.getUUID().hashCode(), SMARTTRADE$COLORS.length)]
		);
	}

	@ModifyArgs(
		method = "lambda$submit$0",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/ExperienceOrbRenderer;vertex(Lcom/mojang/blaze3d/vertex/VertexConsumer;Lcom/mojang/blaze3d/vertex/PoseStack$Pose;FFIIIFFI)V"
		)
	)
	private static void smarttrade$replaceVertexColor(
		Args args,
		int vanillaRed,
		int vanillaBlue,
		float u0,
		float v1,
		ExperienceOrbRenderState state,
		float u1,
		float v0,
		PoseStack.Pose pose,
		VertexConsumer buffer
	) {
		if (SmartTradeConfig.randomExperienceOrbColors()) {
			int color = ((ExperienceOrbColorState) state).smarttrade$getColor();
			args.set(4, color >> 16 & 0xFF);
			args.set(5, color >> 8 & 0xFF);
			args.set(6, color & 0xFF);
		}
	}

	@ModifyConstant(method = "vertex", constant = @Constant(intValue = 128))
	private static int smarttrade$useOpaquePaletteColor(int vanillaAlpha) {
		return SmartTradeConfig.randomExperienceOrbColors() ? 180 : vanillaAlpha;
	}
}
