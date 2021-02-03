package de.siphalor.wanderingcollector.mixin;

import com.mojang.authlib.GameProfile;
import de.siphalor.wanderingcollector.util.IServerPlayerEntity;
import de.siphalor.wanderingcollector.WCConfig;
import de.siphalor.wanderingcollector.WanderingCollector;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
	private ArrayList<CompoundTag> lostStacks = new ArrayList<>();

	public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	@Inject(method = "copyFrom", at = @At("RETURN"))
	public void copyFromInject(ServerPlayerEntity other, boolean alive, CallbackInfo callbackInfo) {
		lostStacks = ((IServerPlayerEntity) other).wandering_collector$getLostStackCompounds();
	}

	@Inject(method = "readCustomDataFromTag", at = @At("RETURN"))
	public void readCustomDataFromTagInject(CompoundTag tag, CallbackInfo callbackInfo) {
		lostStacks.clear();
		if (tag.contains(WanderingCollector.LOST_STACKS_KEY, 9)) {
			ListTag lostStacksTag = tag.getList(WanderingCollector.LOST_STACKS_KEY, 10);
			//noinspection unchecked
			lostStacks.addAll((Collection<? extends CompoundTag>) (Object) lostStacksTag.copy());
		}
	}

	@Inject(method = "writeCustomDataToTag", at = @At("RETURN"))
	public void writeCustomDataToTagInject(CompoundTag tag, CallbackInfo callbackInfo) {
		ListTag lostStacksTag = new ListTag();
		lostStacksTag.addAll(lostStacks);
		tag.put(WanderingCollector.LOST_STACKS_KEY, lostStacksTag);
	}

	@Override
	public ArrayList<CompoundTag> wandering_collector$getLostStackCompounds() {
		return lostStacks;
	}

	@Override
	public void wandering_collector$addLostStack(ItemStack stack) {
		CompoundTag compoundTag = stack.toTag(new CompoundTag());
		if (lostStacks.size() < WCConfig.maxLostStackAmount) {
			lostStacks.add(compoundTag);
		} else {
			lostStacks.set(random.nextInt(WCConfig.maxLostStackAmount), compoundTag);
		}
	}
}
