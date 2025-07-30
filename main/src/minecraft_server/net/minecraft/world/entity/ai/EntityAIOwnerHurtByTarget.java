package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityTameable;

public class EntityAIOwnerHurtByTarget extends EntityAITarget {
	EntityTameable theAnimal;
	EntityLiving theOwner;

	public EntityAIOwnerHurtByTarget(EntityTameable entityTameable1) {
		super(entityTameable1, 32.0F, false);
		this.theAnimal = entityTameable1;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(!this.theAnimal.isTamed()) {
			return false;
		} else {
			EntityLiving entityLiving1 = this.theAnimal.getOwner();
			if(entityLiving1 == null) {
				return false;
			} else {
				this.theOwner = entityLiving1.getAITarget();
				return this.func_48376_a(this.theOwner, false);
			}
		}
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.theOwner);
		super.startExecuting();
	}
}
