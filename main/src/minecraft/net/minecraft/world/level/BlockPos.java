package net.minecraft.world.level;

import net.minecraft.world.Direction;

public class BlockPos {
	public int x;
	public int y;
	public int z;
	
	public BlockPos() {	
	}

	public BlockPos set(int x, int y, int z) {
		this.x = x; this.y = y; this.z = z;
		return this;
	}
	
	public BlockPos move(int direction) {
		this.x += Direction.offsetX[direction];
		this.z += Direction.offsetZ[direction];
		this.y += Direction.offsetY[direction];
		return this;
	}

	public BlockPos move(int direction, int amount) {
		this.x += Direction.offsetX[direction] * amount;
		this.z += Direction.offsetZ[direction] * amount;
		this.y += Direction.offsetY[direction] * amount;
		return this;
	}
	
	public BlockPos copy() {
		return new BlockPos().set(this);
	}

	public BlockPos set(BlockPos blockPos) {
		return this.set(blockPos.x, blockPos.y, blockPos.z);
	}

	public BlockPos rotateHorzFrom(BlockPos origin, boolean rotated) {
		return this.rotateHorzFrom(origin.x, origin.x, rotated);
	}
		
	public BlockPos rotateHorzFrom(int x, int z, boolean rotated) {		
		BlockPos blockPos = new BlockPos();
		if(rotated) {
			return blockPos.set(
					x + this.z - z,
					this.y,
					z + this.x - x
			);
		} else {
			return blockPos.set(this);
		}
	}
	
	public String toString() {
		return "(" + this.x +", " + this.y + ", " + this.z + ")";
	}

}
