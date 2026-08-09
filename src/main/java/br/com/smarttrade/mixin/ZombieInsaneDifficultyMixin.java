package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.world.entity.monster.zombie.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(Zombie.class)
public abstract class ZombieInsaneDifficultyMixin {
	@ModifyConstant(
		method = "populateDefaultEquipmentSlots",
		constant = @Constant(floatValue = 0.05F)
	)
	private float smarttrade$increaseIronWeaponChance(float chance) {
		Zombie zombie = (Zombie)(Object)this;
		return InsaneDifficulty.isActive(zombie.level()) ? 0.30F : chance;
	}
}
