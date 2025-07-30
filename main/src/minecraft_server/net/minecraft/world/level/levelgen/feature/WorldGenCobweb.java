package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class WorldGenCobweb {
	public WorldGenCobweb() {
		// TODO Auto-generated constructor stub
	}

	public boolean generate(World world, Random rand, int x, int y, int z) {
		int xx = x; int yy = y; int zz = z;
		for(int i = 0; i < 8; ++i) {
			xx += rand.nextInt(3) - 1;
			yy += rand.nextInt(3) - 1;
			zz += rand.nextInt(3) - 1;
			
			if (world.getBlockId(xx, yy, zz) == 0) {
				world.setBlock(xx, yy, zz, Block.web.blockID);
			}
		}

		return true;
	}
}
