package de.siphalor.wanderingcollector.mixin;

import de.siphalor.wanderingcollector.util.IMerchantInventory;
import de.siphalor.wanderingcollector.util.IWanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.village.Merchant;
import net.minecraft.village.MerchantInventory;
import net.minecraft.village.TradeOfferList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(MerchantInventory.class)
public class MixinMerchantInventory implements IMerchantInventory {
	@Unique
	private PlayerEntity player;

	@Override
	public void wandering_collector$setPlayer(PlayerEntity player) {
		this.player = player;
	}

	@Redirect(method = "updateRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/Merchant;getOffers()Lnet/minecraft/village/TradeOfferList;"))
	public TradeOfferList getOffersRedirect(Merchant merchant) {
		if (merchant instanceof IWanderingTraderEntity) {
			return ((IWanderingTraderEntity) merchant).wandering_collector$getOffers(player);
		}
		return merchant.getOffers();
	}
}
