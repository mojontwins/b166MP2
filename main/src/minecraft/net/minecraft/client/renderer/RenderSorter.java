package net.minecraft.client.renderer;

import java.util.Comparator;

import net.minecraft.world.entity.EntityLiving;

public class RenderSorter implements Comparator<Object> {
	private EntityLiving baseEntity;

	public RenderSorter(EntityLiving entityLiving1) {
		this.baseEntity = entityLiving1;
	}

	public int doCompare(WorldRenderer worldRenderer1, WorldRenderer worldRenderer2) {
		if(worldRenderer1.isInFrustum && !worldRenderer2.isInFrustum) {
			return 1;
		} else if(worldRenderer2.isInFrustum && !worldRenderer1.isInFrustum) {
			return -1;
		} else {
			double d3 = (double)worldRenderer1.distanceToEntitySquared(this.baseEntity);
			double d5 = (double)worldRenderer2.distanceToEntitySquared(this.baseEntity);
			return d3 < d5 ? 1 : (d3 > d5 ? -1 : (worldRenderer1.chunkIndex < worldRenderer2.chunkIndex ? 1 : -1));
		}
	}

	public int compare(Object object1, Object object2) {
		return this.doCompare((WorldRenderer)object1, (WorldRenderer)object2);
	}
}
