package com.unascribed.fabrication.mixin.i_woina.flat_items;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import com.unascribed.fabrication.support.EligibleIf;
import com.unascribed.fabrication.support.Env;
import com.unascribed.fabrication.support.MixinConfigPlugin.RuntimeChecks;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformation.Mode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;

@Mixin(HeldItemRenderer.class)
@EligibleIf(configEnabled="*.flat_items", envMatches=Env.CLIENT)
public class MixinHeldItemRenderer {

	@ModifyVariable(at=@At("HEAD"), method="renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformation$Mode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
			index=3, argsOnly=true)
	public Mode renderItemTransformMode(Mode orig, LivingEntity entity, ItemStack stack, Mode orig2, boolean leftHanded, MatrixStack matrices) {
		if (RuntimeChecks.check("*.flat_items")) {
			if (!(stack.getItem() instanceof BlockItem)) {
				if (orig == Mode.FIRST_PERSON_LEFT_HAND || orig == Mode.FIRST_PERSON_RIGHT_HAND) {
					matrices.translate(leftHanded ? -0.1 : 0.1, -0.16, -0.15);
					matrices.scale(1, 1, 0.01f);
					if (leftHanded) {
						matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
					}
					return Mode.GROUND;
				}
			}
		}
		return orig;
	}
	
}
