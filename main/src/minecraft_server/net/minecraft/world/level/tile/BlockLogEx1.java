package net.minecraft.world.level.tile;

public class BlockLogEx1 extends BlockLog implements IBlockWithSubtypes {

	public static final int SwampMetadata = 0;
	public static final int BaobabMetadata = 1;
	public static final int AcaciaMetadata = 2;
	public static final int PineMetadata = 3;
	
	private String[] names = new String[] {
			"Swamp", "Baobab", "Acacia", "Pine"
	};

	public BlockLogEx1(int id) {
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
		return 304 + this.getWoodType(metadata);
	}
	
	@Override
	public int getTextureEnds(int metadata) {
		return 320 + this.getWoodType(metadata);
	}

}
