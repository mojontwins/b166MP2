package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.biome.BiomeGenBase;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class WorldGenLakes extends WorldGenerator {
	private int blockIndex;

	public WorldGenLakes(int i1) {
		this.blockIndex = i1;
	}

	public boolean generate(World world, Random rand, int x0, int y0, int z0) {
		x0 -= 8;

		// Descend until we hit ground
		for(; y0 > 0 && world.isAirBlock(x0, y0, z0); --y0) {
			;
		}

		if (y0 < 4) return false;

		// Descends 4 blocks into ground.
		y0 -= 4;
		
		// This array represents a slice of this chunk 8 blocks tall (16x8x16).
		boolean[] liquid = new boolean[2048];

		int masses = rand.nextInt(4) + 4;

		for(int i = 0; i < masses; ++i) {
			double wi = rand.nextDouble() * 6.0D + 3.0D;
			double he = rand.nextDouble() * 4.0D + 2.0D;
			double de = rand.nextDouble() * 6.0D + 3.0D;
			double blobX = rand.nextDouble() * (16.0D - wi - 2.0D) + 1.0D + wi / 2.0D;
			double blobY = rand.nextDouble() * (8.0D - he - 4.0D) + 2.0D + he / 2.0D;
			double blobZ = rand.nextDouble() * (16.0D - de - 2.0D) + 1.0D + de / 2.0D;

			for(int x = 1; x < 15; ++x) {
				for(int z = 1; z < 15; ++z) {
					for(int y = 1; y < 7; ++y) {
						double dx = ((double)x - blobX) / (wi / 2.0D);
						double dy = ((double)y - blobY) / (he / 2.0D);
						double dz = ((double)z - blobZ) / (de / 2.0D);
						double dSq = dx * dx + dy * dy + dz * dz;
						if(dSq < 1.0D) {
							liquid[(x * 16 + z) * 8 + y] = true;
							}
						}
					}
				}
			}

		// Check if we can set a new pool:

		int x, y, z;
		boolean z33;
		for(x = 0; x < 16; ++x) {
			for(z = 0; z < 16; ++z) {
				for(y = 0; y < 8; ++y) {
					z33 = 
						// No liquid here but liquid on the right
						!liquid[(x * 16 + z) * 8 + y] && (x < 15 && liquid[((x + 1) * 16 + z) * 8 + y] || 
						// OR liquid on the left
						x > 0 && liquid[((x - 1) * 16 + z) * 8 + y] || 
						// OR liquid behind
						z < 15 && liquid[(x * 16 + z + 1) * 8 + y] || 
						// OR liquid in front
						z > 0 && liquid[(x * 16 + (z - 1)) * 8 + y] || 
						// OR liquid on
						y < 7 && liquid[(x * 16 + z) * 8 + y + 1] || 
						// OR liquid beneath
						y > 0 && liquid[(x * 16 + z) * 8 + (y - 1)]);
					
					if(z33) {
					Material material12 = world.getBlockMaterial(x0 + x, y0 + y, z0 + z);
					if(y >= 4 && material12.isLiquid()) {
							return false;
						}

					if(y < 4 && !material12.isSolid() && world.getBlockId(x0 + x, y0 + y, z0 + z) != this.blockIndex) {
							return false;
						}
					}
				}
			}
		}

		// Draw pool

		for(x = 0; x < 16; ++x) {
			for(z = 0; z < 16; ++z) {
				for(y = 0; y < 8; ++y) {
					if(liquid[(x * 16 + z) * 8 + y]) {
						world.setBlock(x0 + x, y0 + y, z0 + z, y >= 4 ? 0 : this.blockIndex);
					}
				}
			}
		}

		// Add grass

		for(x = 0; x < 16; ++x) {
			for(z = 0; z < 16; ++z) {
				for(y = 4; y < 8; ++y) {
					if(liquid[(x * 16 + z) * 8 + y] && world.getBlockId(x0 + x, y0 + y - 1, z0 + z) == Block.dirt.blockID && world.getSavedLightValue(EnumSkyBlock.Sky, x0 + x, y0 + y, z0 + z) > 0) {
						world.setBlock(x0 + x, y0 + y - 1, z0 + z, Block.grass.blockID);
					}
				}
			}
		}

		if(Block.blocksList[this.blockIndex].blockMaterial == Material.lava) {
			for(x = 0; x < 16; ++x) {
				for(z = 0; z < 16; ++z) {
					for(y = 0; y < 8; ++y) {
						z33 = !liquid[(x * 16 + z) * 8 + y] && (x < 15 && liquid[((x + 1) * 16 + z) * 8 + y] || x > 0 && liquid[((x - 1) * 16 + z) * 8 + y] || z < 15 && liquid[(x * 16 + z + 1) * 8 + y] || z > 0 && liquid[(x * 16 + (z - 1)) * 8 + y] || y < 7 && liquid[(x * 16 + z) * 8 + y + 1] || y > 0 && liquid[(x * 16 + z) * 8 + (y - 1)]);
						if(z33 && (y < 4 || rand.nextInt(2) != 0) && world.getBlockMaterial(x0 + x, y0 + y, z0 + z).isSolid()) {
							world.setBlock(x0 + x, y0 + y, z0 + z, Block.stone.blockID);
						}
					}
				}
			}
		}

		if(Block.blocksList[this.blockIndex].blockMaterial == Material.water) {
			for(x = 0; x < 16; ++x) {
				for(z = 0; z < 16; ++z) {
					BiomeGenBase biomeGen = world.getBiomeGenForCoords(x, z);
					byte b35 = 4;
					if(world.canFreezeWaterDirectly(x0 + x, y0 + b35, z0 + z, biomeGen)) {
						world.setBlock(x0 + x, y0 + b35, z0 + z, Block.ice.blockID);
					}
				}
			}
		}

		return true;		
	}
}
