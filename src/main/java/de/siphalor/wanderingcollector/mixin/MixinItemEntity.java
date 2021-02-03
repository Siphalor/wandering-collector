package de.siphalor.wanderingcollector.mixin;

import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow public abstract ItemStack getStack();

	@Shadow private UUID thrower;

	@Inject(
			method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V", ordinal = 1)
	)
	public void tickInject(CallbackInfo callbackInfo) {
		addStackToThrower();
	}

	@Inject(
			method = "damage",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;remove()V")
	)
	public void onDeathInject(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		addStackToThrower();
	}

	@Unique
	private void addStackToThrower() {
		if (thrower != null) {
			PlayerEntity player = world.getPlayerByUuid(thrower);
			if (player instanceof IServerPlayerEntity) {
				((IServerPlayerEntity) player).wandering_collector$addLostStack(getStack());
			}
		}
	}
}
