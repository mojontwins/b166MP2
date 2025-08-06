package net.minecraft.world.entity.ai;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.projectile.EntityArrow;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.level.World;

public class EntityAIArrowAttack extends EntityAIBase {
	World worldObj;
	EntityLiving entityHost;
	EntityLiving attackTarget;
	int rangedAttackTime = 0;
	float field_48370_e;
	int field_48367_f = 0;
	int rangedAttackID;
	int maxRangedAttackTime;

	public EntityAIArrowAttack(EntityLiving entityLiving1, float f2, int i3, int i4) {
		this.entityHost = entityLiving1;
		this.worldObj = entityLiving1.worldObj;
		this.field_48370_e = f2;
		this.rangedAttackID = i3;
		this.maxRangedAttackTime = i4;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		EntityLiving entityLiving1 = this.entityHost.getAttackTarget();
		if(entityLiving1 == null) {
			return false;
		} else {
			this.attackTarget = entityLiving1;
			return true;
		}
	}

	public boolean continueExecuting() {
		return this.shouldExecute() || !this.entityHost.getNavigator().noPath();
	}

	public void resetTask() {
		this.attackTarget = null;
	}

	public void updateTask() {
		double d1 = 100.0D;
		double d3 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.boundingBox.minY, this.attackTarget.posZ);
		boolean z5 = this.entityHost.getEntitySenses().canSee(this.attackTarget);
		if(z5) {
			++this.field_48367_f;
		} else {
			this.field_48367_f = 0;
		}

		if(d3 <= d1 && this.field_48367_f >= 20) {
			this.entityHost.getNavigator().clearPathEntity();
		} else {
			this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.field_48370_e);
		}

		this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
		this.rangedAttackTime = Math.max(this.rangedAttackTime - 1, 0);
		if(this.rangedAttackTime <= 0) {
			if(d3 <= d1 && z5) {
				this.doRangedAttack();
				this.rangedAttackTime = this.maxRangedAttackTime;
			}
		}
	}

	private void doRangedAttack() {
		if(this.rangedAttackID == 1) {
			EntityArrow entityArrow1 = new EntityArrow(this.worldObj, this.entityHost, this.attackTarget, 1.6F, 12.0F);
			this.worldObj.playSoundAtEntity(this.entityHost, "random.bow", 1.0F, 1.0F / (this.entityHost.getRNG().nextFloat() * 0.4F + 0.8F));
			this.worldObj.spawnEntityInWorld(entityArrow1);
		} else if(this.rangedAttackID == 2) {
			EntitySnowball entitySnowball9 = new EntitySnowball(this.worldObj, this.entityHost);
			double d2 = this.attackTarget.posX - this.entityHost.posX;
			double d4 = this.attackTarget.posY + (double)this.attackTarget.getEyeHeight() - 1.100000023841858D - entitySnowball9.posY;
			double d6 = this.attackTarget.posZ - this.entityHost.posZ;
			float f8 = MathHelper.sqrt_double(d2 * d2 + d6 * d6) * 0.2F;
			entitySnowball9.setThrowableHeading(d2, d4 + (double)f8, d6, 1.6F, 12.0F);
			this.worldObj.playSoundAtEntity(this.entityHost, "random.bow", 1.0F, 1.0F / (this.entityHost.getRNG().nextFloat() * 0.4F + 0.8F));
			this.worldObj.spawnEntityInWorld(entitySnowball9);
		}

	}
}
