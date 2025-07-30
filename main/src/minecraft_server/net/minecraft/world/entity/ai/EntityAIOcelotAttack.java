package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;

public class EntityAIOcelotAttack extends EntityAIBase {
	World theWorld;
	EntityLiving theEntity;
	EntityLiving theTarget;
	int field_48360_d = 0;

	public EntityAIOcelotAttack(EntityLiving entityLiving1) {
		this.theEntity = entityLiving1;
		this.theWorld = entityLiving1.worldObj;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		EntityLiving entityLiving1 = this.theEntity.getAttackTarget();
		if(entityLiving1 == null) {
			return false;
		} else {
			this.theTarget = entityLiving1;
			return true;
		}
	}

	public boolean continueExecuting() {
		return !this.theTarget.isEntityAlive() ? false : (this.theEntity.getDistanceSqToEntity(this.theTarget) > 225.0D ? false : !this.theEntity.getNavigator().noPath() || this.shouldExecute());
	}

	public void resetTask() {
		this.theTarget = null;
		this.theEntity.getNavigator().clearPathEntity();
	}

	public void updateTask() {
		this.theEntity.getLookHelper().setLookPositionWithEntity(this.theTarget, 30.0F, 30.0F);
		double d1 = (double)(this.theEntity.width * 2.0F * this.theEntity.width * 2.0F);
		double d3 = this.theEntity.getDistanceSq(this.theTarget.posX, this.theTarget.boundingBox.minY, this.theTarget.posZ);
		float f5 = 0.23F;
		if(d3 > d1 && d3 < 16.0D) {
			f5 = 0.4F;
		} else if(d3 < 225.0D) {
			f5 = 0.18F;
		}

		this.theEntity.getNavigator().tryMoveToEntityLiving(this.theTarget, f5);
		this.field_48360_d = Math.max(this.field_48360_d - 1, 0);
		if(d3 <= d1) {
			if(this.field_48360_d <= 0) {
				this.field_48360_d = 20;
				this.theEntity.attackEntityAsMob(this.theTarget);
			}
		}
	}
}
