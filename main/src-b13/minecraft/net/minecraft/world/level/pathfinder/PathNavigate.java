package net.minecraft.world.level.pathfinder;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.Vec3D;

public class PathNavigate {
	private EntityLiving theEntity;
	private World worldObj;
	private PathEntity currentPath;
	private float speed;
	private float pathSearchRange;
	private boolean noSunPathfind = false;
	private int totalTicks;
	private int ticksAtLastPos;
	private Vec3D lastPosCheck = Vec3D.createVectorHelper(0.0D, 0.0D, 0.0D);
	private boolean canPassOpenWoodenDoors = true;
	private boolean canPassClosedWoodenDoors = false;
	private boolean avoidsWater = false;
	private boolean canSwim = false;

	public PathNavigate(EntityLiving entityLiving1, World world2, float f3) {
		this.theEntity = entityLiving1;
		this.worldObj = world2;
		this.pathSearchRange = f3;
	}

	public void setAvoidsWater(boolean z1) {
		this.avoidsWater = z1;
	}

	public boolean getAvoidsWater() {
		return this.avoidsWater;
	}

	public void setBreakDoors(boolean z1) {
		this.canPassClosedWoodenDoors = z1;
	}

	public void setEnterDoors(boolean z1) {
		this.canPassOpenWoodenDoors = z1;
	}

	public boolean getCanBreakDoors() {
		return this.canPassClosedWoodenDoors;
	}

	public void setAvoidSun(boolean z1) {
		this.noSunPathfind = z1;
	}

	public void setSpeed(float f1) {
		this.speed = f1;
	}

	public void setCanSwim(boolean z1) {
		this.canSwim = z1;
	}

	public PathEntity getPathToXYZ(double d1, double d3, double d5) {
		return !this.canNavigate() ? null : this.worldObj.getEntityPathToXYZ(this.theEntity, MathHelper.floor_double(d1), (int)d3, MathHelper.floor_double(d5), this.pathSearchRange, this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
	}

	public boolean tryMoveToXYZ(double d1, double d3, double d5, float f7) {
		PathEntity pathEntity8 = this.getPathToXYZ((double)MathHelper.floor_double(d1), (double)((int)d3), (double)MathHelper.floor_double(d5));
		return this.setPath(pathEntity8, f7);
	}

	public PathEntity getPathToEntityLiving(EntityLiving entityLiving1) {
		return !this.canNavigate() ? null : this.worldObj.getPathEntityToEntity(this.theEntity, entityLiving1, this.pathSearchRange, this.canPassOpenWoodenDoors, this.canPassClosedWoodenDoors, this.avoidsWater, this.canSwim);
	}

	public boolean tryMoveToEntityLiving(EntityLiving entityLiving1, float f2) {
		PathEntity pathEntity3 = this.getPathToEntityLiving(entityLiving1);
		return pathEntity3 != null ? this.setPath(pathEntity3, f2) : false;
	}

	public boolean setPath(PathEntity pathEntity1, float f2) {
		if(pathEntity1 == null) {
			this.currentPath = null;
			return false;
		} else {
			if(!pathEntity1.func_48647_a(this.currentPath)) {
				this.currentPath = pathEntity1;
			}

			if(this.noSunPathfind) {
				this.removeSunnyPath();
			}

			if(this.currentPath.getCurrentPathLength() == 0) {
				return false;
			} else {
				this.speed = f2;
				Vec3D vec3D3 = this.getEntityPosition();
				this.ticksAtLastPos = this.totalTicks;
				this.lastPosCheck.xCoord = vec3D3.xCoord;
				this.lastPosCheck.yCoord = vec3D3.yCoord;
				this.lastPosCheck.zCoord = vec3D3.zCoord;
				return true;
			}
		}
	}

	public PathEntity getPath() {
		return this.currentPath;
	}

	public void onUpdateNavigation() {
		++this.totalTicks;
		if(!this.noPath()) {
			if(this.canNavigate()) {
				this.pathFollow();
			}

			if(!this.noPath()) {
				Vec3D vec3D1 = this.currentPath.getCurrentNodeVec3d(this.theEntity);
				if(vec3D1 != null) {
					this.theEntity.getMoveHelper().setMoveTo(vec3D1.xCoord, vec3D1.yCoord, vec3D1.zCoord, this.speed);
				}
			}
		}
	}

	private void pathFollow() {
		Vec3D vec3D1 = this.getEntityPosition();
		int i2 = this.currentPath.getCurrentPathLength();

		for(int i3 = this.currentPath.getCurrentPathIndex(); i3 < this.currentPath.getCurrentPathLength(); ++i3) {
			if(this.currentPath.getPathPointFromIndex(i3).yCoord != (int)vec3D1.yCoord) {
				i2 = i3;
				break;
			}
		}

		float f8 = this.theEntity.width * this.theEntity.width;

		int i4;
		for(i4 = this.currentPath.getCurrentPathIndex(); i4 < i2; ++i4) {
			if(vec3D1.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, i4)) < (double)f8) {
				this.currentPath.setCurrentPathIndex(i4 + 1);
			}
		}

