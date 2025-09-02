package net.minecraft.world.level.tile;

public interface IBlockWithSubtypes {
	public String getNameFromMeta(int meta);
	public int getIndexInTextureFromMeta(int meta);
}
