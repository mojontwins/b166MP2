package net.minecraft.world.level;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.biome.BiomeGenBase;

public class WorldChunkManagerHell extends WorldChunkManager {
	private BiomeGenBase biomeGenerator;
	private float hellTemperature;
	private float rainfall;

	public WorldChunkManagerHell(BiomeGenBase biomeGenBase1, float f2, float f3) {
		this.biomeGenerator = biomeGenBase1;
		this.hellTemperature = f2;
		this.rainfall = f3;
	}

	public List<BiomeGenBase> getBiomesToSpawnIn() {
		List<BiomeGenBase> biomes = new ArrayList<BiomeGenBase> ();
		biomes.add(this.biomeGenerator);
		return biomes;
	}
	
	public BiomeGenBase getBiomeGenAt(int i1, int i2) {
		return this.biomeGenerator;
	}

	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomeGenBase1, int i2, int i3, int i4, int i5) {
		if(biomeGenBase1 == null || biomeGenBase1.length < i4 * i5) {
			biomeGenBase1 = new BiomeGenBase[i4 * i5];
		}

		Arrays.fill(biomeGenBase1, 0, i4 * i5, this.biomeGenerator);
		return biomeGenBase1;
	}

	public float[] getTemperatures(float[] f1, int i2, int i3, int i4, int i5) {
		if(f1 == null || f1.length < i4 * i5) {
			f1 = new float[i4 * i5];
		}

		Arrays.fill(f1, 0, i4 * i5, this.hellTemperature);
		return f1;
	}

	public float[] getRainfall(float[] f1, int i2, int i3, int i4, int i5) {
		if(f1 == null || f1.length < i4 * i5) {
			f1 = new float[i4 * i5];
		}

		Arrays.fill(f1, 0, i4 * i5, this.rainfall);
		return f1;
	}

	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomeGenBase1, int i2, int i3, int i4, int i5) {
		if(biomeGenBase1 == null || biomeGenBase1.length < i4 * i5) {
			biomeGenBase1 = new BiomeGenBase[i4 * i5];
		}

		Arrays.fill(biomeGenBase1, 0, i4 * i5, this.biomeGenerator);
		return biomeGenBase1;
	}

	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomeGenBase1, int i2, int i3, int i4, int i5, boolean z6) {
		return this.loadBlockGeneratorData(biomeGenBase1, i2, i3, i4, i5);
	}

	public ChunkPosition findBiomePosition(int i1, int i2, int i3, List<BiomeGenBase> list4, Random random5) {
		return list4.contains(this.biomeGenerator) ? new ChunkPosition(i1 - i3 + random5.nextInt(i3 * 2 + 1), 0, i2 - i3 + random5.nextInt(i3 * 2 + 1)) : null;
	}

	public boolean areBiomesViable(int i1, int i2, int i3, List<BiomeGenBase> list4) {
		return list4.contains(this.biomeGenerator);
	}
}
