package net.minecraft.src;

public class ChunkCoordinates implements Comparable {
	public int posX;
	public int posY;
	public int posZ;

	public ChunkCoordinates() {
	}

	public ChunkCoordinates(int i1, int i2, int i3) {
		this.posX = i1;
		this.posY = i2;
		this.posZ = i3;
	}

	public ChunkCoordinates(ChunkCoordinates chunkCoordinates1) {
		this.posX = chunkCoordinates1.posX;
		this.posY = chunkCoordinates1.posY;
		this.posZ = chunkCoordinates1.posZ;
	}

	public boolean equals(Object object1) {
		if(!(object1 instanceof ChunkCoordinates)) {
			return false;
		} else {
			ChunkCoordinates chunkCoordinates2 = (ChunkCoordinates)object1;
			return this.posX == chunkCoordinates2.posX && this.posY == chunkCoordinates2.posY && this.posZ == chunkCoordinates2.posZ;
		}
	}

	public int hashCode() {
		return this.posX + this.posZ << 8 + this.posY << 16;
	}

	public int a(ChunkCoordinates chunkCoordinates1) {
		return this.posY == chunkCoordinates1.posY ? (this.posZ == chunkCoordinates1.posZ ? this.posX - chunkCoordinates1.posX : this.posZ - chunkCoordinates1.posZ) : this.posY - chunkCoordinates1.posY;
	}

	public double getSqDistanceTo(int i1, int i2, int i3) {
		int i4 = this.posX - i1;
		int i5 = this.posY - i2;
		int i6 = this.posZ - i3;
		return Math.sqrt((double)(i4 * i4 + i5 * i5 + i6 * i6));
	}

	public int compareTo(Object object1) {
		return this.a((ChunkCoordinates)object1);
	}
}
