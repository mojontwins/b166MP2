package net.minecraft.world.level.tile;

public class BlockLogEx2 extends BlockLog implements IBlockWithSubtypes {

	public static final int AlderMetadata = 0;
	public static final int AspenMetadata = 1;
	public static final int EucalyptusMetadata = 2;
	public static final int CherryTreeMetadata = 3;

	public String[] names = new String[] {
			"Alder", "Aspen", "Eucalyptus", "CherryTree"
	};

	public BlockLogEx2(int id) {
		super(id);
	}

	@Override
	public String getNameFromMeta(int meta) {
		return this.names[meta & 3] + "Log";
	}

	@Override
	public int getIndexInTextureFromMeta(int meta) {
		return this.getBlockTextureFromSideAndMetadata(2, meta);
	}
	
	@Override
	public int getTextureSides(int metadata) {
		return 304 + 4 + this.getWoodType(metadata);
	}
	
	@Override
	public int getTextureEnds(int metadata) {
		return 320 + 4 + this.getWoodType(metadata);
	}
}
