package net.minecraft.world.level.tile;

import java.util.Random;

import net.minecraft.world.level.IBlockAccess;

public class BlockLeavesEx1 extends BlockLeaves implements IBlockWithSubtypes {

	public static final int SwampMetadata = 0;
	public static final int BaobabMetadata = 1;
	public static final int AcaciaMetadata = 2;
	public static final int PineMetadata = 3;
	
	private String[] names = new String[] {
			"Swamp", "Baobab", "Acacia", "Pine"
	};
	
	public BlockLeavesEx1(int blockID) {
		super(blockID, 0);
	}
	
	@Override
	public String getNameFromMeta(int meta) {
		return this.names[meta & 3] + "Leaves";
	}

	@Override
	public int getIndexInTextureFromMeta(int meta) {
		return this.getBlockTextureFromSideAndMetadata(2, meta);
	}

	@Override
	public int getBlockColor() {
		return 0xFFFFFF;
	}
	
	@Override
	public int getRenderColor(int meta) {
		return 0xFFFFFF;
	}
	
	@Override
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		return 0xFFFFFF;
	}
	
	@Override
	public int idDropped(int i1, Random random2, int i3) {
		return Block.saplingEx1.blockID;
	}
	
	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return (BlockLeavesBase.graphicsLevel ? 272 : 288) + (meta & 3);
	}
	
}
