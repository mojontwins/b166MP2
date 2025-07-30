package net.minecraft.src;

import java.util.Random;

public class BlockTallGrass extends BlockFlower {
	protected BlockTallGrass(int i1, int i2) {
		super(i1, i2);
		float f3 = 0.4F;
		this.setBlockBounds(0.5F - f3, 0.0F, 0.5F - f3, 0.5F + f3, 0.8F, 0.5F + f3);
	}

	public int getBlockTextureFromSideAndMetadata(int i1, int i2) {
		return i2 == 1 ? this.blockIndexInTexture : (i2 == 2 ? this.blockIndexInTexture + 16 + 1 : (i2 == 0 ? this.blockIndexInTexture + 16 : this.blockIndexInTexture));
	}

	public int idDropped(int i1, Random random2) {
		return random2.nextInt(8) == 0 ? Item.seeds.shiftedIndex : -1;
	}
}
