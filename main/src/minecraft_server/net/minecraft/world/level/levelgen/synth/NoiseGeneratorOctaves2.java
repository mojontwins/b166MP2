package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class NoiseGeneratorOctaves2 extends NoiseGenerator {
	private NoiseGenerator2[] noiseGenerator2;
	private int iterations;

	public NoiseGeneratorOctaves2(Random var1, int var2) {
		this.iterations = var2;
		this.noiseGenerator2 = new NoiseGenerator2[var2];

		for(int var3 = 0; var3 < var2; ++var3) {
			this.noiseGenerator2[var3] = new NoiseGenerator2(var1);
		}

	}

	public float[] generateNoiseOctaves(float[] var1, double var2, double var4, int var6, int var7, double var8, double var10, double var12) {
		return this.generateNoiseOctaves(var1, var2, var4, var6, var7, var8, var10, var12, 0.5D);
	}

	public float[] generateNoiseOctaves(float[] var1, double var2, double var4, int var6, int var7, double var8, double var10, double var12, double var14) {
		var8 /= 1.5D;
		var10 /= 1.5D;
		if(var1 != null && var1.length >= var6 * var7) {
			for(int var16 = 0; var16 < var1.length; ++var16) {
				var1[var16] = 0.0F;
			}
		} else {
			var1 = new float[var6 * var7];
		}

		double var21 = 1.0D;
		double var18 = 1.0D;

		for(int var20 = 0; var20 < this.iterations; ++var20) {
			this.noiseGenerator2[var20].generateNoise(var1, var2, var4, var6, var7, var8 * var18, var10 * var18, 0.55D / var21);
			var18 *= var12;
			var21 *= var14;
		}

		return var1;
	}
}
