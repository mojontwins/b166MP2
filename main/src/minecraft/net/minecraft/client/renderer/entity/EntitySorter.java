package net.minecraft.client.renderer.entity;

import java.util.Comparator;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.world.entity.Entity;

public class EntitySorter implements Comparator<Object> {
	private double entityPosX;
	private double entityPosY;
	private double entityPosZ;

	public EntitySorter(Entity entity1) {
		this.entityPosX = -entity1.posX;
		this.entityPosY = -entity1.posY;
		this.entityPosZ = -entity1.posZ;
	}

	public int sortByDistanceToEntity(WorldRenderer worldRenderer1, WorldRenderer worldRenderer2) {
		double d3 = (double)worldRenderer1.posXPlus + this.entityPosX;
		double d5 = (double)worldRenderer1.posYPlus + this.entityPosY;
		double d7 = (double)worldRenderer1.posZPlus + this.entityPosZ;
		double d9 = (double)worldRenderer2.posXPlus + this.entityPosX;
		double d11 = (double)worldRenderer2.posYPlus + this.entityPosY;
		double d13 = (double)worldRenderer2.posZPlus + this.entityPosZ;
		return (int)((d3 * d3 + d5 * d5 + d7 * d7 - (d9 * d9 + d11 * d11 + d13 * d13)) * 1024.0D);
	}

	public int compare(Object object1, Object object2) {
		return this.sortByDistanceToEntity((WorldRenderer)object1, (WorldRenderer)object2);
	}
}
