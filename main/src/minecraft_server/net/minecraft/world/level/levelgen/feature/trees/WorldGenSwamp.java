package net.minecraft.world.level.levelgen.feature.trees;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenerator;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockLeaves;

public class WorldGenSwamp extends WorldGenerator {
	EnumTreeType tree = EnumTreeType.SWAMP;
	
	private final int leavesID = tree.leaves.getBlock().blockID;
	private final int leavesMeta = tree.leaves.getMetadata();
	private final int trunkID = tree.wood.getBlock().blockID;
	private final int trunkMeta = tree.wood.getMetadata();
	
	public boolean generate(World world, Random rand, int x, int y, int z) {
		int height;
		for(
				height = rand.nextInt(4) + 5; 
				Material.woa(world.getBlockMaterial(x, y - 1, z)); 
				--y) {
		}

		boolean validPlacement = true;
		if(y >= 1 && y + height + 1 <= 128) {
			int blockID;
			int canopyH;
			int canopyD;
			int xx, yy, zz;
			for(blockID = y; blockID <= y + 1 + height; ++blockID) {
				byte b9 = 1;
				if(blockID == y) {
					b9 = 0;
				}

				if(blockID >= y + 1 + height - 2) {
					b9 = 3;
				}

				for(canopyH = x - b9; canopyH <= x + b9 && validPlacement; ++canopyH) {
					for(canopyD = z - b9; canopyD <= z + b9 && validPlacement; ++canopyD) {
						if(blockID >= 0 && blockID < 128) {
							xx = world.getBlockId(canopyH, blockID, canopyD);
							if(xx != 0 && xx != Block.leaves.blockID) {
								if(xx != Block.waterStill.blockID && xx != Block.waterMoving.blockID) {
									validPlacement = false;
								} else if(blockID > y) {
									validPlacement = false;
								}
							}
						} else {
							validPlacement = false;
						}
					}
				}
			}

			if(!validPlacement) {
				return false;
			} else {
				blockID = world.getBlockId(x, y - 1, z);
				if((blockID == Block.grass.blockID || blockID == Block.dirt.blockID) && y < 128 - height - 1) {
					this.setBlock(world, x, y - 1, z, Block.dirt.blockID);

					int dx;
					for(yy = y - 3 + height; yy <= y + height; ++yy) {
						canopyH = yy - (y + height);
						canopyD = 2 - canopyH / 2;

						for(xx = x - canopyD; xx <= x + canopyD; ++xx) {
							dx = xx - x;

							for(zz = z - canopyD; zz <= z + canopyD; ++zz) {
								int dz = zz - z;
								if((Math.abs(dx) != canopyD || Math.abs(dz) != canopyD || rand.nextInt(2) != 0 && canopyH != 0) && !Block.opaqueCubeLookup[world.getBlockId(xx, yy, zz)]) {
									this.setBlockAndMetadata(world, xx, yy, zz, this.leavesID, this.leavesMeta);
								}
							}
						}
					}

					for(yy = 0; yy < height; ++yy) {
						Material m = world.getBlockMaterial(x, y + yy, z);
						if(
								m == Material.air ||
								m == Material.leaves ||
								m == Material.water) {
							this.setBlockAndMetadata(world, x, y + yy, z, this.trunkID, this.trunkMeta);
						}
					}

					for(yy = y - 3 + height; yy <= y + height; ++yy) {
						canopyH = yy - (y + height);
						canopyD = 2 - canopyH / 2;

						for(xx = x - canopyD; xx <= x + canopyD; ++xx) {
							for(zz = z - canopyD; zz <= z + canopyD; ++zz) {
								if(world.getBlock(xx, yy, zz) instanceof BlockLeaves) {
									if(rand.nextInt(4) == 0 && world.getBlockId(xx - 1, yy, zz) == 0) {
										this.generateVines(world, xx - 1, yy, zz, 8);
									}

									if(rand.nextInt(4) == 0 && world.getBlockId(xx + 1, yy, zz) == 0) {
										this.generateVines(world, xx + 1, yy, zz, 2);
									}

									if(rand.nextInt(4) == 0 && world.getBlockId(xx, yy, zz - 1) == 0) {
										this.generateVines(world, xx, yy, zz - 1, 1);
									}

									if(rand.nextInt(4) == 0 && world.getBlockId(xx, yy, zz + 1) == 0) {
										this.generateVines(world, xx, yy, zz + 1, 4);
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

	private void generateVines(World world, int x, int y, int z, int meta) {
		this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, meta);
		int vl = 4;

		while(true) {
			--y;
			if(world.getBlockId(x, y, z) != 0 || vl <= 0) {
				return;
			}

			this.setBlockAndMetadata(world, x, y, z, Block.vine.blockID, meta);
			--vl;
		}
		
	}
}
