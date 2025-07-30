package net.minecraft.world.level;

import net.minecraft.world.level.tile.Block;

public class BlockState {

	private final Block block;
	private final int metadata;
	private int x, y, z;
	
	public BlockState(Block block, int metadata) {
		this.block = block;
		this.metadata = metadata;
	}
	
	public BlockState(int blockID, int metadata) {
		this(Block.blocksList[blockID], metadata);
	}
	
	public BlockState(int blockID, int metadata, int x, int y, int z) {
		this(blockID, metadata);
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}

	public Block getBlock() {
		return block;
	}

	public int getMetadata() {
		return metadata;
	}

	@Override
	public boolean equals(Object blockState) {
		if(!(blockState instanceof BlockState)) return false;
		return this.block == ((BlockState)blockState).getBlock() && this.metadata == ((BlockState)blockState).getMetadata();
	}

	public boolean isBlock(int blockID) {
		int thisBlockID = this.block == null ? 0 : this.block.blockID;
		return thisBlockID == blockID;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public String toString() {
		if (this.block == null) return "Air";
		return this.block.getBlockName() + "(" + this.block.blockID + "):" + this.metadata;
	}

	public int getBlockID() {
		if(this.block == null) return 0;
		else return this.block.blockID;
	}
}
