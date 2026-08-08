package br.com.smarttrade.mixin;

import br.com.smarttrade.client.gameplay.ExperienceOrbColorState;
import net.minecraft.client.renderer.entity.state.ExperienceOrbRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ExperienceOrbRenderState.class)
public abstract class ExperienceOrbRenderStateMixin implements ExperienceOrbColorState {
	@Unique
	private int smarttrade$color;

	@Override
	public int smarttrade$getColor() {
		return this.smarttrade$color;
	}

	@Override
	public void smarttrade$setColor(int color) {
		this.smarttrade$color = color;
	}
}
