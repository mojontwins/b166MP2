package com.mojontwins.utils;

import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;

public class MetaBlock {
	private final int blockId;
	private final int metadata;
	
	public MetaBlock(int blockId, int metadata) {
		this.blockId = blockId;
		this.metadata = metadata;
	}
	
	public MetaBlock(World world, int x, int y, int z) {
		this(world.getBlockId(x, y, z), world.getBlockMetadata(x, y, z));
	}

	public int blockId() {
		return blockId;
	}
	
	public int metadata() {
		return metadata;
	}
	
	public Block block() {
		return Block.blocksList[this.blockId];
	}
	
	public boolean equals(MetaBlock metaBlock) {
		return this.blockId == metaBlock.blockId && this.metadata == metaBlock.metadata;
	}
}
