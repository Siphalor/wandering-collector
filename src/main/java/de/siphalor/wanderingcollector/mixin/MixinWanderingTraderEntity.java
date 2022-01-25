/*
 * Copyright 2021-2022 Siphalor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied.
 * See the License for the specific language governing
 * permissions and limitations under the License.
 */

package de.siphalor.wanderingcollector.mixin;

import de.siphalor.wanderingcollector.*;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import de.siphalor.wanderingcollector.util.IWanderingTraderEntity;
import de.siphalor.wanderingcollector.util.Utils;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.*;

@Mixin(WanderingTraderEntity.class)
public abstract class MixinWanderingTraderEntity extends MerchantEntity implements Merchant, IWanderingTraderEntity {
	@Unique
	private final Map<UUID, Collection<TradeOffer>> playerSpecificTrades = new HashMap<>();

	public MixinWanderingTraderEntity(EntityType<? extends MerchantEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public TradeOfferList wandering_collector$getOffers(PlayerEntity playerEntity) {
		TradeOfferList tradeOfferList = new TradeOfferList();
		tradeOfferList.addAll(getOffers());
		Collection<TradeOffer> offers = playerSpecificTrades.get(playerEntity.getUuid());
		if (offers == null) {
			ArrayList<NbtCompound> stackCompounds = ((IServerPlayerEntity) playerEntity).wandering_collector$getLostStackCompounds();
			if (stackCompounds.isEmpty() || WCConfig.buyBackTrades <= 0) {
				offers = Collections.emptyList();
			} else {
				offers = new ArrayList<>(WCConfig.buyBackTrades);
				for (int j = 0; j < Math.min(WCConfig.buyBackTrades, stackCompounds.size()); j++) {
					ItemStack stack = ItemStack.fromNbt(stackCompounds.remove(j));
					offers.add(new TradeOffer(WCConfig.defaultPrices.getPriceStack(stack), stack, 1, 0, 1F));
				}
			}
			playerSpecificTrades.put(playerEntity.getUuid(), offers);
		}
		tradeOfferList.addAll(offers);
		return tradeOfferList;
	}

	@Override
	public void sendOffers(PlayerEntity playerEntity, Text text, int i) {
		OptionalInt optionalInt = playerEntity.openHandledScreen(new SimpleNamedScreenHandlerFactory((ix, playerInventory, playerEntityx) ->
				new MerchantScreenHandler(ix, playerInventory, this), text)
		);
		if (optionalInt.isPresent()) {
			TradeOfferList tradeOfferList = wandering_collector$getOffers(playerEntity);
			if (!tradeOfferList.isEmpty()) {
				playerEntity.sendTradeOffers(optionalInt.getAsInt(), tradeOfferList, i, this.getExperience(), this.isLeveledMerchant(), this.canRefreshTrades());
			}
		}
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void readCustomDataFromTagInject(NbtCompound tag, CallbackInfo callbackInfo) {
		playerSpecificTrades.clear();
		if (tag.contains(WanderingCollector.PLAYER_SPECIFIC_TRADES, 10)) {
			NbtCompound compound = tag.getCompound(WanderingCollector.PLAYER_SPECIFIC_TRADES);
			for (String key : compound.getKeys()) {
				if (compound.contains(key, 9)) {
					NbtList list = compound.getList(key, 10);
					ArrayList<TradeOffer> offers = new ArrayList<>(list.size());
					for (NbtElement tradeTag : list) {
						offers.add(new TradeOffer((NbtCompound) tradeTag));
					}
					playerSpecificTrades.put(UUID.fromString(key), offers);
				}
			}
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void writeCustomDataToTag(NbtCompound tag, CallbackInfo callbackInfo) {
		NbtCompound compound = new NbtCompound();
		for (Map.Entry<UUID, Collection<TradeOffer>> entry : playerSpecificTrades.entrySet()) {
			if (entry.getValue().isEmpty()) {
				continue;
			}
			NbtList list = new NbtList();
			for (TradeOffer tradeOffer : entry.getValue()) {
				list.add(tradeOffer.toNbt());
			}
			compound.put(entry.getKey().toString(), list);
		}
		tag.put(WanderingCollector.PLAYER_SPECIFIC_TRADES, compound);
	}
}
