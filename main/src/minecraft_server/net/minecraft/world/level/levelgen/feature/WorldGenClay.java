package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class WorldGenClay extends WorldGenerator {
	private int clayBlockId = Block.blockClay.blockID;
	private int numberOfBlocks;

	public WorldGenClay(int i1) {
		this.numberOfBlocks = i1;
	}

	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		if(world1.getBlockMaterial(i3, i4, i5) != Material.water) {
			return false;
		} else {
			int i6 = random2.nextInt(this.numberOfBlocks - 2) + 2;
			byte b7 = 1;

			for(int i8 = i3 - i6; i8 <= i3 + i6; ++i8) {
				for(int i9 = i5 - i6; i9 <= i5 + i6; ++i9) {
					int i10 = i8 - i3;
					int i11 = i9 - i5;
					if(i10 * i10 + i11 * i11 <= i6 * i6) {
						for(int i12 = i4 - b7; i12 <= i4 + b7; ++i12) {
							int i13 = world1.getBlockId(i8, i12, i9);
							if(i13 == Block.dirt.blockID || i13 == Block.blockClay.blockID) {
								world1.setBlock(i8, i12, i9, this.clayBlockId);
							}
						}
					}
				}
			}

			return true;
		}
	}
}
