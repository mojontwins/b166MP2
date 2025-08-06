package com.mojontwins.minecraft.feature;

public class FeatureAABB {
	public int x1, y1, z1, x2, y2, z2;
	
	public FeatureAABB(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}

	public boolean collidesWith(FeatureAABB other) {
		return (
				this.x1 <= other.x2 && this.x2 >= other.x1 && 
				this.y1 <= other.y2 && this.y2 >= other.y1 &&
				this.z1 <= other.z2 && this.z2 >= other.z1
		);
	}
	
	public boolean containsFully(FeatureAABB other) {
		return (
				this.x1 <= other.x1 && this.x2 >= other.x2 &&
				this.y1 <= other.y1 && this.y2 >= other.y2 &&
				this.z1 <= other.z1 && this.z2 >= other.z2
		);
	}
	
	public String toString() {
		return "(" + this.x1 + ", " + this.y1 + ", " + this.z1 + ") -> (" + this.x2 + ", " + this.y2 + ", " + this.z2 + ")";
	}
	
}
