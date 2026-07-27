package br.com.smarttrade.client.integration.jade;

import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.integration.jade.VillagerReputationProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public final class VillagerReputationComponentProvider implements IEntityComponentProvider {
	public static final VillagerReputationComponentProvider INSTANCE =
		new VillagerReputationComponentProvider();

	private VillagerReputationComponentProvider() {
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!SmartTradeConfig.showAdditionalInformation()) {
			return;
		}

		CompoundTag serverData = accessor.getServerData();
		if (!serverData.contains(VillagerReputationProvider.REPUTATION_KEY)) {
			return;
		}

		int reputation = serverData.getIntOr(VillagerReputationProvider.REPUTATION_KEY, 0);
		ChatFormatting color = reputation > 0
			? ChatFormatting.GREEN
			: reputation < 0 ? ChatFormatting.RED : ChatFormatting.GRAY;
		Component value = Component.literal(reputation > 0 ? "+" + reputation : Integer.toString(reputation))
			.withStyle(color);
		tooltip.add(Component.translatable("smarttrade.jade.reputation", value), getUid());
		int restocks = serverData.getIntOr(VillagerReputationProvider.RESTOCKS_KEY, 0);
		tooltip.add(Component.translatable("smarttrade.jade.restocks", restocks, 2));
		boolean cured = serverData.getBooleanOr(VillagerReputationProvider.CURED_KEY, false);
		tooltip.add(Component.translatable(
			"smarttrade.jade.cured",
			Component.translatable(cured ? "smarttrade.value.yes" : "smarttrade.value.no")
		));
	}

	@Override
	public Identifier getUid() {
		return VillagerReputationProvider.UID;
	}
}
