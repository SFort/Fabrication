package com.unascribed.fabrication.mixin;

import java.util.EnumSet;
import java.util.Locale;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.authlib.GameProfile;
import com.unascribed.fabrication.PlayerTag;
import com.unascribed.fabrication.interfaces.SetSaturation;
import com.unascribed.fabrication.interfaces.TaggablePlayer;
import com.unascribed.fabrication.support.EligibleIf;
import com.unascribed.fabrication.support.MixinConfigPlugin.RuntimeChecks;

import com.google.common.base.Enums;
import com.google.common.collect.ImmutableSet;

import net.fabricmc.fabric.api.util.NbtType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(ServerPlayerEntity.class)
@EligibleIf(configEnabled="*.taggable_players")
public abstract class MixinTaggablePlayerServer extends PlayerEntity implements TaggablePlayer {

	public MixinTaggablePlayerServer(World world, BlockPos pos, float yaw, GameProfile profile) {
		super(world, pos, yaw, profile);
	}

	private final EnumSet<PlayerTag> fabrication$tags = EnumSet.noneOf(PlayerTag.class);
	
	@Override
	public Set<PlayerTag> fabrication$getTags() {
		return ImmutableSet.copyOf(fabrication$tags);
	}

	@Override
	public void fabrication$clearTags() {
		fabrication$tags.clear();
	}

	@Override
	public void fabrication$setTag(PlayerTag tag, boolean enabled) {
		if (enabled) {
			fabrication$tags.add(tag);
		} else {
			if (fabrication$tags.remove(tag)) {
				if (tag == PlayerTag.PERMANENT_DOLPHINS_GRACE) {
					removeStatusEffect(StatusEffects.DOLPHINS_GRACE);
				} else if (tag == PlayerTag.PERMANENT_CONDUIT_POWER) {
					removeStatusEffect(StatusEffects.CONDUIT_POWER);
				}
			}
		}
	}

	@Override
	public boolean fabrication$hasTag(PlayerTag tag) {
		return fabrication$tags.contains(tag);
	}
	
	@Inject(at=@At("TAIL"), method="tick()V")
	public void tick(CallbackInfo ci) {
		if (RuntimeChecks.check("*.taggable_players")) {
			if (fabrication$tags.contains(PlayerTag.NO_HUNGER)) {
				getHungerManager().setFoodLevel(getHealth() >= getMaxHealth() ? 20 : 17);
				// prevent the hunger bar from jiggling
				((SetSaturation)getHungerManager()).fabrication$setSaturation(10);
			}
			if (fabrication$tags.contains(PlayerTag.CAN_BREATHE_WATER)) {
				setAir(getMaxAir());
			}
			if (fabrication$tags.contains(PlayerTag.PERMANENT_DOLPHINS_GRACE) && !hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
				addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, Integer.MAX_VALUE, 0, false, false));
			}
			if (fabrication$tags.contains(PlayerTag.PERMANENT_CONDUIT_POWER) && !hasStatusEffect(StatusEffects.CONDUIT_POWER)) {
				addStatusEffect(new StatusEffectInstance(StatusEffects.CONDUIT_POWER, Integer.MAX_VALUE, 0, false, false));
			}
		}
	}

	@Inject(at=@At("HEAD"), method="copyFrom(Lnet/minecraft/server/network/ServerPlayerEntity;Z)V", cancellable=true)
	public void copyFrom(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
		fabrication$tags.clear();
		fabrication$tags.addAll(((TaggablePlayer)oldPlayer).fabrication$getTags());
	}
	
	@Inject(at=@At("TAIL"), method="writeCustomDataToTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void writeCustomDataToTag(CompoundTag tag, CallbackInfo ci) {
		ListTag li = new ListTag();
		for (PlayerTag pt : fabrication$tags) {
			li.add(StringTag.of(pt.lowerName()));
		}
		tag.put("fabrication:Tags", li);
	}
	
	@Inject(at=@At("TAIL"), method="readCustomDataFromTag(Lnet/minecraft/nbt/CompoundTag;)V")
	public void readCustomDataFromTag(CompoundTag tag, CallbackInfo ci) {
		fabrication$tags.clear();
		ListTag li = tag.getList("fabrication:Tags", NbtType.STRING);
		for (int i = 0; i < li.size(); i++) {
			PlayerTag pt = Enums.getIfPresent(PlayerTag.class, li.getString(i).toUpperCase(Locale.ROOT)).orNull();
			if (pt == null) {
				LogManager.getLogger("Fabrication").warn("Unrecognized tag "+li.getString(i)+" while loading player");
			} else {
				fabrication$tags.add(pt);
			}
		}
	}
	
}
