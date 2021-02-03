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
			cir.setReturnValue(((IWanderingTraderEntity) merchant).wandering_collector$getOffers(merchant.getCurrentCustomer()));
		}
	}
}
