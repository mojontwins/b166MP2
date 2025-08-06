package net.minecraft.world.level.chunk;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPosition;

public class ChunkCoordIntPair {
	public final int chunkXPos;
	public final int chunkZPos;

	public ChunkCoordIntPair(int i1, int i2) {
		this.chunkXPos = i1;
		this.chunkZPos = i2;
	}

	public static long chunkXZ2Int(int i0, int i1) {
		long j2 = (long)i0;
		long j4 = (long)i1;
		return j2 & 4294967295L | (j4 & 4294967295L) << 32;
	}

	public int hashCode() {
		long j1 = chunkXZ2Int(this.chunkXPos, this.chunkZPos);
		int i3 = (int)j1;
		int i4 = (int)(j1 >> 32);
		return i3 ^ i4;
	}

	public boolean equals(Object object1) {
		ChunkCoordIntPair chunkCoordIntPair2 = (ChunkCoordIntPair)object1;
		return chunkCoordIntPair2.chunkXPos == this.chunkXPos && chunkCoordIntPair2.chunkZPos == this.chunkZPos;
	}

	public double s_func_48477_a(Entity entity1) {
		double d2 = (double)(this.chunkXPos * 16 + 8);
		double d4 = (double)(this.chunkZPos * 16 + 8);
		double d6 = d2 - entity1.posX;
		double d8 = d4 - entity1.posZ;
		return d6 * d6 + d8 * d8;
	}

	public int getCenterXPos() {
		return (this.chunkXPos << 4) + 8;
	}

	public int getCenterZPos() {
		return (this.chunkZPos << 4) + 8;
	}

	public ChunkPosition getChunkPosition(int i1) {
		return new ChunkPosition(this.getCenterXPos(), i1, this.getCenterZPos());
	}

	public String toString() {
		return "[" + this.chunkXPos + ", " + this.chunkZPos + "]";
	}
}
