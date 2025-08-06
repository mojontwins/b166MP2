package net.minecraft.world.level.levelgen.feature;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class WorldGenShrub extends WorldGenerator {
	private int leavesMeta;
	private int woodMeta;

	public WorldGenShrub(int woodMeta, int leavesMeta) {
		this.woodMeta = woodMeta;
		this.leavesMeta = leavesMeta;
	}

	public boolean generate(World world1, Random random2, int i3, int i4, int i5) {
		int i15;
		for(; ((i15 = world1.getBlockId(i3, i4, i5)) == 0 || i15 == Block.leaves.blockID) && i4 > 0; --i4) {
		}

		int i7 = world1.getBlockId(i3, i4, i5);

		if(i7 == Block.dirt.blockID || i7 == Block.grass.blockID || i7 == Block.snow.blockID) {
			++i4;
			this.setBlockAndMetadata(world1, i3, i4, i5, Block.wood.blockID, this.woodMeta);

			for(int i8 = i4; i8 <= i4 + 2; ++i8) {
				int i9 = i8 - i4;
				int i10 = 2 - i9;

				for(int i11 = i3 - i10; i11 <= i3 + i10; ++i11) {
					int i12 = i11 - i3;

					for(int i13 = i5 - i10; i13 <= i5 + i10; ++i13) {
						int i14 = i13 - i5;
						if((Math.abs(i12) != i10 || Math.abs(i14) != i10 || random2.nextInt(2) != 0) && !Block.opaqueCubeLookup[world1.getBlockId(i11, i8, i13)]) {
							this.setBlockAndMetadata(world1, i11, i8, i13, Block.leaves.blockID, this.leavesMeta);
						}
					}
				}
			}
		}

		return true;
	}
}
