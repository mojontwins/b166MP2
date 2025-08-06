package com.mojontwins.utils;

public class TextureAtlasSize {
	public static final float w = 256.0F;
	public static final float h = 512.0F;
	public static final int wi = 256;
	public static final int hi = 512;
	
	public static final int tiles = (int)(w * h / 256);
	public static final int widthInTiles = (int)(w / 16.0F);
	public static final int heightInTiles = (int)(h / 16.0F);
}
