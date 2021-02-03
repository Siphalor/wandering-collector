package de.siphalor.wanderingcollector.util;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;

public interface IServerPlayerEntity {
	ArrayList<CompoundTag> wandering_collector$getLostStackCompounds();
	void wandering_collector$addLostStack(ItemStack stack);
}
