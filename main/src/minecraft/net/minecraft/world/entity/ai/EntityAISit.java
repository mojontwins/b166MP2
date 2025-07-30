package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityTameable;

public class EntityAISit extends EntityAIBase {
	private EntityTameable theEntity;
	private boolean field_48408_b = false;

	public EntityAISit(EntityTameable entityTameable1) {
		this.theEntity = entityTameable1;
		this.setMutexBits(5);
	}

	public boolean shouldExecute() {
		if(!this.theEntity.isTamed()) {
			return false;
		} else if(this.theEntity.isInWater()) {
			return false;
		} else if(!this.theEntity.onGround) {
			return false;
		} else {
			EntityLiving entityLiving1 = this.theEntity.getOwner();
			return entityLiving1 == null ? true : (this.theEntity.getDistanceSqToEntity(entityLiving1) < 144.0D && entityLiving1.getAITarget() != null ? false : this.field_48408_b);
		}
	}

	public void startExecuting() {
		this.theEntity.getNavigator().clearPathEntity();
		this.theEntity.setSitting(true);
	}

	public void resetTask() {
		this.theEntity.setSitting(false);
	}

	public void func_48407_a(boolean z1) {
		this.field_48408_b = z1;
	}
}
