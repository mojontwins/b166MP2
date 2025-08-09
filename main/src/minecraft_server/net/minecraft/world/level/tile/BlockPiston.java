package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class BlockPiston extends Block {
	public static boolean resetBase = true;

	public BlockPiston(int i1, int i2, boolean z3) {
		super(i1, Material.iron);
		this.blockIndexInTexture = i2;
	}

	public int quantityDropped(Random random1) {
		return 0;
	}

	public void onBlockRemoval(World world1, int i2, int i3, int i4) {
		if(resetBase) {
			int i5 = world1.getBlockMetadata(i2, i3, i4);
			int i6 = i2;
			int i7 = i3;
			int i8 = i4;
			if(i5 == 0) {
				i7 = i3 + 1;
			}

			if(i5 == 1) {
				--i7;
			}

			if(i5 == 2) {
				i8 = i4 + 1;
			}

			if(i5 == 3) {
				--i8;
			}

			if(i5 == 4) {
				i6 = i2 + 1;
			}

			if(i5 == 5) {
				--i6;
			}

			world1.setBlockMetadataWithNotify(i6, i7, i8, world1.getBlockMetadata(i6, i7, i8) & 7);
		}
	}

	public void onNeighborBlockChange(World world1, int i2, int i3, int i4, int i5) {
		if(i5 == 0) {
			int i6 = world1.getBlockMetadata(i2, i3, i4);
			int i7 = i2;
			int i8 = i3;
			int i9 = i4;
			if(i6 == 0) {
				i8 = i3 + 1;
			}

			if(i6 == 1) {
				--i8;
			}

			if(i6 == 2) {
				i9 = i4 + 1;
			}

			if(i6 == 3) {
				--i9;
			}

			if(i6 == 4) {
				i7 = i2 + 1;
			}

			if(i6 == 5) {
				--i7;
			}

			if(world1.getBlockId(i7, i8, i9) == 0) {
				world1.setBlockWithNotify(i2, i3, i4, 0);
			}

		}
	}

	public boolean isOpaqueCube() {
		return false;
	}

	public boolean renderAsNormalBlock() {
		return false;
	}

	public int getRenderType() {
		return 109;
	}
}
