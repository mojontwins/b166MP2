package net.minecraft.world.level.levelgen.genlayer;

import net.minecraft.world.level.WorldType;

public abstract class GenLayer {
	protected long worldGenSeed;
	public GenLayer parent;
	protected long chunkSeed;
	protected long baseSeed;
	public final long seed;

	public static GenLayer[] initializeAllBiomeGenerators(long j0, WorldType worldType2) {

		return new GenLayer[]{null, null, null};
	}

	public GenLayer(long j1) {
		this.seed = j1;
		this.baseSeed = j1;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += j1;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += j1;
		this.baseSeed *= this.baseSeed * 6364136223846793005L + 1442695040888963407L;
		this.baseSeed += j1;
	}

	public void initWorldGenSeed(long j1) {
		this.worldGenSeed = j1;
		if(this.parent != null) {
			this.parent.initWorldGenSeed(j1);
		}

		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
		this.worldGenSeed *= this.worldGenSeed * 6364136223846793005L + 1442695040888963407L;
		this.worldGenSeed += this.baseSeed;
	}

	public void initChunkSeed(long j1, long j3) {
		this.chunkSeed = this.worldGenSeed;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += j1;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += j3;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += j1;
		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += j3;
	}

	protected int nextInt(int i1) {
		int i2 = (int)((this.chunkSeed >> 24) % (long)i1);
		if(i2 < 0) {
			i2 += i1;
		}

		this.chunkSeed *= this.chunkSeed * 6364136223846793005L + 1442695040888963407L;
		this.chunkSeed += this.worldGenSeed;
		return i2;
	}

	public abstract int[] getInts(int x, int y, int w, int h);
}
