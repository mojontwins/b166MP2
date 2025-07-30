package net.minecraft.client.renderer.culling;

import net.minecraft.world.phys.AxisAlignedBB;

public interface ICamera {
	boolean isBoundingBoxInFrustum(AxisAlignedBB axisAlignedBB1);

	void setPosition(double d1, double d3, double d5);
	
	boolean isBoundingBoxInFrustumFully(AxisAlignedBB axisAlignedBB1);

}
