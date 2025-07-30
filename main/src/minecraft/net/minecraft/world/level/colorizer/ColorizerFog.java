package net.minecraft.world.level.colorizer;

public class ColorizerFog {
	private static int[] fogBuffer = new int[65536];

	public static void setFogBiomeColorizer(int[] i0) {
		fogBuffer = i0;
	}

	public static int getFogColor(double d0, double d2) {
		if(d0 > 1.0) d0 = 1.0;
		if(d2 > 1.0) d2 = 1.0;
		if(d0 < 0.0) d0 = 0.0;
		if(d2 < 0.0) d2 = 0.0;
		d2 *= d0;
		int i4 = (int)((1.0D - d0) * 255.0D);
		int i5 = (int)((1.0D - d2) * 255.0D);
		return fogBuffer[i5 << 8 | i4];
	}
}