		i4 = (int)Math.ceil((double)this.theEntity.width);
		int i5 = (int)this.theEntity.height + 1;
		int i6 = i4;

		for(int i7 = i2 - 1; i7 >= this.currentPath.getCurrentPathIndex(); --i7) {
			if(this.isDirectPathBetweenPoints(vec3D1, this.currentPath.getVectorFromIndex(this.theEntity, i7), i4, i5, i6)) {
				this.currentPath.setCurrentPathIndex(i7);
				break;
			}
		}

		if(this.totalTicks - this.ticksAtLastPos > 100) {
			if(vec3D1.squareDistanceTo(this.lastPosCheck) < 2.25D) {
				this.clearPathEntity();
			}

			this.ticksAtLastPos = this.totalTicks;
			this.lastPosCheck.xCoord = vec3D1.xCoord;
			this.lastPosCheck.yCoord = vec3D1.yCoord;
			this.lastPosCheck.zCoord = vec3D1.zCoord;
		}

	}

	public boolean noPath() {
		return this.currentPath == null || this.currentPath.isFinished();
	}

	public void clearPathEntity() {
		this.currentPath = null;
	}

	private Vec3D getEntityPosition() {
		return Vec3D.createVector(this.theEntity.posX, (double)this.getPathableYPos(), this.theEntity.posZ);
	}

	private int getPathableYPos() {
		if(this.theEntity.isInWater() && this.canSwim) {
			int i1 = (int)this.theEntity.boundingBox.minY;
			int i2 = this.worldObj.getBlockId(MathHelper.floor_double(this.theEntity.posX), i1, MathHelper.floor_double(this.theEntity.posZ));
			int i3 = 0;

			do {
				if(i2 != Block.waterMoving.blockID && i2 != Block.waterStill.blockID) {
					return i1;
				}

				++i1;
				i2 = this.worldObj.getBlockId(MathHelper.floor_double(this.theEntity.posX), i1, MathHelper.floor_double(this.theEntity.posZ));
				++i3;
			} while(i3 <= 16);

			return (int)this.theEntity.boundingBox.minY;
		} else {
			return (int)(this.theEntity.boundingBox.minY + 0.5D);
		}
	}

	private boolean canNavigate() {
		return this.theEntity.onGround || this.canSwim && this.func_48657_k();
	}

	private boolean func_48657_k() {
		return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
	}

	private void removeSunnyPath() {
		if(!this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.theEntity.posX), (int)(this.theEntity.boundingBox.minY + 0.5D), MathHelper.floor_double(this.theEntity.posZ))) {
			for(int i1 = 0; i1 < this.currentPath.getCurrentPathLength(); ++i1) {
				PathPoint pathPoint2 = this.currentPath.getPathPointFromIndex(i1);
				if(this.worldObj.canBlockSeeTheSky(pathPoint2.xCoord, pathPoint2.yCoord, pathPoint2.zCoord)) {
					this.currentPath.setCurrentPathLength(i1 - 1);
					return;
				}
			}

		}
	}

	private boolean isDirectPathBetweenPoints(Vec3D vec3D1, Vec3D vec3D2, int i3, int i4, int i5) {
		int i6 = MathHelper.floor_double(vec3D1.xCoord);
		int i7 = MathHelper.floor_double(vec3D1.zCoord);
		double d8 = vec3D2.xCoord - vec3D1.xCoord;
		double d10 = vec3D2.zCoord - vec3D1.zCoord;
		double d12 = d8 * d8 + d10 * d10;
		if(d12 < 1.0E-8D) {
			return false;
		} else {
			double d14 = 1.0D / Math.sqrt(d12);
			d8 *= d14;
			d10 *= d14;
			i3 += 2;
			i5 += 2;
			if(!this.isSafeToStandAt(i6, (int)vec3D1.yCoord, i7, i3, i4, i5, vec3D1, d8, d10)) {
				return false;
			} else {
				i3 -= 2;
				i5 -= 2;
				double d16 = 1.0D / Math.abs(d8);
				double d18 = 1.0D / Math.abs(d10);
				double d20 = (double)(i6 * 1) - vec3D1.xCoord;
				double d22 = (double)(i7 * 1) - vec3D1.zCoord;
				if(d8 >= 0.0D) {
					++d20;
				}

				if(d10 >= 0.0D) {
					++d22;
				}

				d20 /= d8;
				d22 /= d10;
				int i24 = d8 < 0.0D ? -1 : 1;
				int i25 = d10 < 0.0D ? -1 : 1;
				int i26 = MathHelper.floor_double(vec3D2.xCoord);
				int i27 = MathHelper.floor_double(vec3D2.zCoord);
				int i28 = i26 - i6;
				int i29 = i27 - i7;

				do {
					if(i28 * i24 <= 0 && i29 * i25 <= 0) {
						return true;
					}

					if(d20 < d22) {
						d20 += d16;
						i6 += i24;
						i28 = i26 - i6;
					} else {
						d22 += d18;
						i7 += i25;
						i29 = i27 - i7;
					}
				} while(this.isSafeToStandAt(i6, (int)vec3D1.yCoord, i7, i3, i4, i5, vec3D1, d8, d10));

				return false;
			}
		}
	}

	private boolean isSafeToStandAt(int i1, int i2, int i3, int i4, int i5, int i6, Vec3D vec3D7, double d8, double d10) {
		int i12 = i1 - i4 / 2;
		int i13 = i3 - i6 / 2;
		if(!this.isPositionClear(i12, i2, i13, i4, i5, i6, vec3D7, d8, d10)) {
			return false;
		} else {
			for(int i14 = i12; i14 < i12 + i4; ++i14) {
				for(int i15 = i13; i15 < i13 + i6; ++i15) {
					double d16 = (double)i14 + 0.5D - vec3D7.xCoord;
					double d18 = (double)i15 + 0.5D - vec3D7.zCoord;
					if(d16 * d8 + d18 * d10 >= 0.0D) {
						int i20 = this.worldObj.getBlockId(i14, i2 - 1, i15);
						if(i20 <= 0) {
							return false;
						}

						Material material21 = Block.blocksList[i20].blockMaterial;
						if(material21 == Material.water && !this.theEntity.isInWater()) {
							return false;
						}

						if(material21 == Material.lava) {
							return false;
						}
					}
				}
			}

			return true;
		}
	}

	private boolean isPositionClear(int i1, int i2, int i3, int i4, int i5, int i6, Vec3D vec3D7, double d8, double d10) {
		for(int i12 = i1; i12 < i1 + i4; ++i12) {
			for(int i13 = i2; i13 < i2 + i5; ++i13) {
				for(int i14 = i3; i14 < i3 + i6; ++i14) {
					double d15 = (double)i12 + 0.5D - vec3D7.xCoord;
					double d17 = (double)i14 + 0.5D - vec3D7.zCoord;
					if(d15 * d8 + d17 * d10 >= 0.0D) {
						int i19 = this.worldObj.getBlockId(i12, i13, i14);
						if(i19 > 0 && !Block.blocksList[i19].getBlocksMovement(this.worldObj, i12, i13, i14)) {
							return false;
						}
					}
				}
			}
		}

		return true;
	}
}
