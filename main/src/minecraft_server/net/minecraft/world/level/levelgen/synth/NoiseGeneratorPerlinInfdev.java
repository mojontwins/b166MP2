package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

import net.minecraft.src.MathHelper;

public class NoiseGeneratorPerlinInfdev extends NoiseGenerator {
	private int[] permutations;
	private double xCoord;
	private double yCoord;
	private double zCoord;

	public NoiseGeneratorPerlinInfdev() {
		this(new Random());
	}

	public NoiseGeneratorPerlinInfdev(Random random1) {
		this.permutations = new int[512];
		this.xCoord = random1.nextDouble() * 256.0D;
		this.yCoord = random1.nextDouble() * 256.0D;
		this.zCoord = random1.nextDouble() * 256.0D;

		int i2;
		for(i2 = 0; i2 < 256; this.permutations[i2] = i2++) {
		}

		for(i2 = 0; i2 < 256; ++i2) {
			int i3 = random1.nextInt(256 - i2) + i2;
			int i4 = this.permutations[i2];
			this.permutations[i2] = this.permutations[i3];
			this.permutations[i3] = i4;
			this.permutations[i2 + 256] = this.permutations[i2];
		}

	}

	private double generateNoise(double d1, double d3, double d5) {
		double d7 = d1 + this.xCoord;
		double d9 = d3 + this.yCoord;
		double d11 = d5 + this.zCoord;
		int i22 = MathHelper.floor_double(d7) & 255;
		int i2 = MathHelper.floor_double(d9) & 255;
		int armorValue = MathHelper.floor_double(d11) & 255;
		d7 -= (double)MathHelper.floor_double(d7);
		d9 -= (double)MathHelper.floor_double(d9);
		d11 -= (double)MathHelper.floor_double(d11);
		double d16 = generateNoise(d7);
		double d18 = generateNoise(d9);
		double d20 = generateNoise(d11);
		int i4 = this.permutations[i22] + i2;
		int i24 = this.permutations[i4] + armorValue;
		i4 = this.permutations[i4 + 1] + armorValue;
		i22 = this.permutations[i22 + 1] + i2;
		i2 = this.permutations[i22] + armorValue;
		i22 = this.permutations[i22 + 1] + armorValue;
		return lerp(d20, lerp(d18, lerp(d16, grad(this.permutations[i24], d7, d9, d11), grad(this.permutations[i2], d7 - 1.0D, d9, d11)), lerp(d16, grad(this.permutations[i4], d7, d9 - 1.0D, d11), grad(this.permutations[i22], d7 - 1.0D, d9 - 1.0D, d11))), lerp(d18, lerp(d16, grad(this.permutations[i24 + 1], d7, d9, d11 - 1.0D), grad(this.permutations[i2 + 1], d7 - 1.0D, d9, d11 - 1.0D)), lerp(d16, grad(this.permutations[i4 + 1], d7, d9 - 1.0D, d11 - 1.0D), grad(this.permutations[i22 + 1], d7 - 1.0D, d9 - 1.0D, d11 - 1.0D))));
	}

	private static double generateNoise(double d0) {
		return d0 * d0 * d0 * (d0 * (d0 * 6.0D - 15.0D) + 10.0D);
	}

	private static double lerp(double d0, double d2, double d4) {
		return d2 + d0 * (d4 - d2);
	}

	private static double grad(int i0, double d1, double d3, double d5) {
		double d8 = (i0 &= 15) < 8 ? d1 : d3;
		double d10 = i0 < 4 ? d3 : (i0 != 12 && i0 != 14 ? d5 : d1);
		return ((i0 & 1) == 0 ? d8 : -d8) + ((i0 & 2) == 0 ? d10 : -d10);
	}

	public final double generateNoise(double d1, double d3) {
		return this.generateNoise(d1, d3, 0.0D);
	}

	public final double generateNoiseD(double d1, double d3, double d5) {
		return this.generateNoise(d1, d3, d5);
	}
}
