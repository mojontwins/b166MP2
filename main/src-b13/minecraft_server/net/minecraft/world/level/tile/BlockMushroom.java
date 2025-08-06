package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.levelgen.feature.WorldGenBigMushroom;

public class BlockMushroom extends BlockFlower {
	protected BlockMushroom(int i1, int i2) {
		super(i1, i2);
		float f3 = 0.2F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, f3 * 2.0F, 0.5F + f3);
		this.setTickRandomly(true);
	}

	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(rand.nextInt(25) == 0) {
			byte range = 4;
			int sameBlockCount = 5;

			int xx;
			int yy;
			int zz;

			// Don't spread if too many surrounding
			for(xx = x - range; xx <= x + range; ++xx) {
				for(yy = z - range; yy <= z + range; ++yy) {
					for(zz = y - 1; zz <= y + 1; ++zz) {
						if(world.getBlockId(xx, zz, yy) == this.blockID) {
							--sameBlockCount;
							if(sameBlockCount <= 0) {
								return;
							}
						}
					}
				}
			}

			// Pîck random surrounding location
			xx = x + rand.nextInt(3) - 1;
			yy = y + rand.nextInt(2) - rand.nextInt(2);
			zz = z + rand.nextInt(3) - 1;

			for(int attempts = 0; attempts < 4; ++attempts) {
				if(world.isAirBlock(xx, yy, zz) && this.canBlockStay(world, xx, yy, zz)) {
					x = xx;
					y = yy;
					z = zz;
				}

				xx = x + rand.nextInt(3) - 1;
				yy = y + rand.nextInt(2) - rand.nextInt(2);
				zz = z + rand.nextInt(3) - 1;
			}

			if(world.isAirBlock(xx, yy, zz) && this.canBlockStay(world, xx, yy, zz)) {
				world.setBlockWithNotify(xx, yy, zz, this.blockID);
			}
		}

	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return super.canPlaceBlockAt(world1, i2, i3, i4) && this.canBlockStay(world1, i2, i3, i4);
	}

	protected boolean canThisPlantGrowOnThisBlockID(int i1) {
		Block block = Block.blocksList[i1];
		return block != null && block.canGrowMushrooms();
	}

	public boolean canBlockStay(World world1, int i2, int i3, int i4) {
		if(i3 >= 0 && i3 < 256) {
			int i5 = world1.getBlockId(i2, i3 - 1, i4);
			return /*i5 == Block.mycelium.blockID ||*/ world1.getFullBlockLightValue(i2, i3, i4) < 13 && this.canThisPlantGrowOnThisBlockID(i5);
		} else {
			return false;
		}
	}

	public boolean fertilizeMushroom(World world1, int i2, int i3, int i4, Random random5) {
		int i6 = world1.getBlockMetadata(i2, i3, i4);
		world1.setBlock(i2, i3, i4, 0);
		WorldGenBigMushroom worldGenBigMushroom7 = null;
		if(this.blockID == Block.mushroomBrown.blockID) {
			worldGenBigMushroom7 = new WorldGenBigMushroom(0);
		} else if(this.blockID == Block.mushroomRed.blockID) {
			worldGenBigMushroom7 = new WorldGenBigMushroom(1);
		}

		if(worldGenBigMushroom7 != null && worldGenBigMushroom7.generate(world1, random5, i2, i3, i4)) {
			return true;
		} else {
			world1.setBlockAndMetadata(i2, i3, i4, this.blockID, i6);
			return false;
		}
	}
}
