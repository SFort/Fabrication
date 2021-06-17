package com.unascribed.fabrication.mixin.i_woina.janky_arm;

import net.minecraft.util.math.Quaternion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.unascribed.fabrication.support.EligibleIf;
import com.unascribed.fabrication.support.Env;
import com.unascribed.fabrication.support.MixinConfigPlugin;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3d;
import net.minecraft.util.Arm;

@Mixin(HeldItemRenderer.class)
@EligibleIf(configEnabled="*.janky_arm", envMatches=Env.CLIENT)
public class MixinHeldItemRenderer {

	@Inject(at=@At(value="CONSTANT", args="floatValue=-20"),
			method="renderArmHoldingItem(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;IFFLnet/minecraft/util/Arm;)V")
	private void renderArmHoldingItem(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float equipProgress, float swingProgress, Arm arm, CallbackInfo ci) {
		float f = arm == Arm.LEFT ? -1 : 1;
		if (MixinConfigPlugin.isEnabled("*.janky_arm")) {
			matrices.multiply(new Quaternion(-33.0F, f*-35.0F, f*-8f, true));
			matrices.translate(0.07*f, 0.13, -0.04);
		}
	}
	
}
