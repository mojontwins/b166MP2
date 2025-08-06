package net.minecraft.world.level.pathfinder;

import net.minecraft.src.MathHelper;
import net.minecraft.world.IntHashMap;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.IBlockAccess;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class PathFinder {
	private IBlockAccess worldMap;
	private Path path = new Path();
	private IntHashMap pointMap = new IntHashMap();
	private PathPoint[] pathOptions = new PathPoint[32];
	private boolean isWoddenDoorAllowed;
	private boolean isMovementBlockAllowed;
	private boolean isPathingInWater;
	private boolean canEntityDrown;

	public PathFinder(IBlockAccess iBlockAccess1, boolean z2, boolean z3, boolean z4, boolean z5) {
		this.worldMap = iBlockAccess1;
		this.isWoddenDoorAllowed = z2;
		this.isMovementBlockAllowed = z3;
		this.isPathingInWater = z4;
		this.canEntityDrown = z5;
	}

	public PathEntity createEntityPathTo(Entity entity1, Entity entity2, float f3) {
		return this.createEntityPathTo(entity1, entity2.posX, entity2.boundingBox.minY, entity2.posZ, f3);
	}

	public PathEntity createEntityPathTo(Entity entity1, int i2, int i3, int i4, float f5) {
		return this.createEntityPathTo(entity1, (double)((float)i2 + 0.5F), (double)((float)i3 + 0.5F), (double)((float)i4 + 0.5F), f5);
	}

	private PathEntity createEntityPathTo(Entity entity1, double d2, double d4, double d6, float f8) {
		this.path.clearPath();
		this.pointMap.clearMap();
		boolean z9 = this.isPathingInWater;
		int i10 = MathHelper.floor_double(entity1.boundingBox.minY + 0.5D);
		if(this.canEntityDrown && entity1.isInWater()) {
			i10 = (int)entity1.boundingBox.minY;

			for(int i11 = this.worldMap.getBlockId(MathHelper.floor_double(entity1.posX), i10, MathHelper.floor_double(entity1.posZ)); i11 == Block.waterMoving.blockID || i11 == Block.waterStill.blockID; i11 = this.worldMap.getBlockId(MathHelper.floor_double(entity1.posX), i10, MathHelper.floor_double(entity1.posZ))) {
				++i10;
			}

			z9 = this.isPathingInWater;
			this.isPathingInWater = false;
		} else {
			i10 = MathHelper.floor_double(entity1.boundingBox.minY + 0.5D);
		}

		PathPoint pathPoint15 = this.openPoint(MathHelper.floor_double(entity1.boundingBox.minX), i10, MathHelper.floor_double(entity1.boundingBox.minZ));
		PathPoint pathPoint12 = this.openPoint(MathHelper.floor_double(d2 - (double)(entity1.width / 2.0F)), MathHelper.floor_double(d4), MathHelper.floor_double(d6 - (double)(entity1.width / 2.0F)));
		PathPoint pathPoint13 = new PathPoint(MathHelper.floor_float(entity1.width + 1.0F), MathHelper.floor_float(entity1.height + 1.0F), MathHelper.floor_float(entity1.width + 1.0F));
		PathEntity pathEntity14 = this.addToPath(entity1, pathPoint15, pathPoint12, pathPoint13, f8);
		this.isPathingInWater = z9;
		return pathEntity14;
	}

	private PathEntity addToPath(Entity entity1, PathPoint pathPoint2, PathPoint pathPoint3, PathPoint pathPoint4, float f5) {
		pathPoint2.totalPathDistance = 0.0F;
		pathPoint2.distanceToNext = pathPoint2.distanceTo(pathPoint3);
		pathPoint2.distanceToTarget = pathPoint2.distanceToNext;
		this.path.clearPath();
		this.path.addPoint(pathPoint2);
		PathPoint pathPoint6 = pathPoint2;

		while(!this.path.isPathEmpty()) {
			PathPoint pathPoint7 = this.path.dequeue();
			if(pathPoint7.equals(pathPoint3)) {
				return this.createEntityPath(pathPoint2, pathPoint3);
			}

			if(pathPoint7.distanceTo(pathPoint3) < pathPoint6.distanceTo(pathPoint3)) {
				pathPoint6 = pathPoint7;
			}

			pathPoint7.isFirst = true;
			int i8 = this.findPathOptions(entity1, pathPoint7, pathPoint4, pathPoint3, f5);

			for(int i9 = 0; i9 < i8; ++i9) {
				PathPoint pathPoint10 = this.pathOptions[i9];
				float f11 = pathPoint7.totalPathDistance + pathPoint7.distanceTo(pathPoint10);
				if(!pathPoint10.isAssigned() || f11 < pathPoint10.totalPathDistance) {
					pathPoint10.previous = pathPoint7;
					pathPoint10.totalPathDistance = f11;
					pathPoint10.distanceToNext = pathPoint10.distanceTo(pathPoint3);
					if(pathPoint10.isAssigned()) {
						this.path.changeDistance(pathPoint10, pathPoint10.totalPathDistance + pathPoint10.distanceToNext);
					} else {
						pathPoint10.distanceToTarget = pathPoint10.totalPathDistance + pathPoint10.distanceToNext;
						this.path.addPoint(pathPoint10);
					}
				}
			}
		}

		if(pathPoint6 == pathPoint2) {
			return null;
		} else {
			return this.createEntityPath(pathPoint2, pathPoint6);
		}
	}

	private int findPathOptions(Entity entity1, PathPoint pathPoint2, PathPoint pathPoint3, PathPoint pathPoint4, float f5) {
		int i6 = 0;
		byte b7 = 0;
		if(this.getVerticalOffset(entity1, pathPoint2.xCoord, pathPoint2.yCoord + 1, pathPoint2.zCoord, pathPoint3) == 1) {
			b7 = 1;
		}

		PathPoint pathPoint8 = this.getSafePoint(entity1, pathPoint2.xCoord, pathPoint2.yCoord, pathPoint2.zCoord + 1, pathPoint3, b7);
		PathPoint pathPoint9 = this.getSafePoint(entity1, pathPoint2.xCoord - 1, pathPoint2.yCoord, pathPoint2.zCoord, pathPoint3, b7);
		PathPoint pathPoint10 = this.getSafePoint(entity1, pathPoint2.xCoord + 1, pathPoint2.yCoord, pathPoint2.zCoord, pathPoint3, b7);
		PathPoint pathPoint11 = this.getSafePoint(entity1, pathPoint2.xCoord, pathPoint2.yCoord, pathPoint2.zCoord - 1, pathPoint3, b7);
		if(pathPoint8 != null && !pathPoint8.isFirst && pathPoint8.distanceTo(pathPoint4) < f5) {
			this.pathOptions[i6++] = pathPoint8;
		}

		if(pathPoint9 != null && !pathPoint9.isFirst && pathPoint9.distanceTo(pathPoint4) < f5) {
			this.pathOptions[i6++] = pathPoint9;
		}

		if(pathPoint10 != null && !pathPoint10.isFirst && pathPoint10.distanceTo(pathPoint4) < f5) {
			this.pathOptions[i6++] = pathPoint10;
		}

		if(pathPoint11 != null && !pathPoint11.isFirst && pathPoint11.distanceTo(pathPoint4) < f5) {
			this.pathOptions[i6++] = pathPoint11;
		}

		return i6;
	}

	private PathPoint getSafePoint(Entity entity1, int i2, int i3, int i4, PathPoint pathPoint5, int i6) {
		PathPoint pathPoint7 = null;
		int i8 = this.getVerticalOffset(entity1, i2, i3, i4, pathPoint5);
		if(i8 == 2) {
			return this.openPoint(i2, i3, i4);
		} else {
			if(i8 == 1) {
				pathPoint7 = this.openPoint(i2, i3, i4);
			}

			if(pathPoint7 == null && i6 > 0 && i8 != -3 && i8 != -4 && this.getVerticalOffset(entity1, i2, i3 + i6, i4, pathPoint5) == 1) {
				pathPoint7 = this.openPoint(i2, i3 + i6, i4);
				i3 += i6;
			}

			if(pathPoint7 != null) {
				int i9 = 0;
				int i10 = 0;

				while(i3 > 0) {
					i10 = this.getVerticalOffset(entity1, i2, i3 - 1, i4, pathPoint5);
					if(this.isPathingInWater && i10 == -1) {
						return null;
					}

					if(i10 != 1) {
						break;
					}

					++i9;
					if(i9 >= 4) {
						return null;
					}

					--i3;
					if(i3 > 0) {
						pathPoint7 = this.openPoint(i2, i3, i4);
					}
				}

				if(i10 == -2) {
					return null;
				}
			}

			return pathPoint7;
		}
	}

	private final PathPoint openPoint(int i1, int i2, int i3) {
		int i4 = PathPoint.makeHash(i1, i2, i3);
		PathPoint pathPoint5 = (PathPoint)this.pointMap.lookup(i4);
		if(pathPoint5 == null) {
			pathPoint5 = new PathPoint(i1, i2, i3);
			this.pointMap.addKey(i4, pathPoint5);
		}

		return pathPoint5;
	}

	private int getVerticalOffset(Entity entity1, int i2, int i3, int i4, PathPoint pathPoint5) {
		boolean z6 = false;

		for(int i7 = i2; i7 < i2 + pathPoint5.xCoord; ++i7) {
			for(int i8 = i3; i8 < i3 + pathPoint5.yCoord; ++i8) {
				for(int i9 = i4; i9 < i4 + pathPoint5.zCoord; ++i9) {
					int i10 = this.worldMap.getBlockId(i7, i8, i9);
					if(i10 > 0) {
						/*if(i10 == Block.trapdoor.blockID) {
							z6 = true;
						} else*/ if(i10 != Block.waterMoving.blockID && i10 != Block.waterStill.blockID) {
							if(!this.isWoddenDoorAllowed && i10 == Block.doorWood.blockID) {
								return 0;
							}
						} else {
							if(this.isPathingInWater) {
								return -1;
							}

							z6 = true;
						}

						Block block11 = Block.blocksList[i10];
						if(!block11.getBlocksMovement(this.worldMap, i7, i8, i9) && (!this.isMovementBlockAllowed || i10 != Block.doorWood.blockID)) {
							if(i10 == Block.fence.blockID /*|| i10 == Block.fenceGate.blockID*/) {
								return -3;
							}
							/*
							if(i10 == Block.trapdoor.blockID) {
								return -4;
							}
							*/

							Material material12 = block11.blockMaterial;
							if(material12 != Material.lava) {
								return 0;
							}

							if(!entity1.handleLavaMovement()) {
								return -2;
							}
						}
					}
				}
			}
		}

		return z6 ? 2 : 1;
	}

	private PathEntity createEntityPath(PathPoint pathPoint1, PathPoint pathPoint2) {
		int i3 = 1;

		PathPoint pathPoint4;
		for(pathPoint4 = pathPoint2; pathPoint4.previous != null; pathPoint4 = pathPoint4.previous) {
			++i3;
		}

		PathPoint[] pathPoint5 = new PathPoint[i3];
		pathPoint4 = pathPoint2;
		--i3;

		for(pathPoint5[i3] = pathPoint2; pathPoint4.previous != null; pathPoint5[i3] = pathPoint4) {
			pathPoint4 = pathPoint4.previous;
			--i3;
		}

		return new PathEntity(pathPoint5);
	}
}
