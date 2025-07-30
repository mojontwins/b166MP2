package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.monster.EntityCreeper;

public class EntityAICreeperSwell extends EntityAIBase {
	EntityCreeper swellingCreeper;
	EntityLiving creeperAttackTarget;

	public EntityAICreeperSwell(EntityCreeper entityCreeper1) {
		this.swellingCreeper = entityCreeper1;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		EntityLiving entityLiving1 = this.swellingCreeper.getAttackTarget();
		return this.swellingCreeper.getCreeperState() > 0 || entityLiving1 != null && this.swellingCreeper.getDistanceSqToEntity(entityLiving1) < 9.0D;
	}

	public void startExecuting() {
		this.swellingCreeper.getNavigator().clearPathEntity();
		this.creeperAttackTarget = this.swellingCreeper.getAttackTarget();
	}

	public void resetTask() {
		this.creeperAttackTarget = null;
	}

	public void updateTask() {
		if(this.creeperAttackTarget == null) {
			this.swellingCreeper.setCreeperState(-1);
		} else if(this.swellingCreeper.getDistanceSqToEntity(this.creeperAttackTarget) > 49.0D) {
			this.swellingCreeper.setCreeperState(-1);
		} else if(!this.swellingCreeper.getEntitySenses().canSee(this.creeperAttackTarget)) {
			this.swellingCreeper.setCreeperState(-1);
		} else {
			this.swellingCreeper.setCreeperState(1);
		}
	}
}
