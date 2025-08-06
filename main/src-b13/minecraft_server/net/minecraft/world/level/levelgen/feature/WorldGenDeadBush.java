package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockFlower;

public class WorldGenDeadBush extends WorldGenerator {
	private int deadBushID;

	public WorldGenDeadBush(int i1) {
		this.deadBushID = i1;
	}

	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		int i11;
		for(; ((i11 = world1.getBlockId(i3, i4, i5)) == 0 || i11 == Block.leaves.blockID) && i4 > 0; --i4) {
		}

		for(int i7 = 0; i7 < 4; ++i7) {
			int i8 = i3 + random2.nextInt(8) - random2.nextInt(8);
			int i9 = i4 + random2.nextInt(4) - random2.nextInt(4);
			int i10 = i5 + random2.nextInt(8) - random2.nextInt(8);
			if(world1.isAirBlock(i8, i9, i10) && ((BlockFlower)Block.blocksList[this.deadBushID]).canBlockStay(world1, i8, i9, i10)) {
				world1.setBlock(i8, i9, i10, this.deadBushID);
			}
		}

		return true;
	}
}
