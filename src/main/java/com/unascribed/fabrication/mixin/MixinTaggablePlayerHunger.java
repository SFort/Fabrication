package com.unascribed.fabrication.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.unascribed.fabrication.PlayerTag;
import com.unascribed.fabrication.interfaces.SetSaturation;
import com.unascribed.fabrication.interfaces.TaggablePlayer;
import com.unascribed.fabrication.support.EligibleIf;
import com.unascribed.fabrication.support.MixinConfigPlugin.RuntimeChecks;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;

@Mixin(HungerManager.class)
@EligibleIf(configEnabled="*.taggable_players")
public abstract class MixinTaggablePlayerHunger implements SetSaturation {

	@Shadow
	private float foodSaturationLevel;
	
	@Inject(at=@At("HEAD"), method="update(Lnet/minecraft/entity/player/PlayerEntity;)V", cancellable=true)
	public void update(PlayerEntity pe, CallbackInfo ci) {
		if (RuntimeChecks.check("*.taggable_players") && pe instanceof TaggablePlayer) {
			if (((TaggablePlayer)pe).fabrication$hasTag(PlayerTag.NO_HUNGER)) {
				ci.cancel();
			}
		}
	}
	
	@Override
	public void fabrication$setSaturation(float sat) {
		foodSaturationLevel = sat;
	}
	
}
