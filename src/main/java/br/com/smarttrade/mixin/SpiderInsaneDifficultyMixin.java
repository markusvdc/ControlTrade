package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.world.entity.monster.spider.Spider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Spider.class)
public abstract class SpiderInsaneDifficultyMixin {
	@ModifyConstant(method = "finalizeSpawn", constant = @Constant(floatValue = 0.1F))
	private float smarttrade$increaseEffectChance(float chance) {
		Spider spider = (Spider)(Object)this;
		return InsaneDifficulty.isActive(spider.level()) ? 0.30F : chance;
	}
}
