package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import com.mojontwins.utils.BlockUtils;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockLog;

public class WorldGenTaiga2 extends WorldGenerator {
	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		int height = rand.nextInt(4) + 6;
		int trunkHeight = 1 + rand.nextInt(2);
		int canopyHeight = height - trunkHeight;
		int canopyWidth = 2 + rand.nextInt(2);

		// Check if it fits in the world

		if (y0 < 1 || y0 + height + 1 >= 256) return false;

		// Check if valid soil

		Block block = world.getBlock(x0, y0 - 1, z0);
		if (block == null || !block.canGrowPlants ()) return false;

		// Check if it fits

		for(int y = y0; y <= y0 + 1 + height; ++y) {
			int radius = (y - y0 < trunkHeight) ? 0 : canopyWidth;

			for(int x = x0 - radius; x <= x0 + radius; ++x) {
				for(int z = z0 - radius; z <= z0 + radius; ++z) {
					if (!BlockUtils.canBeReplacedByLeaves(world.getBlockId(x, y, z))) {
						return false;
					}
				}
			}
		}

		this.setBlock(world, x0, y0 - 1, z0, Block.dirt.blockID);

		int radius = rand.nextInt(2);
		int w = 1;
		int radius0 = 0;

		for(int y = 0; y <= canopyHeight; ++y) {
			int yy = y0 + height - y;

			for(int x = x0 - radius; x <= x0 + radius; ++x) {
				int dx = Math.abs(x - x0);

				for(int z = z0 - radius; z <= z0 + radius; ++z) {
					int dz = Math.abs(z - z0);
					if((dx != radius || dz != radius || radius <= 0) && !Block.opaqueCubeLookup[world.getBlockId(x, yy, z)]) {
						this.setBlockAndMetadata(world, x, yy, z, Block.leaves.blockID, 1);
					}
				}
			}

			if(radius >= w) {
				radius = radius0; radius0 = 1;
				++w; if(w > canopyWidth) {
					w = canopyWidth;
				}
			} else {
				++radius;
			}
		}

		int y = rand.nextInt(3);
		for(int yy = 0; yy < height - y; ++yy) {
			if(BlockUtils.canBeReplacedByWood(world.getBlockId(x0, y0 + y, z0))) {
				this.setBlockAndMetadata(world, x0, y0 + y, z0, Block.wood.blockID, BlockLog.SpruceMetadata);
			}
		}

		return true;
	}
}
