package net.minecraft.world.level.levelgen;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.IChunkProvider;

public class MapGenBase {
	protected int range = 8;
	protected Random rand = new Random();
	protected World worldObj;

	public void generate(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, byte[] blocks) {
		int range = this.range;
		this.worldObj = world;
		this.rand.setSeed(world.getSeed());
		long rval1 = this.rand.nextLong();
		long rval2 = this.rand.nextLong();

		for(int x = chunkX - range; x <= chunkX + range; ++x) {
			for(int z = chunkZ - range; z <= chunkZ + range; ++z) {
				long rvalX = (long)x * rval1;
				long rvalZ = (long)z * rval2;
				this.rand.setSeed(rvalX ^ rvalZ ^ world.getSeed());
				this.recursiveGenerate(world, x, z, chunkX, chunkZ, blocks);
			}
		}

	}

	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int chunkX0, int chunkZ0, byte[] blocks) {
	}
	
	public void generate(IChunkProvider chunkProvider, World world, int chunkX, int chunkZ, short[] blocks) {
		int range = this.range;
		this.worldObj = world;
		this.rand.setSeed(world.getSeed());
		long rval1 = this.rand.nextLong();
		long rval2 = this.rand.nextLong();

		for(int x = chunkX - range; x <= chunkX + range; ++x) {
			for(int z = chunkZ - range; z <= chunkZ + range; ++z) {
				long rvalX = (long)x * rval1;
				long rvalZ = (long)z * rval2;
				this.rand.setSeed(rvalX ^ rvalZ ^ world.getSeed());
				this.recursiveGenerate(world, x, z, chunkX, chunkZ, blocks);
			}
		}

	}

	protected void recursiveGenerate(World world, int chunkX, int chunkZ, int chunkX0, int chunkZ0, short[] blocks) {
	}
}
