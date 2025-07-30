package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

import net.minecraft.src.MathHelper;

public class NoiseGeneratorOctaves extends NoiseGenerator {
	private NoiseGeneratorPerlin[] generatorCollection;
	private int octaves;

	public NoiseGeneratorOctaves(Random random1, int i2) {
		this.octaves = i2;
		this.generatorCollection = new NoiseGeneratorPerlin[i2];

		for(int i3 = 0; i3 < i2; ++i3) {
			this.generatorCollection[i3] = new NoiseGeneratorPerlin(random1);
		}

	}

	public double[] generateNoiseOctaves(double[] d1, int i2, int i3, int i4, int i5, int i6, int i7, double d8, double d10, double d12) {
		if(d1 == null) {
			d1 = new double[i5 * i6 * i7];
		} else {
			for(int i14 = 0; i14 < d1.length; ++i14) {
				d1[i14] = 0.0D;
			}
		}

		double d27 = 1.0D;

		for(int i16 = 0; i16 < this.octaves; ++i16) {
			double d17 = (double)i2 * d27 * d8;
			double d19 = (double)i3 * d27 * d10;
			double d21 = (double)i4 * d27 * d12;
			long j23 = MathHelper.floor_double_long(d17);
			long j25 = MathHelper.floor_double_long(d21);
			d17 -= (double)j23;
			d21 -= (double)j25;
			j23 %= 16777216L;
			j25 %= 16777216L;
			d17 += (double)j23;
			d21 += (double)j25;
			this.generatorCollection[i16].populateNoiseArray(d1, d17, d19, d21, i5, i6, i7, d8 * d27, d10 * d27, d12 * d27, d27);
			d27 /= 2.0D;
		}

		return d1;
	}
	
	public double[] generateNoiseOctaves(double[] data, double d2, double d4, double d6, int x, int y, int z, double d11, double d13, double d15) {
		if(data == null) {
			data = new double[x * y * z];
		} else {
			for(int i17 = 0; i17 < data.length; ++i17) {
				data[i17] = 0.0D;
			}
		}

		double d20 = 1.0D;

		for(int i19 = 0; i19 < this.octaves; ++i19) {
			this.generatorCollection[i19].populateNoiseArray(data, d2, d4, d6, x, y, z, d11 * d20, d13 * d20, d15 * d20, d20);
			d20 /= 2.0D;
		}

		return data;
	}

	public double[] generateNoiseOctaves(double[] d1, int i2, int i3, int i4, int i5, double d6, double d8, double d10) {
		return this.generateNoiseOctaves(d1, i2, 10, i3, i4, 1, i5, d6, 1.0D, d8);
	}
}
