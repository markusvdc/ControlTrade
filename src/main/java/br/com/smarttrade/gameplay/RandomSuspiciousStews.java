package br.com.smarttrade.gameplay;

import java.util.List;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.SuspiciousStewEffects;

public final class RandomSuspiciousStews {
	private static final List<SuspiciousStewEffects.Entry> EFFECTS = List.of(
		new SuspiciousStewEffects.Entry(MobEffects.SATURATION, 7),
		new SuspiciousStewEffects.Entry(MobEffects.NIGHT_VISION, 100),
		new SuspiciousStewEffects.Entry(MobEffects.FIRE_RESISTANCE, 60),
		new SuspiciousStewEffects.Entry(MobEffects.BLINDNESS, 220),
		new SuspiciousStewEffects.Entry(MobEffects.WEAKNESS, 140),
		new SuspiciousStewEffects.Entry(MobEffects.REGENERATION, 140),
		new SuspiciousStewEffects.Entry(MobEffects.JUMP_BOOST, 100),
		new SuspiciousStewEffects.Entry(MobEffects.WITHER, 140),
		new SuspiciousStewEffects.Entry(MobEffects.POISON, 220),
		new SuspiciousStewEffects.Entry(MobEffects.NAUSEA, 140)
	);

	private RandomSuspiciousStews() {
	}

	public static void chooseEffect(ItemStack stack, RandomSource random) {
		SuspiciousStewEffects.Entry effect = EFFECTS.get(random.nextInt(EFFECTS.size()));
		stack.set(DataComponents.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffects(List.of(effect)));
	}
}
