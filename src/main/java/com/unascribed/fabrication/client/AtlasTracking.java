package com.unascribed.fabrication.client;

import java.util.Set;

import java.util.WeakHashMap;

import java.util.Collections;

import net.minecraft.client.texture.SpriteAtlasTexture;

public class AtlasTracking {

	public static final Set<SpriteAtlasTexture> allAtlases = Collections.newSetFromMap(new WeakHashMap<>());
	
}
