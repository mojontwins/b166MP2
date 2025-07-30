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

	public BiomeCache(WorldChunkManager worldChunkManager1) {
		this.chunkManager = worldChunkManager1;
	}

	public BiomeCacheBlock getBiomeCacheBlock(int i1, int i2) {
		i1 >>= 4;
		i2 >>= 4;
		long j3 = (long)i1 & 4294967295L | ((long)i2 & 4294967295L) << 32;
		BiomeCacheBlock biomeCacheBlock5 = (BiomeCacheBlock)this.cacheMap.getValueByKey(j3);
		if(biomeCacheBlock5 == null) {
			biomeCacheBlock5 = new BiomeCacheBlock(this, i1, i2);
			this.cacheMap.add(j3, biomeCacheBlock5);
			this.cache.add(biomeCacheBlock5);
		}

		biomeCacheBlock5.lastAccessTime = System.currentTimeMillis();
		return biomeCacheBlock5;
	}

	public BiomeGenBase getBiomeGenAt(int i1, int i2) {
		return this.getBiomeCacheBlock(i1, i2).getBiomeGenAt(i1, i2);
	}

	public void cleanupCache() {
		long j1 = System.currentTimeMillis();
		long j3 = j1 - this.lastCleanupTime;
		if(j3 > 7500L || j3 < 0L) {
			this.lastCleanupTime = j1;

			for(int i5 = 0; i5 < this.cache.size(); ++i5) {
				BiomeCacheBlock biomeCacheBlock6 = (BiomeCacheBlock)this.cache.get(i5);
				long j7 = j1 - biomeCacheBlock6.lastAccessTime;
				if(j7 > 30000L || j7 < 0L) {
					this.cache.remove(i5--);
					long j9 = (long)biomeCacheBlock6.xPosition & 4294967295L | ((long)biomeCacheBlock6.zPosition & 4294967295L) << 32;
					this.cacheMap.remove(j9);
				}
			}
		}

	}

	public BiomeGenBase[] getCachedBiomes(int i1, int i2) {
		return this.getBiomeCacheBlock(i1, i2).biomes;
	}

	static WorldChunkManager getChunkManager(BiomeCache biomeCache0) {
		return biomeCache0.chunkManager;
	}
}
