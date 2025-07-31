package net.minecraft.world.level;

import java.util.Arrays;

import net.minecraft.world.level.biome.BiomeCache;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.levelgen.genlayer.GenLayer;
import net.minecraft.world.level.levelgen.genlayer.GenLayerHell;

public class WorldChunkManagerHellWithBiomes extends WorldChunkManager {
	
	protected WorldChunkManagerHellWithBiomes() {
		this.biomeCache = new BiomeCache(this);
	}

	public WorldChunkManagerHellWithBiomes(long seed) {
		this();
		
		this.biomesToSpawnIn = Arrays.asList(WorldType.HELL.getBiomesForWorldType());
		
		BiomeGenBase.buildBiomeStructure(WorldType.HELL.getBiomesForWorldType());
		
		GenLayer[] genLayer = GenLayerHell.initializeAllBiomeGenerators(seed, WorldType.HELL);
		
		this.genBiomes = genLayer[0];
		this.biomeIndexLayer = genLayer[1];
	}
	
	public WorldChunkManagerHellWithBiomes(World world) {
		this(world.getSeed());
	}
	
	public float[] getRainfall(float[] rainfalls, int x, int y, int w, int h) {
		if(rainfalls == null || rainfalls.length < w * h) {
			rainfalls = new float[w * h];
		}

		for(int i = 0; i < w * h; ++i) {
			rainfalls[i] = 0F;
		}

		return rainfalls;
	}

	public float getTemperatureAtHeight(float f1, int i2) {
		return f1;
	}

	public float[] getTemperatures(float[] temperatures, int x, int y, int w, int h) {
		if(temperatures == null || temperatures.length < w * h) {
			temperatures = new float[w * h];
		}

		for(int i = 0; i < w * h; ++i) {
			temperatures[i] = 1F;
		}

		return temperatures;
	}

}
