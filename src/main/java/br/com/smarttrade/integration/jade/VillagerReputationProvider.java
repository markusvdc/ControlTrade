package br.com.smarttrade.integration.jade;

import br.com.smarttrade.SmartTrade;
import br.com.smarttrade.config.SmartTradeConfig;
import br.com.smarttrade.gameplay.VillagerRestockAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IServerDataProvider;

public final class VillagerReputationProvider implements IServerDataProvider<EntityAccessor> {
	public static final VillagerReputationProvider INSTANCE = new VillagerReputationProvider();
	public static final Identifier UID =
		Identifier.fromNamespaceAndPath(SmartTrade.MOD_ID, "villager_reputation");
	public static final String REPUTATION_KEY = "SmartTradeReputation";
	public static final String RESTOCKS_KEY = "SmartTradeRestocksToday";
	public static final String CURED_KEY = "SmartTradeCuredByPlayer";

	private VillagerReputationProvider() {
	}

	@Override
	public void appendServerData(CompoundTag data, EntityAccessor accessor) {
		if (SmartTradeConfig.showAdditionalInformation()
			&& accessor.getEntity() instanceof Villager villager) {
			data.putInt(REPUTATION_KEY, villager.getPlayerReputation(accessor.getPlayer()));
			data.putInt(RESTOCKS_KEY, ((VillagerRestockAccess) villager).smarttrade$getRestocksToday());
			data.putBoolean(
				CURED_KEY,
				villager.getGossips().getReputation(
					accessor.getPlayer().getUUID(),
					type -> type == GossipType.MAJOR_POSITIVE
				) > 0
			);
		}
	}

	@Override
	public boolean shouldRequestData(EntityAccessor accessor) {
		return SmartTradeConfig.showAdditionalInformation()
			&& accessor.getEntity() instanceof Villager;
	}

	@Override
	public Identifier getUid() {
		return UID;
	}
}
