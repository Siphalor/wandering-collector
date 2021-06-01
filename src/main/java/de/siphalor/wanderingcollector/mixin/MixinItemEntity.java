/*
 * Copyright 2021 Siphalor
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

import de.siphalor.wanderingcollector.WanderingCollector;
import de.siphalor.wanderingcollector.util.IItemEntity;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
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
public abstract class MixinItemEntity extends Entity implements IItemEntity {
	@Unique
	private static final String formerOwnerKey = WanderingCollector.MOD_ID + ":FormerOwner";

	@Unique
	private UUID formerOwner;

	public MixinItemEntity(EntityType<?> type, World world) {
		super(type, world);
	}

	@Shadow public abstract ItemStack getStack();

	@Shadow private UUID thrower;

	@Override
	public UUID wanderingCollector$getFormerOwner() {
		return formerOwner;
	}

	@Override
	public void wanderingCollector$setFormerOwner(UUID playerUuid) {
		this.formerOwner = playerUuid;
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void readCustomDataFromTag(NbtCompound tag, CallbackInfo ci) {
		if (tag.containsUuid(formerOwnerKey)) {
			formerOwner = tag.getUuid(formerOwnerKey);
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void writeCustomDataFromTag(NbtCompound tag, CallbackInfo ci) {
		if (formerOwner != null) {
			tag.putUuid(formerOwnerKey, formerOwner);
		}
	}

	@Inject(
			method = "tick",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;discard()V", ordinal = 1)
	)
	public void tickInject(CallbackInfo callbackInfo) {
		addStackToThrower();
	}

	@Inject(
			method = "damage",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;discard()V")
	)
	public void onDeathInject(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		addStackToThrower();
	}

	@Unique
	private void addStackToThrower() {
		UUID theFormerOwner = thrower;
		if (theFormerOwner == null) {
			theFormerOwner = formerOwner;
		}
		if (theFormerOwner != null) {
			PlayerEntity player = world.getPlayerByUuid(theFormerOwner);
			if (player instanceof IServerPlayerEntity) {
				((IServerPlayerEntity) player).wandering_collector$addLostStack(getStack());
			}
		}
	}
}
