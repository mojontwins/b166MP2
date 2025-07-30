package net.minecraft.world.level.tile;

public class BlockIron extends BlockOreStorage {

	public BlockIron(int i1, int i2) {
		super(i1, i2);
	}

	public int getBlockTextureFromSide(int side) {
		if(side == 1) return 13*16+7;
		if(side == 0) return 13*16+11;
		return 13*16+8;
		
	}
}
