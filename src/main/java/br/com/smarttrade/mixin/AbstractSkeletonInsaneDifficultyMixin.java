package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(AbstractSkeleton.class)
public abstract class AbstractSkeletonInsaneDifficultyMixin {
	@ModifyVariable(method = "reassessWeaponGoal", at = @At(value = "STORE", ordinal = 0), name = "minAttackInterval")
	private int smarttrade$shortenBowInterval(int interval) {
		AbstractSkeleton skeleton = (AbstractSkeleton)(Object)this;
		return skeleton.getType() == EntityTypes.SKELETON && InsaneDifficulty.isActive(skeleton.level()) ? 10 : interval;
	}
}
