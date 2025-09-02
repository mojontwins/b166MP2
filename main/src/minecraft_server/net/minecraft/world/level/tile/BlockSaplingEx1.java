package net.minecraft.world.level.tile;

public class BlockSaplingEx1 extends BlockSapling implements IBlockWithSubtypes {

	private String[] names = new String[] {
			"Swamp", "Baobab", "Acacia", "Pine"
	};
	
	protected BlockSaplingEx1(int id) {
		super(id, 0);
	}

	@Override
	public String getNameFromMeta(int meta) {
		return this.names[meta & 3] + "Sapling";
	}

	@Override
	public int getIndexInTextureFromMeta(int meta) {
		return this.getBlockTextureFromSideAndMetadata(2, meta);
	}

	@Override
	public int getBlockTextureFromSideAndMetadata(int side, int meta) {
		return 336 + (meta & 3);
	}
}
