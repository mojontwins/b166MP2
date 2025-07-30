package net.minecraft.world.level.tile;

public class BlockDiamond extends BlockOreStorage {

	public BlockDiamond(int i1, int i2) {
		super(i1, i2);
	}

	public int getBlockTextureFromSide(int side) {
		if(side == 1) return 24;
		if(side == 0) return 13*16+13;
		return 13*16+10;
	}
}
