package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class WorldGenHellLava extends WorldGenerator {
	private int hellLavaID;

	public WorldGenHellLava(int i1) {
		this.hellLavaID = i1;
	}

	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		if(world1.getBlockId(i3, i4 + 1, i5) != Block.netherrack.blockID) {
			return false;
		} else if(world1.getBlockId(i3, i4, i5) != 0 && world1.getBlockId(i3, i4, i5) != Block.netherrack.blockID) {
			return false;
		} else {
			int i6 = 0;
			if(world1.getBlockId(i3 - 1, i4, i5) == Block.netherrack.blockID) {
				++i6;
			}

			if(world1.getBlockId(i3 + 1, i4, i5) == Block.netherrack.blockID) {
				++i6;
			}

			if(world1.getBlockId(i3, i4, i5 - 1) == Block.netherrack.blockID) {
				++i6;
			}

			if(world1.getBlockId(i3, i4, i5 + 1) == Block.netherrack.blockID) {
				++i6;
			}

			if(world1.getBlockId(i3, i4 - 1, i5) == Block.netherrack.blockID) {
				++i6;
			}

			int i7 = 0;
			if(world1.isAirBlock(i3 - 1, i4, i5)) {
				++i7;
			}

			if(world1.isAirBlock(i3 + 1, i4, i5)) {
				++i7;
			}

			if(world1.isAirBlock(i3, i4, i5 - 1)) {
				++i7;
			}

			if(world1.isAirBlock(i3, i4, i5 + 1)) {
				++i7;
			}

			if(world1.isAirBlock(i3, i4 - 1, i5)) {
				++i7;
			}

			if(i6 == 4 && i7 == 1) {
				world1.setBlockWithNotify(i3, i4, i5, this.hellLavaID);
				world1.scheduledUpdatesAreImmediate = true;
				Block.blocksList[this.hellLavaID].updateTick(world1, i3, i4, i5, random2);
				world1.scheduledUpdatesAreImmediate = false;
			}

			return true;
		}
	}
}
