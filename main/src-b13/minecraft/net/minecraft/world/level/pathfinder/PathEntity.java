package net.minecraft.world.level.pathfinder;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3D;

public class PathEntity {
	public final PathPoint[] points;
	public int currentPathIndex;
	public int pathLength;

	public PathEntity(PathPoint[] pathPoint1) {
		this.points = pathPoint1;
		this.pathLength = pathPoint1.length;
	}

	public void incrementPathIndex() {
		++this.currentPathIndex;
	}

	public boolean isFinished() {
		return this.currentPathIndex >= this.pathLength;
	}

	public PathPoint getFinalPathPoint() {
		return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
	}

	public PathPoint getPathPointFromIndex(int i1) {
		return this.points[i1];
	}

	public int getCurrentPathLength() {
		return this.pathLength;
	}

	public void setCurrentPathLength(int i1) {
		this.pathLength = i1;
	}

	public int getCurrentPathIndex() {
		return this.currentPathIndex;
	}

	public void setCurrentPathIndex(int i1) {
		this.currentPathIndex = i1;
	}

	public Vec3D getVectorFromIndex(Entity entity1, int i2) {
		double d3 = (double)this.points[i2].xCoord + (double)((int)(entity1.width + 1.0F)) * 0.5D;
		double d5 = (double)this.points[i2].yCoord;
		double d7 = (double)this.points[i2].zCoord + (double)((int)(entity1.width + 1.0F)) * 0.5D;
		return Vec3D.createVector(d3, d5, d7);
	}

	public Vec3D getCurrentNodeVec3d(Entity entity1) {
		return this.getVectorFromIndex(entity1, this.currentPathIndex);
	}

	public boolean func_48647_a(PathEntity pathEntity1) {
		if(pathEntity1 == null) {
			return false;
		} else if(pathEntity1.points.length != this.points.length) {
			return false;
		} else {
			for(int i2 = 0; i2 < this.points.length; ++i2) {
				if(this.points[i2].xCoord != pathEntity1.points[i2].xCoord || this.points[i2].yCoord != pathEntity1.points[i2].yCoord || this.points[i2].zCoord != pathEntity1.points[i2].zCoord) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean func_48639_a(Vec3D vec3D1) {
		PathPoint pathPoint2 = this.getFinalPathPoint();
		return pathPoint2 == null ? false : pathPoint2.xCoord == (int)vec3D1.xCoord && pathPoint2.zCoord == (int)vec3D1.zCoord;
	}
}
