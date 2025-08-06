package com.mojontwins.minecraft.feature;

public abstract class FeatureDynamicSchematicPiece {
	public int posX, posY, posZ;
	public FeatureAABB aabb;
	
	public void setPiece(int x, int y, int z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		
		this.aabb = new FeatureAABB(x, y, z, x + this.getWidth() - 1, y + this.getHeight() - 1, z + this.getLength() - 1);
	}

	public boolean intersectsWith(FeatureDynamicSchematicPiece other) {
		return this.aabb.collidesWith(other.aabb);
	}
	
	public abstract int getWidth();
	
	public abstract int getLength();
	
	public abstract int getHeight();
	
	public abstract void drawPiece(short[][][] space);
}
