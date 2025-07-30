package com.mojontwins.minecraft.feature;

public class StructureBlockData {
	public int x;
	public int y;
	public int z;
	public int blockID;
	public int blockMetadata;

	public StructureBlockData(int x, int y, int z, int blockID, int blockMetada) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.blockID = blockID;
		this.blockMetadata = blockMetada;
	}
}
