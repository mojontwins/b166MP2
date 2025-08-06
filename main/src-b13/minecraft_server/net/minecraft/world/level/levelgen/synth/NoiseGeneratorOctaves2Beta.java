package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

public class NoiseGeneratorOctaves2Beta extends NoiseGenerator {
	private NoiseGenerator2Beta[] noiseGenerator;
	private int numOctaves;

	public NoiseGeneratorOctaves2Beta(Random random1, int i2) {
		this.numOctaves = i2;
		this.noiseGenerator = new NoiseGenerator2Beta[i2];

		for(int i3 = 0; i3 < i2; ++i3) {
			this.noiseGenerator[i3] = new NoiseGenerator2Beta(random1);
		}

	}

	public double[] generateNoiseOctaves(double[] d1, double d2, double d4, int i6, int i7, double d8, double d10, double d12) {
		return this.generateNoiseOctaves(d1, d2, d4, i6, i7, d8, d10, d12, 0.5D);
	}

	public double[] generateNoiseOctaves(double[] d1, double d2, double d4, int i6, int i7, double d8, double d10, double d12, double d14) {
		d8 /= 1.5D;
		d10 /= 1.5D;
		if(d1 != null && d1.length >= i6 * i7) {
			for(int i16 = 0; i16 < d1.length; ++i16) {
				d1[i16] = 0.0D;
			}
		} else {
			d1 = new double[i6 * i7];
		}

		double d21 = 1.0D;
		double d18 = 1.0D;

		for(int i20 = 0; i20 < this.numOctaves; ++i20) {
			this.noiseGenerator[i20].generateNoise(d1, d2, d4, i6, i7, d8 * d18, d10 * d18, 0.55D / d21);
			d18 *= d12;
			d21 *= d14;
		}

		return d1;
	}
}
