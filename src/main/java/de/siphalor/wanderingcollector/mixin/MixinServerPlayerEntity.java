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

import com.mojang.authlib.GameProfile;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import de.siphalor.wanderingcollector.WCConfig;
import de.siphalor.wanderingcollector.WanderingCollector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collection;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity implements IServerPlayerEntity {
	@Unique
	private ArrayList<NbtCompound> lostStacks = new ArrayList<>();

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "copyFrom", at = @At("RETURN"))
	public void copyFromInject(ServerPlayerEntity other, boolean alive, CallbackInfo callbackInfo) {
		lostStacks = ((IServerPlayerEntity) other).wandering_collector$getLostStackCompounds();
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void readCustomDataFromTagInject(NbtCompound tag, CallbackInfo callbackInfo) {
		lostStacks.clear();
		if (tag.contains(WanderingCollector.LOST_STACKS_KEY, 9)) {
			NbtList lostStacksTag = tag.getList(WanderingCollector.LOST_STACKS_KEY, 10);
			//noinspection unchecked
			lostStacks.addAll((Collection<? extends NbtCompound>) (Object) lostStacksTag.copy());
		}
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void writeCustomDataToTagInject(NbtCompound tag, CallbackInfo callbackInfo) {
		NbtList lostStacksTag = new NbtList();
		lostStacksTag.addAll(lostStacks);
		tag.put(WanderingCollector.LOST_STACKS_KEY, lostStacksTag);
	}

	@Override
	public ArrayList<NbtCompound> wandering_collector$getLostStackCompounds() {
		return lostStacks;
	}

	@Override
	public void wandering_collector$addLostStack(ItemStack stack) {
		NbtCompound compoundTag = stack.writeNbt(new NbtCompound());
		if (lostStacks.size() < WCConfig.maxLostStackAmount) {
			lostStacks.add(compoundTag);
		} else {
			lostStacks.set(random.nextInt(WCConfig.maxLostStackAmount), compoundTag);
		}
	}
}
