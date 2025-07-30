package com.mojontwins.utils;

public class Idx2uvF {
	public static int u, v;
	public static double u1, v1, u2, v2;
	public static final double textureWidth = TextureAtlasSize.w;
	public static final double textureHeight = TextureAtlasSize.h;
	public static void calc (int idx) {
		u = (idx & 15) << 4;
		v = idx & 0xff0;
		
		u1 = u / textureWidth;
		v1 = v / textureHeight;
		u2 = u1 + Texels.texW;
		v2 = v1 + Texels.texH;
	}
}
