package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.IntCache;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.biome.BiomeGenBase;

public class GenLayerHellBiomes extends GenLayer {
	private BiomeGenBase[] allowedBiomes;
	public boolean doNoiseBasedChoices = true;
	
	/*
	 * In this preliminary version, just pick up a random biome from allowed biomes.
	 */
	
	public GenLayerHellBiomes(long j1, GenLayer genLayer3, WorldType worldType, boolean doNoiseBasedChoices) {
		super(j1);
		this.parent = genLayer3;
		this.doNoiseBasedChoices = doNoiseBasedChoices;
	
		if(worldType != null) {
			this.allowedBiomes = worldType.getBiomesForWorldType();
		}
	}

	public int[] getInts(int x, int z, int w, int h) {
		int[] src = this.parent.getInts(x, z, w, h);
		int[] dst = IntCache.getIntCache(w * h);

		for(int iY = 0; iY < h; ++iY) {
			for(int iX = 0; iX < w; ++iX) {
				this.initChunkSeed((long)(iX + x), (long)(iY + z));

				int srcC = src[iX + iY * w];
				if(srcC == 0) {
					// 0 is ocean in normal surface genlayers.
					// It has no meaning here (still)
				} else if(srcC == 1) {
					// This selects a biome at random
					dst[iX + iY * w] = this.allowedBiomes[this.nextInt(this.allowedBiomes.length)].biomeID;
				} else {
					// This won't happen, but just in case
					dst[iX + iY * w] = srcC;
				}
			}
		}

		return dst;
	}
	
}
