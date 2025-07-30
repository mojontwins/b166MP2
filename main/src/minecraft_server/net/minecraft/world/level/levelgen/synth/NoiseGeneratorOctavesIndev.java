package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class NoiseGeneratorOctavesIndev extends NoiseGenerator  {
	private NoiseGeneratorPerlinIndev[] generatorCollection;
	private int octaves;

	public NoiseGeneratorOctavesIndev(Random var1, int var2) {
		this.octaves = var2;
		this.generatorCollection = new NoiseGeneratorPerlinIndev[var2];

		for(int var3 = 0; var3 < var2; ++var3) {
			this.generatorCollection[var3] = new NoiseGeneratorPerlinIndev(var1);
		}

	}

	public final double generateNoise(double var1, double var3) {
		double var5 = 0.0D;
		double var7 = 1.0D;

		for(int var9 = 0; var9 < this.octaves; ++var9) {
			var5 += this.generatorCollection[var9].generateNoise(var1 / var7, var3 / var7) * var7;
			var7 *= 2.0D;
		}

		return var5;
	}
	
	public double generateNoiseOctaves(final double d, double d1, final double d2) {
		double d3 = 0.0;
		double d4 = 1.0;
		for (d1 = 0.0; d1 < this.octaves; ++d1) {
			d3 += this.generatorCollection[(int)d1].generateNoise(d / d4, 0.0 / d4, d2 / d4) * d4;
			d4 *= 2.0;
		}
		return d3;
	}
}
