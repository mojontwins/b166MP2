package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;

public class EntityAIWatchClosest extends EntityAIBase {
	private EntityLiving theEntity;
	private Entity closestEntity;
	private float field_46101_d;
	private int field_46102_e;
	private float field_48294_e;
	private Class<?> field_48293_f;

	public EntityAIWatchClosest(EntityLiving entityLiving1, Class<?> class2, float f3) {
		this.theEntity = entityLiving1;
		this.field_48293_f = class2;
		this.field_46101_d = f3;
		this.field_48294_e = 0.02F;
		this.setMutexBits(2);
	}

	public EntityAIWatchClosest(EntityLiving entityLiving1, Class<?> class2, float f3, float f4) {
		this.theEntity = entityLiving1;
		this.field_48293_f = class2;
		this.field_46101_d = f3;
		this.field_48294_e = f4;
		this.setMutexBits(2);
	}

	public boolean shouldExecute() {
		if(this.theEntity.getRNG().nextFloat() >= this.field_48294_e) {
			return false;
		} else {
			if(this.field_48293_f == EntityPlayer.class) {
				this.closestEntity = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, (double)this.field_46101_d);
			} else {
				this.closestEntity = this.theEntity.worldObj.findNearestEntityWithinAABB(this.field_48293_f, this.theEntity.boundingBox.expand((double)this.field_46101_d, 3.0D, (double)this.field_46101_d), this.theEntity);
			}

			return this.closestEntity != null;
		}
	}

	public boolean continueExecuting() {
		return !this.closestEntity.isEntityAlive() ? false : (this.theEntity.getDistanceSqToEntity(this.closestEntity) > (double)(this.field_46101_d * this.field_46101_d) ? false : this.field_46102_e > 0);
	}

	public void startExecuting() {
		this.field_46102_e = 40 + this.theEntity.getRNG().nextInt(40);
	}

	public void resetTask() {
		this.closestEntity = null;
	}

	public void updateTask() {
		this.theEntity.getLookHelper().setLookPosition(this.closestEntity.posX, this.closestEntity.posY + (double)this.closestEntity.getEyeHeight(), this.closestEntity.posZ, 10.0F, (float)this.theEntity.getVerticalFaceSpeed());
		--this.field_46102_e;
	}
}
