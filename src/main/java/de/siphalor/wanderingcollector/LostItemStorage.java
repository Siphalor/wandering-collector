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

package de.siphalor.wanderingcollector;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class LostItemStorage {
	private static final Random RANDOM = new Random();
	private final List<ItemStack> stacks = new ArrayList<>();

	public void read(NbtCompound parentNbt) {
		stacks.clear();

		if (parentNbt.contains(WanderingCollector.LOST_STACKS_KEY)) {
			if (parentNbt.contains(WanderingCollector.LOST_STACKS_KEY, NbtType.LIST)) {
				readStacksFrom(parentNbt, WanderingCollector.LOST_STACKS_KEY);
			} else if (parentNbt.contains(WanderingCollector.LOST_STACKS_KEY, NbtType.COMPOUND)) {
				NbtCompound ownNbt = parentNbt.getCompound(WanderingCollector.LOST_STACKS_KEY);
				if (ownNbt.contains(WanderingCollector.LOST_STACKS_KEY, NbtType.LIST)) {
					readStacksFrom(ownNbt, "Stacks");
				}
			}
		}
	}

	private void readStacksFrom(NbtCompound parentNbt, String key) {
		NbtList listNbt = parentNbt.getList(key, NbtType.COMPOUND);
		for (NbtElement stackNbt : listNbt) {
			if (stackNbt instanceof NbtCompound) {
				stacks.add(ItemStack.fromNbt((NbtCompound) stackNbt));
			}
		}
	}

	public void write(NbtCompound parentNbt) {
		if (stacks.isEmpty()) {
			return;
		}

		NbtCompound ownNbt = new NbtCompound();
		parentNbt.put(WanderingCollector.LOST_STACKS_KEY, ownNbt);

		NbtList listNbt = new NbtList();
		for (ItemStack stack : stacks) {
			listNbt.add(stack.writeNbt(new NbtCompound()));
		}
		ownNbt.put("Stacks", listNbt);
	}

	public boolean isEmpty() {
		return stacks.isEmpty();
	}

	public void add(ItemStack newStack) {
		if (WCConfig.combineLostStacks && tryCombine(newStack)) {
			return;
		}

		if (stacks.size() >= WCConfig.maxLostStackAmount) {
			stacks.remove(0);
		}
		stacks.add(newStack);
	}

	private boolean tryCombine(ItemStack newStack) {
		int requiredSpace = newStack.getCount();
		int space = 0;
		List<ItemStack> equalStacks = new ArrayList<>();
		for (ItemStack stack : stacks) {
			if (ItemStack.canCombine(stack, newStack)) {
				equalStacks.add(stack);
				space += stack.getMaxCount() - stack.getCount();

				if (space >= requiredSpace) {
					break;
				}
			}
		}

		if (space < requiredSpace) {
			return false;
		}

		for (ItemStack equalStack : equalStacks) {
			int move = Math.min(equalStack.getMaxCount() - equalStack.getCount(), requiredSpace);
			equalStack.increment(move);
			requiredSpace -= move;
			if (requiredSpace <= 0) {
				break;
			}
		}
		return true;
	}

	public Collection<ItemStack> poll(int stackCount) {
		stackCount = Math.min(stackCount, stacks.size());
		List<ItemStack> result = new ArrayList<>(stackCount);
		switch (WCConfig.offerCreation) {
			case NEWEST: {
				List<ItemStack> range = stacks.subList(stacks.size() - stackCount, stacks.size());
				result.addAll(range);
				range.clear();
				break;
			}
			case OLDEST: {
				List<ItemStack> range = stacks.subList(0, stackCount);
				result.addAll(range);
				range.clear();
				break;
			}
			case RANDOM:
				for (int i = 0; i < stackCount; i++) {
					int randIndex = RANDOM.nextInt(stacks.size());
					ItemStack stack = stacks.get(randIndex);
					if (stack.getCount() > stack.getMaxCount()) {
						result.add(stack.split(stack.getMaxCount()));
					} else {
						result.add(stack);
						stacks.remove(randIndex);
					}
				}
				break;
		}
		return result;
	}

	public enum PollMode {NEWEST, OLDEST, RANDOM}
}
