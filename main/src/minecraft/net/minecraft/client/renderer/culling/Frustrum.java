package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AxisAlignedBB;

public class Frustrum implements ICamera {
	private ClippingHelper clippingHelper = ClippingHelperImpl.getInstance();
	private double xPosition;
	private double yPosition;
	private double zPosition;

	public void setPosition(double d1, double d3, double d5) {
		this.xPosition = d1;
		this.yPosition = d3;
		this.zPosition = d5;
	}

	public boolean isBoxInFrustum(double d1, double d3, double d5, double d7, double d9, double d11) {
		return this.clippingHelper.isBoxInFrustum(d1 - this.xPosition, d3 - this.yPosition, d5 - this.zPosition, d7 - this.xPosition, d9 - this.yPosition, d11 - this.zPosition);
	}

	public boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB1) {
		return this.isBoxInFrustum(axisAlignedBB1.minX, axisAlignedBB1.minY, axisAlignedBB1.minZ, axisAlignedBB1.maxX, axisAlignedBB1.maxY, axisAlignedBB1.maxZ);
	}
	
	public boolean isBoxInFrustumFully(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.clippingHelper.isBoxInFrustumFully(minX - this.xPosition, minY - this.yPosition, minZ - this.zPosition, maxX - this.xPosition, maxY - this.yPosition, maxZ - this.zPosition);
	}

	public boolean isBoundingBoxInFrustumFully(AxisAlignedBB aab) {
		return this.isBoxInFrustumFully(aab.minX, aab.minY, aab.minZ, aab.maxX, aab.maxY, aab.maxZ);
	}
}
