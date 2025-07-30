package net.minecraft.world.level.levelgen.synth;

import java.util.Random;

import net.minecraft.src.MathHelper;

public class NoiseGeneratorPerlinIndev extends NoiseGenerator {
	private int[] permutations;
	private double xCoord;
	private double yCoord;
	private double zCoord;

	public NoiseGeneratorPerlinIndev() {
		this(new Random());
	}

	public NoiseGeneratorPerlinIndev(Random random) {
		this.permutations = new int[512];
		this.xCoord = random.nextDouble() * 256.0;
		this.yCoord = random.nextDouble() * 256.0;
		this.zCoord = random.nextDouble() * 256.0;

		int var2;
		for(var2 = 0; var2 < 256; this.permutations[var2] = var2++) {
		}

		for(var2 = 0; var2 < 256; ++var2) {
			int var3 = random.nextInt(256 - var2) + var2;
			int var4 = this.permutations[var2];
			this.permutations[var2] = this.permutations[var3];
			this.permutations[var3] = var4;
			this.permutations[var2 + 256] = this.permutations[var2];
		}

	}

	private static double generateNoise(double var0) {
		return var0 * var0 * var0 * (var0 * (var0 * 6.0D - 15.0D) + 10.0D);
	}

	private static double lerp(double var0, double var2, double var4) {
		return var2 + var0 * (var4 - var2);
	}

	private static double grad(int var0, double var1, double var3, double var5) {
		double var8 = (var0 &= 15) < 8 ? var1 : var3;
		double var10 = var0 < 4 ? var3 : (var0 != 12 && var0 != 14 ? var5 : var1);
		return ((var0 & 1) == 0 ? var8 : -var8) + ((var0 & 2) == 0 ? var10 : -var10);
	}

	public double generateNoise(double var1, double var3) {
		double var10 = 0.0D;
		double var8 = var3;
		int var2 = MathHelper.floor_double(var1) & 255;
		int var21 = MathHelper.floor_double(var3) & 255;
		int var4 = MathHelper.floor_double(0.0D) & 255;
		double var6 = var1 - (double)MathHelper.floor_double(var1);
		var8 -= (double)MathHelper.floor_double(var8);
		var10 = 0.0D - (double)MathHelper.floor_double(0.0D);
		double var15 = generateNoise(var6);
		double var17 = generateNoise(var8);
		double var19 = generateNoise(var10);
		int var5 = this.permutations[var2] + var21;
		int var12 = this.permutations[var5] + var4;
		var5 = this.permutations[var5 + 1] + var4;
		var2 = this.permutations[var2 + 1] + var21;
		var21 = this.permutations[var2] + var4;
		var2 = this.permutations[var2 + 1] + var4;
		return lerp(var19, lerp(var17, lerp(var15, grad(this.permutations[var12], var6, var8, var10), grad(this.permutations[var21], var6 - 1.0D, var8, var10)), lerp(var15, grad(this.permutations[var5], var6, var8 - 1.0D, var10), grad(this.permutations[var2], var6 - 1.0D, var8 - 1.0D, var10))), lerp(var17, lerp(var15, grad(this.permutations[var12 + 1], var6, var8, var10 - 1.0D), grad(this.permutations[var21 + 1], var6 - 1.0D, var8, var10 - 1.0D)), lerp(var15, grad(this.permutations[var5 + 1], var6, var8 - 1.0D, var10 - 1.0D), grad(this.permutations[var2 + 1], var6 - 1.0D, var8 - 1.0D, var10 - 1.0D))));
	}
	
	public double generateNoise(double d1, double d2, double d3) {
		double d4 = d1 + this.xCoord;
		double d5 = d2 + this.yCoord;
		double d6 = d3 + this.zCoord;
		d1 = (MathHelper.floor_double(d4) & 0xFF);
		double i = MathHelper.floor_double(d5) & 0xFF;
		d2 = (MathHelper.floor_double(d6) & 0xFF);
		d4 -= MathHelper.floor_double(d4);
		d5 -= MathHelper.floor_double(d5);
		d6 -= MathHelper.floor_double(d6);
		final double d7 = generateNoise(d4);
		final double d8 = generateNoise(d5);
		final double d9 = generateNoise(d6);
		double j = this.permutations[(int)d1] + i;
		d3 = this.permutations[(int)j] + d2;
		j = this.permutations[(int)j + 1] + d2;
		d1 = this.permutations[(int)d1 + 1] + i;
		i = this.permutations[(int)d1] + d2;
		d1 = this.permutations[(int)d1 + 1] + d2;
		return lerp(d9, lerp(d8, lerp(d7, grad(this.permutations[(int)d3], d4, d5, d6), grad(this.permutations[(int)i], d4 - 1.0, d5, d6)), lerp(d7, grad(this.permutations[(int)j], d4, d5 - 1.0, d6), grad(this.permutations[(int)d1], d4 - 1.0, d5 - 1.0, d6))), lerp(d8, lerp(d7, grad(this.permutations[(int)d3 + 1], d4, d5, d6 - 1.0), grad(this.permutations[(int)i + 1], d4 - 1.0, d5, d6 - 1.0)), lerp(d7, grad(this.permutations[(int)j + 1], d4, d5 - 1.0, d6 - 1.0), grad(this.permutations[(int)d1 + 1], d4 - 1.0, d5 - 1.0, d6 - 1.0))));
	}
}
