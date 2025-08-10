package net.minecraft.src;

import java.util.List;

public class EntityShark extends EntityCustomWM {
	public float b;
	public boolean adult;
	public boolean tamed;

	public EntityShark(World world1) {
		super(world1);
		this.texture = "/mob/shark.png";
		this.setSize(1.8F, 1.3F);
		this.b = 1.0F + this.rand.nextFloat();
		this.adult = false;
		this.tamed = false;
		this.health = 25;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.adult && this.rand.nextInt(50) == 0) {
			this.b += 0.01F;
			if(this.b >= 2.0F) {
				this.adult = true;
			}
		}

	}

	protected Entity findPlayerToAttack() {
		if(this.worldObj.difficultySetting > 0 && this.b >= 1.0F) {
			EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
			if(entityPlayer1 != null && entityPlayer1.inWater && !this.tamed) {
				return entityPlayer1;
			}

			if(this.rand.nextInt(30) == 0) {
				EntityLiving entityLiving2 = this.FindTarget(this, 16.0D);
				if(entityLiving2 != null && entityLiving2.inWater) {
					return entityLiving2;
				}
			}
		}

		return null;
	}

	public EntityLiving FindTarget(Entity entity1, double d2) {
		double d4 = -1.0D;
		EntityLiving entityLiving6 = null;
		List list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d2, d2, d2));

		for(int i8 = 0; i8 < list7.size(); ++i8) {
			Entity entity9 = (Entity)list7.get(i8);
			if(entity9 instanceof EntityLiving && !(entity9 instanceof EntityShark) && !(entity9 instanceof EntitySharkEgg) && !(entity9 instanceof EntityPlayer) && (!(entity9 instanceof EntityWolf) || ((Boolean)mod_mocreatures.attackwolves.get()).booleanValue()) && (!(entity9 instanceof EntityHorse) || ((Boolean)mod_mocreatures.attackhorses.get()).booleanValue()) && (!(entity9 instanceof EntityDolphin) || !this.tamed && ((Boolean)mod_mocreatures.attackdolphins.get()).booleanValue())) {
				double d10 = entity9.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
				if((d2 < 0.0D || d10 < d2 * d2) && (d4 == -1.0D || d10 < d4) && ((EntityLiving)entity9).canEntityBeSeen(entity1)) {
					d4 = d10;
					entityLiving6 = (EntityLiving)entity9;
				}
			}
		}

		return entityLiving6;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(super.attackEntityFrom(entity1, i2) && this.worldObj.difficultySetting > 0) {
			if(this.riddenByEntity != entity1 && this.ridingEntity != entity1) {
				if(entity1 != this) {
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
		if((double)f2 < 3.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY && this.b >= 1.0F) {
			this.attackTime = 20;
			entity1.attackEntityFrom(this, 5);
			if(!(entity1 instanceof EntityPlayer)) {
				this.destroyDrops(this, 3.0D);
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("Tamed", this.tamed);
		nBTTagCompound1.setBoolean("Adult", this.adult);
		nBTTagCompound1.setFloat("Age", this.b);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.tamed = nBTTagCompound1.getBoolean("Tamed");
		this.adult = nBTTagCompound1.getBoolean("Adult");
		this.b = nBTTagCompound1.getFloat("Age");
	}

	public boolean Despawn() {
		return !this.tamed;
	}

	protected void dropFewItems() {
		int i1 = this.rand.nextInt(100);
		int i2;
		int i3;
		if(i1 < 90) {
			i2 = this.rand.nextInt(3) + 1;

			for(i3 = 0; i3 < i2; ++i3) {
				this.entityDropItem(new ItemStack(mod_mocreatures.sharkteeth, 1, 0), 0.0F);
			}
		} else if(this.worldObj.difficultySetting > 0 && this.b > 1.5F) {
			i2 = this.rand.nextInt(3) + 1;

			for(i3 = 0; i3 < i2; ++i3) {
				this.entityDropItem(new ItemStack(mod_mocreatures.sharkegg, 1, 0), 0.0F);
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
		if(!this.tamed || this.health <= 0) {
			super.setEntityDead();
		}
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.sharkfreq.get()).intValue() > 0 && this.worldObj.difficultySetting >= ((Integer)mod_mocreatures.sharkSpawnDifficulty.get()).intValue() + 1 && super.getCanSpawnHere();
	}
}
