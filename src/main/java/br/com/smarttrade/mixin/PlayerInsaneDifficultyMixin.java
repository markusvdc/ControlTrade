package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Player.class)
public abstract class PlayerInsaneDifficultyMixin {
	@ModifyConstant(method = "hurtServer", constant = @Constant(floatValue = 3.0F))
	private float smarttrade$applyInsaneDamage(float hardMultiplier) {
		Player player = (Player)(Object)this;
		return InsaneDifficulty.isActive(player.level())
			? 4.0F
			: hardMultiplier;
	}
}
