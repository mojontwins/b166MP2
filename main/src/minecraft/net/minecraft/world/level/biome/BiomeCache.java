package net.minecraft.world.level.biome;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.LongHashMap;
import net.minecraft.world.level.WorldChunkManager;

public class BiomeCache {
	private final WorldChunkManager chunkManager;
	private long lastCleanupTime = 0L;
	private LongHashMap cacheMap = new LongHashMap();
	private List<BiomeCacheBlock> cache = new ArrayList<BiomeCacheBlock>();

	public BiomeCache(WorldChunkManager worldChunkManager) {
		this.chunkManager = worldChunkManager;
	}

	public BiomeCacheBlock getBiomeCacheBlock(int x, int z) {
		x >>= 4;
		z >>= 4;
		long hash = (long)x & 4294967295L | ((long)z & 4294967295L) << 32;
		BiomeCacheBlock block = (BiomeCacheBlock)this.cacheMap.getValueByKey(hash);
		if(block == null) {
			block = new BiomeCacheBlock(this, x, z);
			this.cacheMap.add(hash, block);
			this.cache.add(block);
		}

		block.lastAccessTime = System.currentTimeMillis();
		return block;
	}

	public BiomeGenBase getBiomeGenAt(int x, int z) {
		return this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
	}
	
	public float getTemperatureAt(int x, int z) {
		return this.getBiomeCacheBlock(x, z).getTemperatureAt(x, z);
	}
	
	public float getRainfallAt(int x, int z) {
		return this.getBiomeCacheBlock(x, z).getRainfallAt(x, z);
	}

	public void cleanupCache() {
		long now = System.currentTimeMillis();
		long elapsed = now - this.lastCleanupTime;
		if(elapsed > 7500L || elapsed < 0L) {
			this.lastCleanupTime = now;

			for(int i = 0; i < this.cache.size(); ++i) {
				BiomeCacheBlock block = (BiomeCacheBlock)this.cache.get(i);
				long elapsedB = now - block.lastAccessTime;
				if(elapsedB > 30000L || elapsedB < 0L) {
					this.cache.remove(i--);
					long hash = (long)block.xPosition & 4294967295L | ((long)block.zPosition & 4294967295L) << 32;
					this.cacheMap.remove(hash);
				}
			}
		}

	}

	public BiomeGenBase[] getCachedBiomes(int x, int z) {
		return this.getBiomeCacheBlock(x, z).biomes;
	}

	static WorldChunkManager getChunkManager(BiomeCache cache) {
		return cache.chunkManager;
	}
}
