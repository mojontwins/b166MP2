package net.minecraft.src;

import java.util.List;

public class EntityWWolf extends EntityMob implements IMob {
	public boolean wolfboolean = false;

	public EntityWWolf(World world1) {
		super(world1);
		this.texture = "/mob/wolfa.png";
		this.setSize(0.9F, 1.3F);
		this.attackStrength = 1;
	}

	public void onLivingUpdate() {
		if(this.worldObj.difficultySetting == 1) {
			this.attackStrength = 3;
		} else if(this.worldObj.difficultySetting > 1) {
			this.attackStrength = 5;
		}

		super.onLivingUpdate();
	}

	public int getMaxSpawnedInChunk() {
		return 6;
	}

	protected Entity findPlayerToAttack() {
		float f1 = this.getEntityBrightness(1.0F);
		if(f1 < 0.5F) {
			double d4 = 16.0D;
			return this.worldObj.getClosestPlayerToEntity(this, d4);
		} else if(this.rand.nextInt(80) == 0) {
			EntityLiving entityLiving2 = this.getClosestTarget(this, 10.0D);
			return entityLiving2;
		} else {
			return null;
		}
	}

	public EntityLiving getClosestTarget(Entity entity1, double d2) {
		double d4 = -1.0D;
		EntityLiving entityLiving6 = null;
		List list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d2, d2, d2));

		for(int i8 = 0; i8 < list7.size(); ++i8) {
			Entity entity9 = (Entity)list7.get(i8);
			if(entity9 instanceof EntityLiving && entity9 != entity1 && entity9 != entity1.riddenByEntity && entity9 != entity1.ridingEntity && !(entity9 instanceof EntityPlayer) && !(entity9 instanceof EntityMob) && !(entity9 instanceof EntityBigCat) && !(entity9 instanceof EntityBear) && !(entity9 instanceof EntityCow) && (!(entity9 instanceof EntityWolf) || ((Boolean)mod_mocreatures.attackwolves.get()).booleanValue()) && (!(entity9 instanceof EntityHorse) || ((Boolean)mod_mocreatures.attackhorses.get()).booleanValue())) {
				double d10 = entity9.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
				if((d2 < 0.0D || d10 < d2 * d2) && (d4 == -1.0D || d10 < d4) && ((EntityLiving)entity9).canEntityBeSeen(entity1)) {
					d4 = d10;
					entityLiving6 = (EntityLiving)entity9;
				}
			}
		}

		return entityLiving6;
	}

	protected void attackEntity(Entity entity1, float f2) {
		if((double)f2 < 2.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity1.attackEntityFrom(this, this.attackStrength);
			if(!(entity1 instanceof EntityPlayer)) {
				this.destroyDrops(this, 3.0D);
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("WolfBoolean", this.wolfboolean);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.wolfboolean = nBTTagCompound1.getBoolean("WolfBoolean");
	}

	protected String getLivingSound() {
		return "wolfgrunt";
	}

	protected String getHurtSound() {
		return "wolfhurt";
	}

	protected String getDeathSound() {
		return "wolfdeath";
	}

	protected int getDropItemId() {
		return Item.leather.shiftedIndex;
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

	public boolean getCanSpawnHere() {
		return this.worldObj.canBlockSeeTheSky(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY), MathHelper.floor_double(this.posZ)) && ((Integer)mod_mocreatures.wwolffreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}
}
