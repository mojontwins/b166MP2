package net.minecraft.world.entity.ai;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;

public class EntityAILeapAtTarget extends EntityAIBase {
	EntityLiving leaper;
	EntityLiving leapTarget;
	float leapMotionY;

	public EntityAILeapAtTarget(EntityLiving entityLiving1, float f2) {
		this.leaper = entityLiving1;
		this.leapMotionY = f2;
		this.setMutexBits(5);
	}

	public boolean shouldExecute() {
		this.leapTarget = this.leaper.getAttackTarget();
		if(this.leapTarget == null) {
			return false;
		} else {
			double d1 = this.leaper.getDistanceSqToEntity(this.leapTarget);
			return d1 >= 4.0D && d1 <= 16.0D ? (!this.leaper.onGround ? false : this.leaper.getRNG().nextInt(5) == 0) : false;
		}
	}

	public boolean continueExecuting() {
		return !this.leaper.onGround;
	}

	public void startExecuting() {
		double d1 = this.leapTarget.posX - this.leaper.posX;
		double d3 = this.leapTarget.posZ - this.leaper.posZ;
		float f5 = MathHelper.sqrt_double(d1 * d1 + d3 * d3);
		this.leaper.motionX += d1 / (double)f5 * 0.5D * (double)0.8F + this.leaper.motionX * (double)0.2F;
		this.leaper.motionZ += d3 / (double)f5 * 0.5D * (double)0.8F + this.leaper.motionZ * (double)0.2F;
		this.leaper.motionY = (double)this.leapMotionY;
	}
}
