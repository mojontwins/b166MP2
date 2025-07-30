package net.minecraft.src;

import java.util.Arrays;

public class WorldChunkManagerHell extends WorldChunkManager {
	private BiomeGenBase field_4262_e;
	private double field_4261_f;
	private double field_4260_g;

	public WorldChunkManagerHell(BiomeGenBase biomeGenBase1, double d2, double d4) {
		this.field_4262_e = biomeGenBase1;
		this.field_4261_f = d2;
		this.field_4260_g = d4;
	}

	public BiomeGenBase func_4066_a(ChunkCoordIntPair chunkCoordIntPair1) {
		return this.field_4262_e;
	}

	public BiomeGenBase getBiomeGenAt(int i1, int i2) {
		return this.field_4262_e;
	}

	public BiomeGenBase[] func_4065_a(int i1, int i2, int i3, int i4) {
		this.field_4256_d = this.loadBlockGeneratorData(this.field_4256_d, i1, i2, i3, i4);
		return this.field_4256_d;
	}

	public double[] getTemperatures(double[] d1, int i2, int i3, int i4, int i5) {
		if(d1 == null || d1.length < i4 * i5) {
			d1 = new double[i4 * i5];
		}

		Arrays.fill(d1, 0, i4 * i5, this.field_4261_f);
		return d1;
	}

	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomeGenBase1, int i2, int i3, int i4, int i5) {
		if(biomeGenBase1 == null || biomeGenBase1.length < i4 * i5) {
			biomeGenBase1 = new BiomeGenBase[i4 * i5];
		}

		if(this.temperature == null || this.temperature.length < i4 * i5) {
			this.temperature = new double[i4 * i5];
			this.humidity = new double[i4 * i5];
		}

		Arrays.fill(biomeGenBase1, 0, i4 * i5, this.field_4262_e);
		Arrays.fill(this.humidity, 0, i4 * i5, this.field_4260_g);
		Arrays.fill(this.temperature, 0, i4 * i5, this.field_4261_f);
		return biomeGenBase1;
	}
}
