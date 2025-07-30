package net.minecraft.world.level.tile;

public class BlockGold extends BlockOreStorage {

	public BlockGold(int i1, int i2) {
		super(i1, i2);
	}

	public int getBlockTextureFromSide(int side) {
		if(side == 1) return 23;
		if(side == 0) return 13*16+12;
		return 13*16+9;
	}
}
