package org.mojontwins.retroterrain;

import java.util.Random;

import net.minecraft.src.NoiseGenerator;

public class NoiseGeneratorPerlinAlpha extends NoiseGenerator {
	private int[] permutations;
	public double xCoord;
	public double yCoord;
	public double zCoord;

	public NoiseGeneratorPerlinAlpha() {
		this(new Random());
	}

	public NoiseGeneratorPerlinAlpha(Random random) {
		this.permutations = new int[512];
		this.xCoord = random.nextDouble() * 256.0D;
		this.yCoord = random.nextDouble() * 256.0D;
		this.zCoord = random.nextDouble() * 256.0D;

		int i2;
		for(i2 = 0; i2 < 256; this.permutations[i2] = i2++) {
		}

		for(i2 = 0; i2 < 256; ++i2) {
			int i3 = random.nextInt(256 - i2) + i2;
			int i4 = this.permutations[i2];
			this.permutations[i2] = this.permutations[i3];
			this.permutations[i3] = i4;
			this.permutations[i2 + 256] = this.permutations[i2];
		}

	}

	public double generateNoise(double d1, double d3, double d5) {
		double d7 = d1 + this.xCoord;
		double d9 = d3 + this.yCoord;
		double d11 = d5 + this.zCoord;
		int i13 = (int)d7;
		int i14 = (int)d9;
		int i15 = (int)d11;
		if(d7 < (double)i13) {
			--i13;
		}

		if(d9 < (double)i14) {
			--i14;
		}

		if(d11 < (double)i15) {
			--i15;
		}

		int i16 = i13 & 255;
		int i17 = i14 & 255;
		int i18 = i15 & 255;
		d7 -= (double)i13;
		d9 -= (double)i14;
		d11 -= (double)i15;
		double d19 = d7 * d7 * d7 * (d7 * (d7 * 6.0D - 15.0D) + 10.0D);
		double d21 = d9 * d9 * d9 * (d9 * (d9 * 6.0D - 15.0D) + 10.0D);
		double d23 = d11 * d11 * d11 * (d11 * (d11 * 6.0D - 15.0D) + 10.0D);
		int i25 = this.permutations[i16] + i17;
		int i26 = this.permutations[i25] + i18;
		int i27 = this.permutations[i25 + 1] + i18;
		int i28 = this.permutations[i16 + 1] + i17;
		int i29 = this.permutations[i28] + i18;
		int i30 = this.permutations[i28 + 1] + i18;
		return this.lerp(d23, this.lerp(d21, this.lerp(d19, this.grad(this.permutations[i26], d7, d9, d11), this.grad(this.permutations[i29], d7 - 1.0D, d9, d11)), this.lerp(d19, this.grad(this.permutations[i27], d7, d9 - 1.0D, d11), this.grad(this.permutations[i30], d7 - 1.0D, d9 - 1.0D, d11))), this.lerp(d21, this.lerp(d19, this.grad(this.permutations[i26 + 1], d7, d9, d11 - 1.0D), this.grad(this.permutations[i29 + 1], d7 - 1.0D, d9, d11 - 1.0D)), this.lerp(d19, this.grad(this.permutations[i27 + 1], d7, d9 - 1.0D, d11 - 1.0D), this.grad(this.permutations[i30 + 1], d7 - 1.0D, d9 - 1.0D, d11 - 1.0D))));
	}

	public double lerp(double d1, double d3, double d5) {
		return d3 + d1 * (d5 - d3);
	}

	public double grad(int i1, double d2, double d4, double d6) {
		int i8 = i1 & 15;
		double d9 = i8 < 8 ? d2 : d4;
		double d11 = i8 < 4 ? d4 : (i8 != 12 && i8 != 14 ? d6 : d2);
		return ((i8 & 1) == 0 ? d9 : -d9) + ((i8 & 2) == 0 ? d11 : -d11);
	}

	public double generateNoise(double d1, double d3) {
		return this.generateNoise(d1, d3, 0.0D);
	}

	public void populateNoiseArray(double[] d1, double d2, double d4, double d6, int i8, int i9, int i10, double d11, double d13, double d15, double d17) {
		int i19 = 0;
		double d20 = 1.0D / d17;
		int i22 = -1;
		double d29 = 0.0D;
		double d31 = 0.0D;
		double d33 = 0.0D;
		double d35 = 0.0D;

		for(int i37 = 0; i37 < i8; ++i37) {
			double d38 = (d2 + (double)i37) * d11 + this.xCoord;
			int i40 = (int)d38;
			if(d38 < (double)i40) {
				--i40;
			}

			int i41 = i40 & 255;
			d38 -= (double)i40;
			double d42 = d38 * d38 * d38 * (d38 * (d38 * 6.0D - 15.0D) + 10.0D);

			for(int i44 = 0; i44 < i10; ++i44) {
				double d45 = (d6 + (double)i44) * d15 + this.zCoord;
				int i47 = (int)d45;
				if(d45 < (double)i47) {
					--i47;
				}

				int i48 = i47 & 255;
				d45 -= (double)i47;
				double d49 = d45 * d45 * d45 * (d45 * (d45 * 6.0D - 15.0D) + 10.0D);

				for(int i51 = 0; i51 < i9; ++i51) {
					double d52 = (d4 + (double)i51) * d13 + this.yCoord;
					int i54 = (int)d52;
					if(d52 < (double)i54) {
						--i54;
					}

					int i55 = i54 & 255;
					d52 -= (double)i54;
					double d56 = d52 * d52 * d52 * (d52 * (d52 * 6.0D - 15.0D) + 10.0D);
					if(i51 == 0 || i55 != i22) {
						i22 = i55;
						int i64 = this.permutations[i41] + i55;
						int i65 = this.permutations[i64] + i48;
						int i66 = this.permutations[i64 + 1] + i48;
						int i67 = this.permutations[i41 + 1] + i55;
						int i68 = this.permutations[i67] + i48;
						int i69 = this.permutations[i67 + 1] + i48;
						d29 = this.lerp(d42, this.grad(this.permutations[i65], d38, d52, d45), this.grad(this.permutations[i68], d38 - 1.0D, d52, d45));
						d31 = this.lerp(d42, this.grad(this.permutations[i66], d38, d52 - 1.0D, d45), this.grad(this.permutations[i69], d38 - 1.0D, d52 - 1.0D, d45));
						d33 = this.lerp(d42, this.grad(this.permutations[i65 + 1], d38, d52, d45 - 1.0D), this.grad(this.permutations[i68 + 1], d38 - 1.0D, d52, d45 - 1.0D));
						d35 = this.lerp(d42, this.grad(this.permutations[i66 + 1], d38, d52 - 1.0D, d45 - 1.0D), this.grad(this.permutations[i69 + 1], d38 - 1.0D, d52 - 1.0D, d45 - 1.0D));
					}

					double d58 = this.lerp(d56, d29, d31);
					double d60 = this.lerp(d56, d33, d35);
					double d62 = this.lerp(d49, d58, d60);
					int i10001 = i19++;
					d1[i10001] += d62 * d20;
				}
			}
		}

	}
}
