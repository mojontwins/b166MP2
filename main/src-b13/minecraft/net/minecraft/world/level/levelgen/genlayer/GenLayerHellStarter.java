package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.IntCache;

public class GenLayerHellStarter extends GenLayerHell {

	public GenLayerHellStarter(long seed) {
		super(seed);
	}

	@Override
	public int[] getInts(int x, int z, int w, int h) {
		int[] dst = IntCache.getIntCache(w * h);

		for (int iY = 0; iY < h; ++iY) {
			for (int iX = 0; iX < w; ++iX) {
				dst[iX + iY * w] = 1;
			}
		}

		if (x > -w && x <= 0 && z > -h && z <= 0) {
			dst[-x + -z * w] = 1;
		}

		return dst;
	}
}
