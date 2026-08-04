package br.com.smarttrade.mixin;

import br.com.smarttrade.client.gameplay.CompactItemCountRenderer;
import br.com.smarttrade.config.SmartTradeConfig;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsExtractorMixin {
	@WrapOperation(
		method = "itemCount",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V"
		)
	)
	private void smarttrade$renderCompactItemCount(
		GuiGraphicsExtractor graphics,
		Font font,
		String text,
		int x,
		int y,
		int color,
		boolean dropShadow,
		Operation<Void> original,
		@Local(argsOnly = true, ordinal = 0) int itemX
	) {
		if (!SmartTradeConfig.compactItemCounts()) {
			original.call(graphics, font, text, x, y, color, dropShadow);
			return;
		}
		CompactItemCountRenderer.draw(graphics, font, text, y, color, itemX);
	}
}
