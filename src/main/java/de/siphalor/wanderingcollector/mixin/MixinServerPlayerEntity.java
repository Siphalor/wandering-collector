/*
 * Copyright 2021-2023 Siphalor
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

import com.mojang.authlib.GameProfile;
import de.siphalor.wanderingcollector.LostItemStorage;
import de.siphalor.wanderingcollector.util.IItemEntity;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements IServerPlayerEntity {
	@Unique
	private LostItemStorage lostItemStorage = new LostItemStorage();

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile, @Nullable PlayerPublicKey publicKey) {
		super(world, pos, yaw, gameProfile, publicKey);
	}

	@Inject(
			method = "dropItem",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"),
			locals = LocalCapture.CAPTURE_FAILHARD
	)
	public void onItemDropped(ItemStack itemStack, boolean thrownRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemStack> cir, ItemEntity itemEntity) {
		if (!retainOwnership) {
			((IItemEntity) itemEntity).wanderingCollector$setFormerOwner(getUuid());
		}
	}

	@Inject(method = "copyFrom", at = @At("RETURN"))
	public void copyFromInject(ServerPlayerEntity other, boolean alive, CallbackInfo callbackInfo) {
		lostItemStorage = ((IServerPlayerEntity) other).wandering_collector$getLostItemStorage();
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void readCustomDataFromTagInject(NbtCompound tag, CallbackInfo callbackInfo) {
		lostItemStorage.read(tag);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void writeCustomDataToTagInject(NbtCompound tag, CallbackInfo callbackInfo) {
		lostItemStorage.write(tag);
	}

	@Override
	public LostItemStorage wandering_collector$getLostItemStorage() {
		return lostItemStorage;
	}
}
