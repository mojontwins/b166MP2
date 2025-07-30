package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.Vec3D;

public class EntityAIMoveTowardsTarget extends EntityAIBase {
	private EntityCreature theEntity;
	private EntityLiving targetEntity;
	private double movePosX;
	private double movePosY;
	private double movePosZ;
	private float field_48330_f;
	private float field_48331_g;

	public EntityAIMoveTowardsTarget(EntityCreature entityCreature1, float f2, float f3) {
		this.theEntity = entityCreature1;
		this.field_48330_f = f2;
		this.field_48331_g = f3;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		this.targetEntity = this.theEntity.getAttackTarget();
		if(this.targetEntity == null) {
			return false;
		} else if(this.targetEntity.getDistanceSqToEntity(this.theEntity) > (double)(this.field_48331_g * this.field_48331_g)) {
			return false;
		} else {
			Vec3D vec3D1 = RandomPositionGenerator.findRandomTargetBlockTowards(this.theEntity, 16, 7, Vec3D.createVector(this.targetEntity.posX, this.targetEntity.posY, this.targetEntity.posZ));
			if(vec3D1 == null) {
				return false;
			} else {
				this.movePosX = vec3D1.xCoord;
				this.movePosY = vec3D1.yCoord;
				this.movePosZ = vec3D1.zCoord;
				return true;
			}
		}
	}

	public boolean continueExecuting() {
		return !this.theEntity.getNavigator().noPath() && this.targetEntity.isEntityAlive() && this.targetEntity.getDistanceSqToEntity(this.theEntity) < (double)(this.field_48331_g * this.field_48331_g);
	}

	public void resetTask() {
		this.targetEntity = null;
	}

	public void startExecuting() {
		this.theEntity.getNavigator().tryMoveToXYZ(this.movePosX, this.movePosY, this.movePosZ, this.field_48330_f);
	}
}
