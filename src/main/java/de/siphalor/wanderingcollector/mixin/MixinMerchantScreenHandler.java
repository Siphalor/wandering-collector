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

import de.siphalor.wanderingcollector.util.IMerchantInventory;
import de.siphalor.wanderingcollector.util.IWanderingTraderEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOfferList;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MerchantScreenHandler.class)
public abstract class MixinMerchantScreenHandler extends ScreenHandler {
	@Shadow @Final private MerchantInventory merchantInventory;

	@Shadow @Final private Merchant merchant;

	protected MixinMerchantScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId) {
		super(type, syncId);
	}

	@Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/village/Merchant;)V", at = @At("RETURN"))
	public void onInit(int syncId, PlayerInventory playerInventory, Merchant merchant, CallbackInfo callbackInfo) {
		((IMerchantInventory) merchantInventory).wandering_collector$setPlayer(playerInventory.player);
	}

	@Inject(method = "getRecipes", at = @At("HEAD"), cancellable = true)
	public void getOffersInject(CallbackInfoReturnable<TradeOfferList> cir) {
		if (merchant instanceof IWanderingTraderEntity) {
			cir.setReturnValue(((IWanderingTraderEntity) merchant).wandering_collector$getOffers(merchant.getCustomer()));
		}
	}
}
