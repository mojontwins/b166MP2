package net.minecraft.world.level.tile;

public class BlockSaplingEx2 extends BlockSapling implements IBlockWithSubtypes {

	public String[] names = new String[] {
			"Alder", "Aspen", "Eucalyptus", "CherryTree"
	};

	protected BlockSaplingEx2(int id) {
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
		return 336 + 4 + (meta & 3);
	}	

}
