package com.unascribed.fabrication.mixin.b_utility.killmessage;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.unascribed.fabrication.interfaces.GetKillMessage;
import com.unascribed.fabrication.support.EligibleIf;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;

@Mixin(Entity.class)
@EligibleIf(configEnabled="*.killmessage")
public abstract class MixinEntity implements GetKillMessage {
	
	private String fabrication$killmessage = null;
	
	@Override
	public String fabrication$getKillMessage() {
		return fabrication$killmessage;
	}
	
	@Inject(at=@At("TAIL"), method="writeNbt")
	public void toTag(NbtCompound tag, CallbackInfoReturnable<NbtCompound> ci) {
		if (fabrication$killmessage != null) {
			tag.putString("KillMessage", fabrication$killmessage);
		}
	}
	
	@Inject(at=@At("TAIL"), method="readNbt")
	public void fromTag(NbtCompound tag, CallbackInfo ci) {
		if (tag.contains("KillMessage", NbtType.STRING) ) {
			fabrication$killmessage = tag.getString("KillMessage");
		}
	}
	
}
