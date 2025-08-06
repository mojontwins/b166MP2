package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.Direction;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class WorldGenCanopyTree extends WorldGenerator {
	// Code adapted from r1.7.10

	public WorldGenCanopyTree(boolean notify) {
		super(notify);
	}

	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		int height = rand.nextInt(3) + rand.nextInt(2) + 6;
		boolean generate = true;

		if (y0 >= 1 && y0 + height + 1 <= 256) {
			for (int y = y0; y <= y0 + 1 + height; ++y) {
				byte radius = 1;

				if (y == y0) {
					radius = 0;
				}

				if (y >= y0 + 1 + height - 2) {
					radius = 2;
				}

				for (int x = x0 - radius; x <= x0 + radius && generate; ++x) {
					for (int z = z0 - radius; z <= z0 + radius && generate; ++z) {
						if (y >= 0 && y < 256) {
							Block block = Block.blocksList[world.getBlockId(x, y, z)];

							if (!this.canBeReplacedByLeaves(block)) {
								generate = false;
							}
						} else {
							generate = false;
						}
					}
				}
			}

			if (!generate) {
				return false;
			} else {
				Block block = Block.blocksList[world.getBlockId(x0, y0 - 1, z0)];
				if (block == null)
					return false;

				if (block.canGrowPlants() && y0 < 256 - height - 1) {
					this.setBlock(world, x0, y0 - 1, z0, Block.dirt.blockID);
					this.setBlock(world, x0 + 1, y0 - 1, z0, Block.dirt.blockID);
					this.setBlock(world, x0 + 1, y0 - 1, z0 + 1, Block.dirt.blockID);
					this.setBlock(world, x0, y0 - 1, z0 + 1, Block.dirt.blockID);
					int var21 = rand.nextInt(4);
					int trunkLength = height - rand.nextInt(4);
					int var11 = 2 - rand.nextInt(3);
					int x = x0;
					int z = z0;
					int var14 = 0;
					int var15;
					int var16;

					for (var15 = 0; var15 < height; ++var15) {
						var16 = y0 + var15;

						if (var15 >= trunkLength && var11 > 0) {
							x += Direction.offsetX[var21];
							z += Direction.offsetZ[var21];
							--var11;
						}

						Block var17 = Block.blocksList[world.getBlockId(x, var16, z)];

						if (var17 == null || var17.blockMaterial == Material.air || var17.blockMaterial == Material.leaves) {
							this.setBlockAndMetadata(world, x, var16, z, Block.wood.blockID, 1);
							this.setBlockAndMetadata(world, x + 1, var16, z, Block.wood.blockID, 1);
							this.setBlockAndMetadata(world, x, var16, z + 1, Block.wood.blockID, 1);
							this.setBlockAndMetadata(world, x + 1, var16, z + 1, Block.wood.blockID, 1);
							var14 = var16;
						}
					}

					for (var15 = -2; var15 <= 0; ++var15) {
						for (var16 = -2; var16 <= 0; ++var16) {
							byte var23 = -1;
							this.setLeaves(world, x + var15, var14 + var23, z + var16);
							this.setLeaves(world, 1 + x - var15, var14 + var23, z + var16);
							this.setLeaves(world, x + var15, var14 + var23, 1 + z - var16);
							this.setLeaves(world, 1 + x - var15, var14 + var23, 1 + z - var16);

							if ((var15 > -2 || var16 > -1) && (var15 != -1 || var16 != -2)) {
								byte var24 = 1;
								this.setLeaves(world, x + var15, var14 + var24, z + var16);
								this.setLeaves(world, 1 + x - var15, var14 + var24, z + var16);
								this.setLeaves(world, x + var15, var14 + var24, 1 + z - var16);
								this.setLeaves(world, 1 + x - var15, var14 + var24, 1 + z - var16);
							}
						}
					}

					if (rand.nextBoolean()) {
						this.setLeaves(world, x, var14 + 2, z);
						this.setLeaves(world, x + 1, var14 + 2, z);
						this.setLeaves(world, x + 1, var14 + 2, z + 1);
						this.setLeaves(world, x, var14 + 2, z + 1);
					}

					for (var15 = -3; var15 <= 4; ++var15) {
						for (var16 = -3; var16 <= 4; ++var16) {
							if ((var15 != -3 || var16 != -3) && (var15 != -3 || var16 != 4)
									&& (var15 != 4 || var16 != -3) && (var15 != 4 || var16 != 4)
									&& (Math.abs(var15) < 3 || Math.abs(var16) < 3)) {
								this.setLeaves(world, x + var15, var14, z + var16);
							}
						}
					}

					for (var15 = -1; var15 <= 2; ++var15) {
						for (var16 = -1; var16 <= 2; ++var16) {
							if ((var15 < 0 || var15 > 1 || var16 < 0 || var16 > 1) && rand.nextInt(3) <= 0) {
								int var25 = rand.nextInt(3) + 2;
								int var18;

								for (var18 = 0; var18 < var25; ++var18) {
									this.setBlockAndMetadata(world, x0 + var15, var14 - var18 - 1, z0 + var16,
											Block.wood.blockID, 1);
								}

								int var19;

								for (var18 = -1; var18 <= 1; ++var18) {
									for (var19 = -1; var19 <= 1; ++var19) {
										this.setLeaves(world, x + var15 + var18, var14 - 0, z + var16 + var19);
									}
								}

								for (var18 = -2; var18 <= 2; ++var18) {
									for (var19 = -2; var19 <= 2; ++var19) {
										if (Math.abs(var18) != 2 || Math.abs(var19) != 2) {
											this.setLeaves(world, x + var15 + var18, var14 - 1, z + var16 + var19);
										}
									}
								}
							}
						}
					}

					return true;
				} else {
					return false;
				}
			}
		} else {
			return false;
		}
	}

	private void setLeaves(World world, int x, int y, int z) {
		Block block = Block.blocksList[world.getBlockId(x, y, z)];

		if (block == null || block.blockMaterial == Material.air) {
			this.setBlockAndMetadata(world, x, y, z, Block.leaves.blockID, 1);
		}
	}

}
