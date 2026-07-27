package br.com.smarttrade.client.integration.jade;

import br.com.smarttrade.integration.jade.VillagerReputationProvider;
import net.minecraft.world.entity.npc.villager.Villager;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class SmartTradeJadePlugin implements IWailaPlugin {
	@Override
	public void register(IWailaCommonRegistration registration) {
		registration.registerEntityDataProvider(VillagerReputationProvider.INSTANCE, Villager.class);
	}

	@Override
	public void registerClient(IWailaClientRegistration registration) {
		registration.registerEntityComponent(VillagerReputationComponentProvider.INSTANCE, Villager.class);
	}
}
