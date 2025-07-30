package net.minecraft.world.entity.ai;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;

public class EntityAIAttackOnCollide extends EntityAIBase {
	World worldObj;
	EntityLiving attacker;
	EntityLiving entityTarget;
	int field_46091_d;
	float field_48266_e;
	boolean field_48264_f;
	PathEntity field_48265_g;
	Class<?> classTarget;
	private int field_48269_i;

	public EntityAIAttackOnCollide(EntityLiving entityLiving1, Class<?> class2, float f3, boolean z4) {
		this(entityLiving1, f3, z4);
		this.classTarget = class2;
	}

	public EntityAIAttackOnCollide(EntityLiving entityLiving1, float f2, boolean z3) {
		this.field_46091_d = 0;
		this.attacker = entityLiving1;
		this.worldObj = entityLiving1.worldObj;
		this.field_48266_e = f2;
		this.field_48264_f = z3;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		EntityLiving entityLiving1 = this.attacker.getAttackTarget();
		if(entityLiving1 == null) {
			return false;
		} else if(this.classTarget != null && !this.classTarget.isAssignableFrom(entityLiving1.getClass())) {
			return false;
		} else {
			this.entityTarget = entityLiving1;
			this.field_48265_g = this.attacker.getNavigator().getPathToEntityLiving(this.entityTarget);
			return this.field_48265_g != null;
		}
	}

	public boolean continueExecuting() {
		EntityLiving entityLiving1 = this.attacker.getAttackTarget();
		return entityLiving1 == null ? false : (!this.entityTarget.isEntityAlive() ? false : (!this.field_48264_f ? !this.attacker.getNavigator().noPath() : this.attacker.isWithinHomeDistance(MathHelper.floor_double(this.entityTarget.posX), MathHelper.floor_double(this.entityTarget.posY), MathHelper.floor_double(this.entityTarget.posZ))));
	}

	public void startExecuting() {
		this.attacker.getNavigator().setPath(this.field_48265_g, this.field_48266_e);
		this.field_48269_i = 0;
	}

	public void resetTask() {
		this.entityTarget = null;
		this.attacker.getNavigator().clearPathEntity();
	}

	public void updateTask() {
		this.attacker.getLookHelper().setLookPositionWithEntity(this.entityTarget, 30.0F, 30.0F);
		if((this.field_48264_f || this.attacker.getEntitySenses().canSee(this.entityTarget)) && --this.field_48269_i <= 0) {
			this.field_48269_i = 4 + this.attacker.getRNG().nextInt(7);
			this.attacker.getNavigator().tryMoveToEntityLiving(this.entityTarget, this.field_48266_e);
		}

		this.field_46091_d = Math.max(this.field_46091_d - 1, 0);
		double d1 = (double)(this.attacker.width * 2.0F * this.attacker.width * 2.0F);
		if(this.attacker.getDistanceSq(this.entityTarget.posX, this.entityTarget.boundingBox.minY, this.entityTarget.posZ) <= d1) {
			if(this.field_46091_d <= 0) {
				this.field_46091_d = 20;
				this.attacker.attackEntityAsMob(this.entityTarget);
			}
		}
	}
}
