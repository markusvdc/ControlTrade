package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.InsaneDifficulty;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.skeleton.AbstractSkeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ServerLevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobInsaneDifficultyMixin {
	@Inject(method = "enchantSpawnedWeapon", at = @At("HEAD"), cancellable = true)
	private void smarttrade$useFlameOnSkeletonBow(
		ServerLevelAccessor level,
		RandomSource random,
		DifficultyInstance difficulty,
		CallbackInfo callback
	) {
		Mob mob = (Mob)(Object)this;
		if (mob instanceof AbstractSkeleton && mob.getType() == EntityTypes.SKELETON && InsaneDifficulty.isActive(mob.level())) {
			ItemStack bow = mob.getItemBySlot(EquipmentSlot.MAINHAND);
			bow.enchant(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FLAME), 1);
			mob.setItemSlot(EquipmentSlot.MAINHAND, bow);
			callback.cancel();
		}
	}
}
