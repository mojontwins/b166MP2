package net.minecraft.src;

import java.util.List;

public class EntityBunny extends EntityAnimal {
	public boolean a = false;
	public float b = 0.0F;
	public float c = 0.0F;
	public float d;
	public float d1;
	public float e;
	public float f = 1.0F;
	public boolean h;
	public int j;
	public int i;
	public int typeint;
	public boolean typechosen;
	public boolean tamed;
	public boolean adult = true;
	public float edad = 0.5F;

	public EntityBunny(World world1) {
		super(world1);
		this.moveSpeed = 1.5F;
		this.texture = "/mob/bunny.png";
		this.yOffset = -0.16F;
		this.setSize(0.4F, 0.4F);
		this.health = 4;
		this.j = this.rand.nextInt(64);
		this.i = 0;
		this.typeint = 0;
		this.typechosen = false;
	}

	public void setType(int i1) {
		this.typeint = i1;
		this.typechosen = false;
		this.chooseType();
	}

	public void chooseType() {
		if(this.typeint == 0) {
			int i1 = this.rand.nextInt(100);
			if(i1 <= 25) {
				this.typeint = 1;
			} else if(i1 <= 50) {
				this.typeint = 2;
			} else if(i1 <= 75) {
				this.typeint = 3;
			} else {
				this.typeint = 4;
			}
		}

		if(!this.typechosen) {
			if(this.typeint == 1) {
				this.texture = "/mob/bunny.png";
			} else if(this.typeint == 2) {
				this.texture = "/mob/bunnyb.png";
			} else if(this.typeint == 3) {
				this.texture = "/mob/bunnyc.png";
			} else if(this.typeint == 4) {
				this.texture = "/mob/bunnyd.png";
			}
		}

		this.typechosen = true;
	}

	public void onLivingUpdate() {
		if(!this.adult && this.rand.nextInt(200) == 0) {
			this.edad += 0.01F;
			if(this.edad >= 1.0F) {
				this.adult = true;
			}
		}

		super.onLivingUpdate();
		this.e = this.b;
		this.d = this.c;
		this.c = (float)((double)this.c + (double)(this.onGround ? -1 : 4) * 0.3D);
		if(this.c < 0.0F) {
			this.c = 0.0F;
		}

		if(this.c > 1.0F) {
			this.c = 1.0F;
		}

		if(!this.onGround && this.f < 1.0F) {
			this.f = 1.0F;
		}

		this.f = (float)((double)this.f * 0.9D);
		this.b += this.f * 2.0F;
	}

	public void onUpdate() {
		if(this.j < 1023) {
			++this.j;
		} else if(this.i < 127) {
			++this.i;
		} else {
			int i1 = 0;
			List list2 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(16.0D, 16.0D, 16.0D));

			for(int i3 = 0; i3 < list2.size(); ++i3) {
				Entity entity4 = (Entity)list2.get(i3);
				if(entity4 instanceof EntityBunny) {
					++i1;
				}
			}

			if(i1 > 12) {
				this.proceed();
				return;
			}

			List list9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(1.0D, 1.0D, 1.0D));
			boolean z10 = false;

			for(int i5 = 0; i5 < list2.size(); ++i5) {
				Entity entity6 = (Entity)list9.get(i5);
				if(entity6 instanceof EntityBunny && entity6 != this) {
					EntityBunny entityBunny7 = (EntityBunny)entity6;
					if(entityBunny7.ridingEntity == null && entityBunny7.j >= 1023 && entityBunny7.adult) {
						EntityBunny entityBunny8 = new EntityBunny(this.worldObj);
						entityBunny8.setPosition(this.posX, this.posY, this.posZ);
						entityBunny8.adult = false;
						entityBunny8.tamed = true;
						this.worldObj.entityJoinedWorld(entityBunny8);
						this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
						this.proceed();
						entityBunny7.proceed();
						z10 = true;
						break;
					}
				}
			}

			if(!z10) {
				i1 = this.rand.nextInt(16);
			}
		}

		super.onUpdate();
	}

	protected void fall(float f1) {
	}

	protected void updatePlayerActionState() {
		if(this.onGround && (this.motionX > 0.05D || this.motionZ > 0.05D || this.motionX < -0.05D || this.motionZ < -0.05D)) {
			this.motionY = 0.45D;
		}

		if(!this.h) {
			super.updatePlayerActionState();
		} else if(this.onGround) {
			this.h = false;
			this.worldObj.playSoundAtEntity(this, "rabbitland", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			List list1 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(12.0D, 12.0D, 12.0D));

			for(int i2 = 0; i2 < list1.size(); ++i2) {
				Entity entity3 = (Entity)list1.get(i2);
				if(entity3 instanceof EntityMob) {
					EntityMob entityMob4 = (EntityMob)entity3;
					entityMob4.playerToAttack = this;
				}
			}
		}

	}

	public boolean interact(EntityPlayer entityPlayer1) {
		this.rotationYaw = entityPlayer1.rotationYaw;
		this.mountEntity(entityPlayer1);
		if(this.ridingEntity == null) {
			this.h = true;
		} else {
			this.worldObj.playSoundAtEntity(this, "rabbitlift", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
		}

		this.motionX = entityPlayer1.motionX * 5.0D;
		this.motionY = entityPlayer1.motionY / 2.0D + 0.5D;
		this.motionZ = entityPlayer1.motionZ * 5.0D;
		return true;
	}

	public double getYOffset() {
		return this.ridingEntity instanceof EntityPlayer ? (double)(this.yOffset - 1.15F) : (double)this.yOffset;
	}

	protected String getLivingSound() {
		return null;
	}

	public void proceed() {
		this.i = 0;
		this.j = this.rand.nextInt(64);
	}

	protected String getHurtSound() {
		return "rabbithurt";
	}

	public void knockBack(Entity entity1, int i2, double d3, double d5) {
		super.knockBack(entity1, i2, d3, d5);
	}

	protected String getDeathSound() {
		return "rabbitdeath";
	}

	public boolean maxNumberReached() {
		int i1 = 0;

		for(int i2 = 0; i2 < this.worldObj.loadedEntityList.size(); ++i2) {
			Entity entity3 = (Entity)this.worldObj.loadedEntityList.get(i2);
			if(entity3 instanceof EntityBunny) {
				++i1;
			}
		}

		return false;
	}

	public void setEntityDead() {
		super.setEntityDead();
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setInteger("TypeInt", this.typeint);
		nBTTagCompound1.setBoolean("Tamed", this.tamed);
		nBTTagCompound1.setFloat("Edad", this.edad);
		nBTTagCompound1.setBoolean("Adult", this.adult);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.typeint = nBTTagCompound1.getInteger("TypeInt");
		this.edad = nBTTagCompound1.getFloat("Edad");
		this.tamed = nBTTagCompound1.getBoolean("Tamed");
		this.adult = nBTTagCompound1.getBoolean("Adult");
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.bunnyfreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}

	protected boolean canDespawn() {
		return !this.tamed;
	}
}
