package com.unascribed.fabrication.mixin.g_weird_tweaks.enhanced_fire_resistance;

import com.mojang.blaze3d.systems.RenderSystem;
import com.unascribed.fabrication.support.EligibleIf;
import com.unascribed.fabrication.support.MixinConfigPlugin;
import net.minecraft.client.render.BackgroundRenderer;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.tag.FluidTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BackgroundRenderer.class)
@EligibleIf(configEnabled="*.enhanced_fire_resistance")
public abstract class MixinBackgroundRenderer {
	@Inject(method="applyFog(Lnet/minecraft/client/render/Camera;Lnet/minecraft/client/render/BackgroundRenderer$FogType;FZ)V",
			at=@At("TAIL")
	)
	private static void updateFog(Camera camera, BackgroundRenderer.FogType fogType, float viewDistance, boolean thickFog, CallbackInfo ci) {
		Entity cameraEntity = camera.getFocusedEntity();
		if (MixinConfigPlugin.isEnabled("*.enhanced_fire_resistance") && cameraEntity instanceof LivingEntity &&
				((LivingEntity)cameraEntity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE) && camera.getSubmergedFluidState().isIn(FluidTags.LAVA)) {
			RenderSystem.fogEnd(Math.min(viewDistance, 30.0F));
		}
	}
}
