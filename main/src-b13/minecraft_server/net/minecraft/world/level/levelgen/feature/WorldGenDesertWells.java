package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class WorldGenDesertWells extends WorldGenerator {
	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		while(world1.isAirBlock(i3, i4, i5) && i4 > 2) {
			--i4;
		}

		int i6 = world1.getBlockId(i3, i4, i5);
		if(i6 != Block.sand.blockID) {
			return false;
		} else {
			int i7;
			int i8;
			for(i7 = -2; i7 <= 2; ++i7) {
				for(i8 = -2; i8 <= 2; ++i8) {
					if(world1.isAirBlock(i3 + i7, i4 - 1, i5 + i8) && world1.isAirBlock(i3 + i7, i4 - 2, i5 + i8)) {
						return false;
					}
				}
			}

			for(i7 = -1; i7 <= 0; ++i7) {
				for(i8 = -2; i8 <= 2; ++i8) {
					for(int i9 = -2; i9 <= 2; ++i9) {
						world1.setBlock(i3 + i8, i4 + i7, i5 + i9, Block.sandStone.blockID);
					}
				}
			}

			world1.setBlock(i3, i4, i5, Block.waterMoving.blockID);
			world1.setBlock(i3 - 1, i4, i5, Block.waterMoving.blockID);
			world1.setBlock(i3 + 1, i4, i5, Block.waterMoving.blockID);
			world1.setBlock(i3, i4, i5 - 1, Block.waterMoving.blockID);
			world1.setBlock(i3, i4, i5 + 1, Block.waterMoving.blockID);

			for(i7 = -2; i7 <= 2; ++i7) {
				for(i8 = -2; i8 <= 2; ++i8) {
					if(i7 == -2 || i7 == 2 || i8 == -2 || i8 == 2) {
						world1.setBlock(i3 + i7, i4 + 1, i5 + i8, Block.sandStone.blockID);
					}
				}
			}

			world1.setBlockAndMetadata(i3 + 2, i4 + 1, i5, Block.stairSingle.blockID, 1);
			world1.setBlockAndMetadata(i3 - 2, i4 + 1, i5, Block.stairSingle.blockID, 1);
			world1.setBlockAndMetadata(i3, i4 + 1, i5 + 2, Block.stairSingle.blockID, 1);
			world1.setBlockAndMetadata(i3, i4 + 1, i5 - 2, Block.stairSingle.blockID, 1);

			for(i7 = -1; i7 <= 1; ++i7) {
				for(i8 = -1; i8 <= 1; ++i8) {
					if(i7 == 0 && i8 == 0) {
						world1.setBlock(i3 + i7, i4 + 4, i5 + i8, Block.sandStone.blockID);
					} else {
						world1.setBlockAndMetadata(i3 + i7, i4 + 4, i5 + i8, Block.stairSingle.blockID, 1);
					}
				}
			}

			for(i7 = 1; i7 <= 3; ++i7) {
				world1.setBlock(i3 - 1, i4 + i7, i5 - 1, Block.sandStone.blockID);
				world1.setBlock(i3 - 1, i4 + i7, i5 + 1, Block.sandStone.blockID);
				world1.setBlock(i3 + 1, i4 + i7, i5 - 1, Block.sandStone.blockID);
				world1.setBlock(i3 + 1, i4 + i7, i5 + 1, Block.sandStone.blockID);
			}

			return true;
		}
	}
}
