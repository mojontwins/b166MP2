package net.minecraft.world.level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.GameRules;
import net.minecraft.world.level.biome.BiomeCache;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.levelgen.genlayer.GenLayer;

public class WorldChunkManager {
	// GenBiomes is used for instance by the release noise calculator. It is 1/4 the size of the world.
	protected GenLayer genBiomes;
	
	// biomeIndexLayer contains which biome is each block in the world.
	protected GenLayer biomeIndexLayer;
	
	protected BiomeCache biomeCache;
	protected List<BiomeGenBase> biomesToSpawnIn;

	public WorldChunkManager() {
		this.biomeCache = new BiomeCache(this);
		
		this.biomesToSpawnIn = new ArrayList<BiomeGenBase>();
		this.biomesToSpawnIn.add(BiomeGenBase.plains);
		this.biomesToSpawnIn.add(BiomeGenBase.forest);
		this.biomesToSpawnIn.add(BiomeGenBase.rainforest);
		this.biomesToSpawnIn.add(BiomeGenBase.seasonalForest);

	}

	public WorldChunkManager(long seed, WorldType worldType) {
		this();
			
		if (GameRules.genlayerWorldChunkManager) {
			BiomeGenBase.buildBiomeStructure(worldType.getBiomesForWorldType());
		}
		
		GenLayer[] genLayerArray = GameRules.initializeAllBiomeGenerators(seed, worldType);
		
		this.genBiomes = genLayerArray[0];
		this.biomeIndexLayer = genLayerArray[1];
	}

	public WorldChunkManager(World world) {
		this(world.getSeed(), world.getWorldInfo().getTerrainType());
	}

	public List<BiomeGenBase> getBiomesToSpawnIn() {
		return this.biomesToSpawnIn;
	}

	public BiomeGenBase getBiomeGenAt(int x, int z) {
		return this.biomeCache.getBiomeGenAt(x, z);
	}
	
	public float getTemperatureAt(int x, int z) {
		return this.biomeCache.getTemperatureAt(x, z);
	}
	
	public float getRainfallAt(int x, int z) {
		return this.biomeCache.getRainfallAt(x, z);
	}

	public float[] getRainfall(float[] rainfalls, int x, int y, int w, int h) {
		IntCache.resetIntCache();
		if(rainfalls == null || rainfalls.length < w * h) {
			rainfalls = new float[w * h];
		}

		int[] biomeIDs = this.biomeIndexLayer.getInts(x, y, w, h);

		for(int i = 0; i < w * h; ++i) {
			float rainfall = (float)BiomeGenBase.biomeList[biomeIDs[i]].getIntRainfall() / 65536.0F;
			if(rainfall > 1.0F) {
				rainfall = 1.0F;
			}

			rainfalls[i] = rainfall;
		}

		return rainfalls;
	}

	public float getTemperatureAtHeight(float t, int h) {
		return t;
	}

	public float[] getTemperatures(float[] temperatures, int x, int y, int w, int h) {
		IntCache.resetIntCache();
		if(temperatures == null || temperatures.length < w * h) {
			temperatures = new float[w * h];
		}

		int[] biomeIDs = this.biomeIndexLayer.getInts(x, y, w, h);

		for(int i = 0; i < w * h; ++i) {
			float t = (float)BiomeGenBase.biomeList[biomeIDs[i]].getIntTemperature() / 65536.0F;
			if(t > 1.0F) {
				t = 1.0F;
			}

			temperatures[i] = t;
		}

		return temperatures;
	}

	public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, int x, int y, int w, int h) {
		IntCache.resetIntCache();
		if(biomes == null || biomes.length < w * h) {
			biomes = new BiomeGenBase[w * h];
		}

		int[] i6 = this.genBiomes.getInts(x, y, w, h);

		for(int i7 = 0; i7 < w * h; ++i7) {
			biomes[i7] = BiomeGenBase.biomeList[i6[i7]];
		}

		return biomes;
	}

	// i.e. get biome array for chunk
	public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] biomes, int x, int y, int w, int hy) {
		return this.getBiomeGenAt(biomes, x, y, w, hy, true);
	}

	public BiomeGenBase[] getBiomeGenAt(BiomeGenBase[] biomes, int x, int y, int w, int h, boolean isChunk) {
		IntCache.resetIntCache();
		if(biomes == null || biomes.length < w * h) {
			biomes = new BiomeGenBase[w * h];
		}

		if(isChunk && w == 16 && h == 16 && (x & 15) == 0 && (y & 15) == 0) {
			BiomeGenBase[] biomeGenBase9 = this.biomeCache.getCachedBiomes(x, y);
			System.arraycopy(biomeGenBase9, 0, biomes, 0, w * h);
			return biomes;
		} else {
			int[] biomeIDs = this.biomeIndexLayer.getInts(x, y, w, h);

			for(int i = 0; i < w * h; ++i) {
				biomes[i] = BiomeGenBase.biomeList[biomeIDs[i]];
			}

			return biomes;
		}
	}

	// Used by the release player spawner
	public boolean areBiomesViable(int x, int z, int r, List<BiomeGenBase> goodBiomes) {
		int x1 = x - r >> 2;
		int z1 = z - r >> 2;
		int x2 = x + r >> 2;
		int z2 = z + r >> 2;
		int w = x2 - x1 + 1;
		int l = z2 - z1 + 1;
		int[] biomeIDs = this.genBiomes.getInts(x1, z1, w, l);

		for(int i = 0; i < w * l; ++i) {
			BiomeGenBase biome = BiomeGenBase.biomeList[biomeIDs[i]];
			if(!goodBiomes.contains(biome)) {
				return false;
			}
		}

		return true;
	}

	// Used to find a good spot for a structure
	public ChunkPosition findBiomePosition(int x, int z, int r, List<BiomeGenBase> goodBiomes, Random rand) {
		int x1 = x - r >> 2;
		int z1 = z - r >> 2;
		int x2 = x + r >> 2;
		int z2 = z + r >> 2;
		int w = x2 - x1 + 1;
		int l = z2 - z1 + 1;
		int[] biomeIDs = this.genBiomes.getInts(x1, z1, w, l);
		ChunkPosition pos = null;
		int attempts = 0;

		for(int i = 0; i < biomeIDs.length; ++i) {
			int rx = x1 + i % w << 2;
			int rz = z1 + i / w << 2;
			BiomeGenBase biomeGenBase18 = BiomeGenBase.biomeList[biomeIDs[i]];
			if(goodBiomes.contains(biomeGenBase18) && (pos == null || rand.nextInt(attempts + 1) == 0)) {
				pos = new ChunkPosition(rx, 0, rz);
				++attempts;
			}
		}

		return pos;
	}

	public void cleanupCache() {
		this.biomeCache.cleanupCache();
	}
}
