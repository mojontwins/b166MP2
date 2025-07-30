package net.minecraft.world.entity.ai;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityTameable;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathPoint;

public abstract class EntityAITarget extends EntityAIBase {
	protected EntityLiving taskOwner;
	protected float field_48379_d;
	protected boolean field_48380_e;
	private boolean field_48383_a;
	private int field_48381_b;
	private int field_48377_f;
	private int field_48378_g;

	public EntityAITarget(EntityLiving entityLiving1, float f2, boolean z3) {
		this(entityLiving1, f2, z3, false);
	}

	public EntityAITarget(EntityLiving entityLiving1, float f2, boolean z3, boolean z4) {
		this.field_48381_b = 0;
		this.field_48377_f = 0;
		this.field_48378_g = 0;
		this.taskOwner = entityLiving1;
		this.field_48379_d = f2;
		this.field_48380_e = z3;
		this.field_48383_a = z4;
	}

	public boolean continueExecuting() {
		EntityLiving entityLiving1 = this.taskOwner.getAttackTarget();
		if(entityLiving1 == null) {
			return false;
		} else if(!entityLiving1.isEntityAlive()) {
			return false;
		} else if(this.taskOwner.getDistanceSqToEntity(entityLiving1) > (double)(this.field_48379_d * this.field_48379_d)) {
			return false;
		} else {
			if(this.field_48380_e) {
				if(!this.taskOwner.getEntitySenses().canSee(entityLiving1)) {
					if(++this.field_48378_g > 60) {
						return false;
					}
				} else {
					this.field_48378_g = 0;
				}
			}

			return true;
		}
	}

	public void startExecuting() {
		this.field_48381_b = 0;
		this.field_48377_f = 0;
		this.field_48378_g = 0;
	}

	public void resetTask() {
		this.taskOwner.setAttackTarget((EntityLiving)null);
	}

	protected boolean func_48376_a(EntityLiving entityLiving1, boolean z2) {
		if(entityLiving1 == null) {
			return false;
		} else if(entityLiving1 == this.taskOwner) {
			return false;
		} else if(!entityLiving1.isEntityAlive()) {
			return false;
		} else if(entityLiving1.boundingBox.maxY > this.taskOwner.boundingBox.minY && entityLiving1.boundingBox.minY < this.taskOwner.boundingBox.maxY) {
			if(!this.taskOwner.func_48100_a(entityLiving1.getClass())) {
				return false;
			} else {
				if(this.taskOwner instanceof EntityTameable && ((EntityTameable)this.taskOwner).isTamed()) {
					if(entityLiving1 instanceof EntityTameable && ((EntityTameable)entityLiving1).isTamed()) {
						return false;
					}

					if(entityLiving1 == ((EntityTameable)this.taskOwner).getOwner()) {
						return false;
					}
				} else if(entityLiving1 instanceof EntityPlayer && !z2 && ((EntityPlayer)entityLiving1).capabilities.disableDamage) {
					return false;
				}

				if(!this.taskOwner.isWithinHomeDistance(MathHelper.floor_double(entityLiving1.posX), MathHelper.floor_double(entityLiving1.posY), MathHelper.floor_double(entityLiving1.posZ))) {
					return false;
				} else if(this.field_48380_e && !this.taskOwner.getEntitySenses().canSee(entityLiving1)) {
					return false;
				} else {
					if(this.field_48383_a) {
						if(--this.field_48377_f <= 0) {
							this.field_48381_b = 0;
						}

						if(this.field_48381_b == 0) {
							this.field_48381_b = this.func_48375_a(entityLiving1) ? 1 : 2;
						}

						if(this.field_48381_b == 2) {
							return false;
						}
					}

					return true;
				}
			}
		} else {
			return false;
		}
	}

	private boolean func_48375_a(EntityLiving entityLiving1) {
		this.field_48377_f = 10 + this.taskOwner.getRNG().nextInt(5);
		PathEntity pathEntity2 = this.taskOwner.getNavigator().getPathToEntityLiving(entityLiving1);
		if(pathEntity2 == null) {
			return false;
		} else {
			PathPoint pathPoint3 = pathEntity2.getFinalPathPoint();
			if(pathPoint3 == null) {
				return false;
			} else {
				int i4 = pathPoint3.xCoord - MathHelper.floor_double(entityLiving1.posX);
				int i5 = pathPoint3.zCoord - MathHelper.floor_double(entityLiving1.posZ);
				return (double)(i4 * i4 + i5 * i5) <= 2.25D;
			}
		}
	}
}
