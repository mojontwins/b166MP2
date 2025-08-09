package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import com.mojontwins.utils.BlockUtils;

import net.minecraft.world.level.BlockState;
import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.tile.Block;

public class WorldGenTrees extends WorldGenerator {
	private final int addedHeight;
	private final boolean hasVines;
	private final int woodMeta;
	private final int leavesMeta;
	private BlockState fruitBlock = null;
	private int fruitChance = 8;

	public WorldGenTrees(boolean notify) {
		this(notify, 4, 0, 0, false);
	}

	public WorldGenTrees(boolean notify, int addedHeight, int woodMeta, int leavesMeta, boolean withVines) {
		super(notify);
		this.addedHeight = addedHeight;
		this.woodMeta = woodMeta;
		this.leavesMeta = leavesMeta;
		this.hasVines = withVines;
	}
	
	public WorldGenerator withFruit(BlockState fruitBlock, int chance) {
		this.fruitBlock = fruitBlock;
		this.fruitChance = chance;
		return this;
	}

	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		int height = rand.nextInt(3) + this.addedHeight;

		// Check if it fits in the world

		if (y0 < 1 || y0 + height + 1 >= 256) return false;

		// Check if valid soil

		Block block = world.getBlock(x0, y0 - 1, z0);
		if (block == null || !block.canGrowPlants ()) return false;

		// Check if it fits

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

		this.setBlock(world, x0, y0 - 1, z0, Block.dirt.blockID);
		int canopy = 3;
		byte radiusBase = 0;

		int radius;
		for(int y = y0 - canopy + height; y <= y0 + height; ++y) {
			int yy = y - (y0 + height);
			radius = radiusBase + 1 - yy / 2;

			for(int x = x0 - radius; x <= x0 + radius; ++x) {
				int dx = Math.abs(x - x0);

				for(int z = z0 - radius; z <= z0 + radius; ++z) {
					int dz = Math.abs(z - z0);
					if((dx != radius || Math.abs(dz) != radius || rand.nextInt(2) != 0 && yy != 0) && !Block.opaqueCubeLookup[world.getBlockId(x, y, z)]) {
						this.setBlockAndMetadata(world, x, y, z, Block.leaves.blockID, this.leavesMeta);
						if(
								this.fruitBlock != null &&
								rand.nextInt(this.fruitChance) == 0 &&
								this.fruitBlock.getBlock().canBlockStay(world, x, y - 1, z)
						) {
							this.setBlockAndMetadata(world, x, y - 1, z, this.fruitBlock.getBlockID(), this.fruitBlock.getMetadata());
						}
					}
				}
			}
		}

		for(int y = 0; y < height; ++y) {
			if(BlockUtils.canBeReplacedByWood(world.getBlockId(x0, y0 + y, z0))) {
				this.setBlockAndMetadata(world, x0, y0 + y, z0, Block.wood.blockID, this.woodMeta);
				
				if(this.hasVines && y > 0) {
					if(rand.nextInt(3) > 0 && world.isAirBlock(x0 - 1, y0 + y, z0)) {
						this.setBlockAndMetadata(world, x0 - 1, y0 + y, z0, Block.vine.blockID, 8);
					}

					if(rand.nextInt(3) > 0 && world.isAirBlock(x0 + 1, y0 + y, z0)) {
						this.setBlockAndMetadata(world, x0 + 1, y0 + y, z0, Block.vine.blockID, 2);
					}

					if(rand.nextInt(3) > 0 && world.isAirBlock(x0, y0 + y, z0 - 1)) {
						this.setBlockAndMetadata(world, x0, y0 + y, z0 - 1, Block.vine.blockID, 1);
					}

					if(rand.nextInt(3) > 0 && world.isAirBlock(x0, y0 + y, z0 + 1)) {
						this.setBlockAndMetadata(world, x0, y0 + y, z0 + 1, Block.vine.blockID, 4);
					}
				}
			}
		}

		if(this.hasVines) {
			for(int y = y0 - 3 + height; y <= y0 + height; ++y) {
				int yy = y - (y0 + height);
				radius = 2 - yy / 2;

				for(int x = x0 - radius; x <= x0 + radius; ++x) {
					for(int z = z0 - radius; z <= z0 + radius; ++z) {
						if(world.getBlockId(x, y, z) == Block.leaves.blockID) {
							if(rand.nextInt(4) == 0 && world.getBlockId(x - 1, y, z) == 0) {
								this.growVines(world, x - 1, y, z, 8);
							}

							if(rand.nextInt(4) == 0 && world.getBlockId(x + 1, y, z) == 0) {
								this.growVines(world, x + 1, y, z, 2);
							}

							if(rand.nextInt(4) == 0 && world.getBlockId(x, y, z - 1) == 0) {
								this.growVines(world, x, y, z - 1, 1);
							}

							if(rand.nextInt(4) == 0 && world.getBlockId(x, y, z + 1) == 0) {
								this.growVines(world, x, y, z + 1, 4);
							}
						}
					}
				}
			}
		}

		return true;
	}

	private void growVines(World world1, int addedHeight, int woodMeta, int leavesMeta, int i5) {
		this.setBlockAndMetadata(world1, addedHeight, woodMeta, leavesMeta, Block.vine.blockID, i5);
		int i6 = 4;

		while(true) {
			--woodMeta;
			if(world1.getBlockId(addedHeight, woodMeta, leavesMeta) != 0 || i6 <= 0) {
				return;
			}

			this.setBlockAndMetadata(world1, addedHeight, woodMeta, leavesMeta, Block.vine.blockID, i5);
			--i6;
		}
	}
}
