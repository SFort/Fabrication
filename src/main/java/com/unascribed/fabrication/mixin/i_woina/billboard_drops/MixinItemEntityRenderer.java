package com.unascribed.fabrication.mixin.i_woina.billboard_drops;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;
import com.unascribed.fabrication.support.EligibleIf;
import com.unascribed.fabrication.support.Env;
import com.unascribed.fabrication.support.MixinConfigPlugin.RuntimeChecks;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.BlockItem;

@Mixin(ItemEntityRenderer.class)
@EligibleIf(configEnabled="*.billboard_drops", envMatches=Env.CLIENT)
public class MixinItemEntityRenderer {

	@Inject(at=@At(value="INVOKE", target="net/minecraft/client/util/math/MatrixStack.push()V", shift=Shift.AFTER, ordinal=1),
			method="render(Lnet/minecraft/entity/ItemEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V")
	public void render(ItemEntity entity, float f, float g, MatrixStack matrices, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci) {
		if (RuntimeChecks.check("*.billboard_drops") && !(entity.getStack().getItem() instanceof BlockItem)) {
			matrices.scale(1, 1, 0.01f);
		}
	}
	
}
