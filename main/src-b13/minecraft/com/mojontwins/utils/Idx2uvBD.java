package com.mojontwins.utils;

import net.minecraft.world.level.tile.Block;

public class Idx2uvBD {
	public static int u, v;
	public static double u1, v1, u2, v2;
	public static final double textureWidth = TextureAtlasSize.w;
	public static final double textureHeight = TextureAtlasSize.h;
	
	public static final double textureWidthAdjusted = textureWidth / 16.0D;
	public static final double textureHeightAdjusted = textureHeight / 16.0D;
	
	public static void calcXY (int idx, Block block) {
		u = (idx & 15);
		v = idx >> 4;
		
		u1 = (u + block.minX) / textureWidthAdjusted;
		v1 = (v + block.minY) / textureHeightAdjusted;
		u2 = (u + block.maxX - 0.01D) / textureWidthAdjusted;
		v2 = (v + block.maxY - 0.01D) / textureHeightAdjusted;
	}
	
	public static void calcXZ (int idx, Block block) {
		u = (idx & 15);
		v = idx >> 4;
		
		u1 = (u + block.minX) / textureWidthAdjusted;
		v1 = (v + block.minZ) / textureHeightAdjusted;
		u2 = (u + block.maxX - 0.01D) / textureWidthAdjusted;
		v2 = (v + block.maxZ - 0.01D) / textureHeightAdjusted;
	}
	
	public static void calcZY (int idx, Block block) {
		u = (idx & 15);
		v = idx >> 4;
		
		u1 = (u + block.minZ) / textureWidthAdjusted;
		v1 = (v + block.minY) / textureHeightAdjusted;
		u2 = (u + block.maxZ - 0.01D) / textureWidthAdjusted;
		v2 = (v + block.maxY - 0.01D) / textureHeightAdjusted;
	}
}
