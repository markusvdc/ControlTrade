package br.com.smarttrade.mixin;

import br.com.smarttrade.gameplay.VillagerRestockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import net.minecraft.world.entity.npc.villager.Villager;

@Mixin(Villager.class)
public abstract class VillagerRestockMixin implements VillagerRestockAccess {
	@Shadow
	private int numberOfRestocksToday;

	@Override
	public int smarttrade$getRestocksToday() {
		return this.numberOfRestocksToday;
	}
}
