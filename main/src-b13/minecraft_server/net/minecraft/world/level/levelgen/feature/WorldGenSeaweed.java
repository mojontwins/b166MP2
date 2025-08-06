package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;

public class WorldGenSeaweed extends WorldGenerator {

	public boolean generate(World world, Random random, int x, int y, int z) {
		/*
		for (int i = 0; i < 5; i++) {
			int j = (x + random.nextInt(8)) - random.nextInt(8);
			int k = y;
			int l = (z + random.nextInt(8)) - random.nextInt(8);

			int i1 = 1 + random.nextInt (5);

			for (int j1 = 0; j1 < i1; j1++) {
				if (Block.seaweed.canPlaceBlockAt(world, j, k + j1, l)) {
					world.setBlock(j, k + j1, l,Block.seaweed.blockID);
				}
			}
		}
		*/
		
		return true;
	}
}
