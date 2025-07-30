package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityLiving;

public class EntityAILookIdle extends EntityAIBase {
	private EntityLiving idleEntity;
	private double lookX;
	private double lookZ;
	private int idleTime = 0;

	public EntityAILookIdle(EntityLiving entityLiving1) {
		this.idleEntity = entityLiving1;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		return this.idleEntity.getRNG().nextFloat() < 0.02F;
	}

	public boolean continueExecuting() {
		return this.idleTime >= 0;
	}

	public void startExecuting() {
		double d1 = Math.PI * 2D * this.idleEntity.getRNG().nextDouble();
		this.lookX = Math.cos(d1);
		this.lookZ = Math.sin(d1);
		this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
	}

	public void updateTask() {
		--this.idleTime;
		this.idleEntity.getLookHelper().setLookPosition(this.idleEntity.posX + this.lookX, this.idleEntity.posY + (double)this.idleEntity.getEyeHeight(), this.idleEntity.posZ + this.lookZ, 10.0F, (float)this.idleEntity.getVerticalFaceSpeed());
	}
}
