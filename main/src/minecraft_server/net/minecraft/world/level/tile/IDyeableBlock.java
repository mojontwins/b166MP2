package net.minecraft.world.level.tile;

public interface IDyeableBlock {
	public int getMetadataFromDye(int dye);
	public int getDyeFromMetadata(int meta);
	public int[] getTints();
}
