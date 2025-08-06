package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityLiving;

public class EntityAISwimming extends EntityAIBase {
	private EntityLiving theEntity;

	public EntityAISwimming(EntityLiving entityLiving1) {
		this.theEntity = entityLiving1;
		this.setMutexBits(4);
		entityLiving1.getNavigator().setCanSwim(true);
	}

	public boolean shouldExecute() {
		return this.theEntity.isInWater() || this.theEntity.handleLavaMovement();
	}

	public void updateTask() {
		if(this.theEntity.getRNG().nextFloat() < 0.8F) {
			this.theEntity.getJumpHelper().setJumping();
		}

	}
}
