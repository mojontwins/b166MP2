package net.minecraft.src;

import java.util.List;

public class EntityBoar extends EntityPig {
	protected int force;
	protected double attackRange;

	public EntityBoar(World world1) {
		super(world1);
		this.texture = "/mob/boar.png";
		this.setSize(0.9F, 0.9F);
		this.health = 10;
		this.force = 1;
		this.attackRange = 1.0D;
	}

	public void onLivingUpdate() {
		if(this.worldObj.difficultySetting == 1) {
			this.attackRange = 2.0D;
			this.force = 1;
		} else if(this.worldObj.difficultySetting > 1) {
			this.attackRange = 3.0D;
			this.force = 2;
		}

		super.onLivingUpdate();
	}

	protected Entity findPlayerToAttack() {
		if(this.worldObj.difficultySetting > 0) {
			EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, this.attackRange);
			if(entityPlayer1 != null && this.rand.nextInt(50) == 0) {
				return entityPlayer1;
			}

			if(this.rand.nextInt(80) == 0) {
				EntityLiving entityLiving2 = this.getClosestTarget(this, 10.0D);
				return entityLiving2;
			}
		}

		return null;
	}

	public EntityLiving getClosestTarget(Entity entity1, double d2) {
		double d4 = -1.0D;
		EntityLiving entityLiving6 = null;
		List list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d2, d2, d2));

		for(int i8 = 0; i8 < list7.size(); ++i8) {
			Entity entity9 = (Entity)list7.get(i8);
			if(entity9 instanceof EntityLiving && entity9 != entity1 && entity9 != entity1.riddenByEntity && entity9 != entity1.ridingEntity && !(entity9 instanceof EntityPlayer) && !(entity9 instanceof EntityMob) && this.height > entity9.height && this.width > entity9.width) {
				double d10 = entity9.getDistanceSq(entity1.posY, entity1.posZ, entity1.motionX);
				if((d2 < 0.0D || d10 < d2 * d2) && (d4 == -1.0D || d10 < d4) && ((EntityLiving)entity9).canEntityBeSeen(entity1)) {
					d4 = d10;
					entityLiving6 = (EntityLiving)entity9;
				}
			}
		}

		return entityLiving6;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(super.attackEntityFrom(entity1, i2)) {
			if(this.riddenByEntity != entity1 && this.ridingEntity != entity1) {
				if(entity1 != this && this.worldObj.difficultySetting > 0) {
					this.playerToAttack = entity1;
				}

				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	protected void attackEntity(Entity entity1, float f2) {
		if((double)f2 < 2.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity1.attackEntityFrom(this, this.force);
			if(!(entity1 instanceof EntityPlayer)) {
				this.destroyDrops(this, 3.0D);
			}
		}

	}

	public void destroyDrops(Entity entity1, double d2) {
		List list4 = this.worldObj.getEntitiesWithinAABBExcludingEntity(entity1, entity1.boundingBox.expand(d2, d2, d2));

		for(int i5 = 0; i5 < list4.size(); ++i5) {
			Entity entity6 = (Entity)list4.get(i5);
			if(entity6 instanceof EntityItem) {
				EntityItem entityItem7 = (EntityItem)entity6;
				if(entityItem7 != null && entityItem7.age < 50 && ((Boolean)mod_mocreatures.destroyitems.get()).booleanValue()) {
					entityItem7.setEntityDead();
				}
			}
		}

	}

	public void setEntityDead() {
		super.setEntityDead();
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.boarfreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}
}
