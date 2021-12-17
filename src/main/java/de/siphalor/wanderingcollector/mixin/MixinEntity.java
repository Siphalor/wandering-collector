package de.siphalor.wanderingcollector.mixin;

import de.siphalor.wanderingcollector.WanderingCollector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
	@Inject(method = "baseTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;destroy()V"))
	public void onVoidRemove(CallbackInfo ci) {
		//noinspection ConstantConditions
		if ((Class) this.getClass() == ItemEntity.class) {
			WanderingCollector.addStackToThrower((ItemEntity)(Object) this);
		}
	}
}
