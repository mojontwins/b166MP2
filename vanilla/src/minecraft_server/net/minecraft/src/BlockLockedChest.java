package net.minecraft.src;

import java.util.Random;

public class BlockLockedChest extends Block {
	protected BlockLockedChest(int i1) {
		super(i1, Material.wood);
		this.blockIndexInTexture = 26;
	}

	public int getBlockTextureFromSide(int i1) {
		return i1 == 1 ? this.blockIndexInTexture - 1 : (i1 == 0 ? this.blockIndexInTexture - 1 : (i1 == 3 ? this.blockIndexInTexture + 1 : this.blockIndexInTexture));
	}

	public boolean canPlaceBlockAt(World world1, int i2, int i3, int i4) {
		return true;
	}

	public void updateTick(World world1, int i2, int i3, int i4, Random random5) {
		world1.setBlockWithNotify(i2, i3, i4, 0);
	}
}
