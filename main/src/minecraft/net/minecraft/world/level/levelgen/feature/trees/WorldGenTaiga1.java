package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import com.mojontwins.utils.BlockUtils;

import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;

public class WorldGenTaiga1 extends WorldGenerator {
	EnumTreeType tree = EnumTreeType.TAIGA;
	
	private final int leavesID = tree.leaves.getBlock().blockID;
	private final int leavesMeta = tree.leaves.getMetadata();
	private final int trunkID = tree.wood.getBlock().blockID;
	private final int trunkMeta = tree.wood.getMetadata();
	
	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		int height = rand.nextInt(5) + 7;
		int trunkHeight = height - rand.nextInt(2) - 3;
		int cHeight = 1 + rand.nextInt(height - trunkHeight + 1);

		// Check if it fits in the world

		if (y0 < 1 || y0 + height + 1 >= 256) return false;

		// Check if valid soil

		Block block = world.getBlock(x0, y0 - 1, z0);
		if (block == null || !block.canGrowPlants ()) return false;

		// Check if it fits

		for(int y = y0; y <= y0 + 1 + height; ++y) {
			int radius = (y - y0 < trunkHeight) ? 0 : cHeight;

			for(int x = x0 - radius; x <= x0 + radius; ++x) {
				for(int z = z0 - radius; z <= z0 + radius; ++z) {
					if (!BlockUtils.canBeReplacedByLeaves(world.getBlockId(x, y, z))) {
						return false;
					}
				}
			}
		}

		this.setBlock(world, x0, y0 - 1, z0, Block.dirt.blockID);
		
		int radius = 0;
		for(int y = y0 + height; y >= y0 + trunkHeight; --y) {
			for(int x = x0 - radius; x <= x0 + radius; ++x) {
				int dx = Math.abs(x - x0);

				for(int z = z0 - radius; z <= z0 + radius; ++z) {
					int dz = Math.abs(z - z0);
					if((dx != radius || dz != radius || radius <= 0) && !Block.opaqueCubeLookup[world.getBlockId(x, y, z)]) {
						this.setBlockAndMetadata(world, x, y, z, this.leavesID, this.leavesMeta);
					}
				}
			}

			if(radius >= 1 && y == y0 + trunkHeight + 1) {
				--radius;
			} else if(radius < cHeight) {
				++radius;
			}
		}

		for(int y = 0; y < height - 1; ++y) {
			if(BlockUtils.canBeReplacedByWood(world.getBlockId(x0, y0 + y, z0))) {
				this.setBlockAndMetadata(world, x0, y0 + y, z0, this.trunkID, this.trunkMeta);
			}
		}

		return true;
	}
}
