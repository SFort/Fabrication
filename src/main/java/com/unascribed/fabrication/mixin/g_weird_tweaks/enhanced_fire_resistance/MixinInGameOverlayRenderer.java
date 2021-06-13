package com.unascribed.fabrication.mixin.g_weird_tweaks.enhanced_fire_resistance;

import com.unascribed.fabrication.support.EligibleIf;
import com.unascribed.fabrication.support.MixinConfigPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameOverlayRenderer.class)
@EligibleIf(configEnabled="*.enhanced_fire_resistance")
public abstract class MixinInGameOverlayRenderer {
	
	@Inject(at=@At("HEAD"), method="renderFireOverlay(Lnet/minecraft/client/MinecraftClient;Lnet/minecraft/client/util/math/MatrixStack;)V", cancellable=true)
	private static void renderFireOverlay(MinecraftClient minecraftClient, MatrixStack matrixStack, CallbackInfo ci) {
		if (MixinConfigPlugin.isEnabled("*.enhanced_fire_resistance") && minecraftClient.cameraEntity instanceof LivingEntity && ((LivingEntity)minecraftClient.cameraEntity).hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
			ci.cancel();
		}
	}
}
