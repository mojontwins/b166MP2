package com.mojontwins.utils;

public class Texels {
	public static final double texW = 16 / TextureAtlasSize.w;
	public static final double texH = 16 / TextureAtlasSize.h;
	
	public static float texelsU(float t) {
		return t / TextureAtlasSize.w;
	}
	
	public static float texelsV(float t) {
		return t / TextureAtlasSize.h;
	}
	
	public static double texelsUd(double t) {
		return t / TextureAtlasSize.w;
	}
	
	public static double texelsVd(double t) {
		return t / TextureAtlasSize.h;
	}
}
