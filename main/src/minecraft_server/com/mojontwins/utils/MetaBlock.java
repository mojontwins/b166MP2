package com.mojontwins.utils;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class MetaBlock {
	private final int blockID;
	private final int metadata;
	
	public MetaBlock(int blockID, int metadata) {
		this.blockID = blockID;
		this.metadata = metadata;
	}
	
	public MetaBlock(World world, int x, int y, int z) {
		this(world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public int blockID() {
		return blockID;
	}
	
	public int metadata() {
		return metadata;
	}
	
	public Block block() {
		return Block.blocksList[this.blockID];
	}
	
	public boolean equals(MetaBlock metaBlock) {
		return this.blockID == metaBlock.blockID && this.metadata == metaBlock.metadata;
	}
}
