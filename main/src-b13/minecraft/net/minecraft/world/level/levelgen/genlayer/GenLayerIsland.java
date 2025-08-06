package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.IntCache;

public class GenLayerIsland extends GenLayer {
	public GenLayerIsland(long j1) {
		super(j1);
	}

	public int[] getInts(int x, int z, int w, int h) {
		int[] res = IntCache.getIntCache(w * h);

		for(int iy = 0; iy < h; ++iy) {
			for(int ix = 0; ix < w; ++ix) {
				this.initChunkSeed((long)(x + ix), (long)(z + iy));

				// 1 means "island", 0 means "sea"
				res[ix + iy * w] = this.nextInt(10) == 0 ? 1 : 0;
			}
		}

		if(x > -w && x <= 0 && z > -h && z <= 0) {
			res[-x + -z * w] = 1;
		}

		return res;
	}
}
