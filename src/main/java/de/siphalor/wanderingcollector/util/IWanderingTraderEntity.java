package de.siphalor.wanderingcollector.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.TradeOfferList;

public interface IWanderingTraderEntity {
	TradeOfferList wandering_collector$getOffers(PlayerEntity playerEntity);
}
