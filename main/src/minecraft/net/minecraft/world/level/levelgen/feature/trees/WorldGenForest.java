package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import com.mojontwins.utils.BlockUtils;

import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockLeaves;
import net.minecraft.world.level.tile.BlockLog;

public class WorldGenForest extends WorldGenerator {
	public WorldGenForest(boolean notify) {
		super(notify);
	}

	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		int height = rand.nextInt(3) + 5;

		// Tree fits in the world? 

		if (y0 < 1 || y0 >= 256 - height - 1) return false;
		
		// Correct soil?

		Block block = world.getBlock(x0, y0 - 1, z0);
		if (block == null || !block.canGrowPlants()) return false;

		// Check if tree fits
		
		for(int y = y0; y <= y0 + 1 + height; ++y) {

			byte radius = (byte) (y == y0 ? 0 : (y >= y0 + 1 + height - 2 ? 2 : 1));

			for(int x = x0 - radius; x <= x0 + radius; ++x) {
				for(int z = z0 - radius; z <= z0 + radius; ++z) {
					if (!BlockUtils.canBeReplacedByLeaves(world.getBlockId(x, y, z))) {
						return false;
					}
				}
			}
		}

		// Grow tree
	
		this.setBlock(world, x0, y0 - 1, z0, Block.dirt.blockID);

		for(int y = y0 - 3 + height; y <= y0 + height; ++y) {
			int yy = y - (y0 + height);
			int cRadius = 1 - yy / 2;

			for(int x = x0 - cRadius; x <= x0 + cRadius; ++x) {
				int dx = Math.abs(x - x0);

				for(int z = z0 - cRadius; z <= z0 + cRadius; ++z) {
					int dz = Math.abs(z - z0);
					if((dx != cRadius || dz != cRadius || rand.nextInt(2) != 0 && yy != 0) && !Block.opaqueCubeLookup[world.getBlockId(x, y, z)]) {
						this.setBlockAndMetadata(world, x, y, z, Block.leaves.blockID, BlockLeaves.BirchMetadata);
					}
				}
			}
		}

		for(int y = 0; y < height; ++y) {
			if(BlockUtils.canBeReplacedByWood(world.getBlockId(x0, y0 + y, z0))) {
				this.setBlockAndMetadata(world, x0, y0 + y, z0, Block.wood.blockID, BlockLog.BirchMetadata);
			}
		}

		return true;
	}
}
