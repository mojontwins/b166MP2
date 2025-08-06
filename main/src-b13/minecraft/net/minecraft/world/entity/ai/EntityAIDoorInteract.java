package net.minecraft.world.entity.ai;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathNavigate;
import net.minecraft.world.level.pathfinder.PathPoint;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockDoor;

public abstract class EntityAIDoorInteract extends EntityAIBase {
	protected EntityLiving theEntity;
	protected int entityPosX;
	protected int entityPosY;
	protected int entityPosZ;
	protected BlockDoor targetDoor;
	boolean field_48319_f;
	float field_48320_g;
	float field_48326_h;

	public EntityAIDoorInteract(EntityLiving entityLiving1) {
		this.theEntity = entityLiving1;
	}

	public boolean shouldExecute() {
		if(!this.theEntity.isCollidedHorizontally) {
			return false;
		} else {
			PathNavigate pathNavigate1 = this.theEntity.getNavigator();
			PathEntity pathEntity2 = pathNavigate1.getPath();
			if(pathEntity2 != null && !pathEntity2.isFinished() && pathNavigate1.getCanBreakDoors()) {
				for(int i3 = 0; i3 < Math.min(pathEntity2.getCurrentPathIndex() + 2, pathEntity2.getCurrentPathLength()); ++i3) {
					PathPoint pathPoint4 = pathEntity2.getPathPointFromIndex(i3);
					this.entityPosX = pathPoint4.xCoord;
					this.entityPosY = pathPoint4.yCoord + 1;
					this.entityPosZ = pathPoint4.zCoord;
					if(this.theEntity.getDistanceSq((double)this.entityPosX, this.theEntity.posY, (double)this.entityPosZ) <= 2.25D) {
						this.targetDoor = this.func_48318_a(this.entityPosX, this.entityPosY, this.entityPosZ);
						if(this.targetDoor != null) {
							return true;
						}
					}
				}

				this.entityPosX = MathHelper.floor_double(this.theEntity.posX);
				this.entityPosY = MathHelper.floor_double(this.theEntity.posY + 1.0D);
				this.entityPosZ = MathHelper.floor_double(this.theEntity.posZ);
				this.targetDoor = this.func_48318_a(this.entityPosX, this.entityPosY, this.entityPosZ);
				return this.targetDoor != null;
			} else {
				return false;
			}
		}
	}

	public boolean continueExecuting() {
		return !this.field_48319_f;
	}

	public void startExecuting() {
		this.field_48319_f = false;
		this.field_48320_g = (float)((double)((float)this.entityPosX + 0.5F) - this.theEntity.posX);
		this.field_48326_h = (float)((double)((float)this.entityPosZ + 0.5F) - this.theEntity.posZ);
	}

	public void updateTask() {
		float f1 = (float)((double)((float)this.entityPosX + 0.5F) - this.theEntity.posX);
		float f2 = (float)((double)((float)this.entityPosZ + 0.5F) - this.theEntity.posZ);
		float f3 = this.field_48320_g * f1 + this.field_48326_h * f2;
		if(f3 < 0.0F) {
			this.field_48319_f = true;
		}

	}

	private BlockDoor func_48318_a(int i1, int i2, int i3) {
		int i4 = this.theEntity.worldObj.getBlockId(i1, i2, i3);
		if(i4 != Block.doorWood.blockID) {
			return null;
		} else {
			BlockDoor blockDoor5 = (BlockDoor)Block.blocksList[i4];
			return blockDoor5;
		}
	}
}
