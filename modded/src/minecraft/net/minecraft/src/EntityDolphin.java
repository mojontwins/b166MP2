package net.minecraft.src;

import java.util.List;

public class EntityDolphin extends EntityCustomWM {
	public int gestationtime;
	public boolean bred;
	public float b;
	public boolean adult;
	public boolean tamed;
	public int typeint;
	private double dolphinspeed;
	private int maxhealth;
	private int temper;
	private boolean eaten;
	public boolean typechosen;
	public boolean hungry;

	public EntityDolphin(World world1) {
		super(world1);
		this.texture = "/mob/dolphin.png";
		this.setSize(1.5F, 0.8F);
		this.b = 0.8F + this.rand.nextFloat();
		this.adult = false;
		this.tamed = false;
		this.dolphinspeed = 1.3D;
		this.maxhealth = 30;
		this.temper = 50;
	}

	public void setTame() {
		this.tamed = true;
	}

	public double speed() {
		return this.dolphinspeed;
	}

	public int tametemper() {
		return this.temper;
	}

	public boolean istamed() {
		return this.tamed;
	}

	public void setType(int i1) {
		this.typeint = i1;
		this.typechosen = false;
		this.chooseType();
	}

	public void chooseType() {
		if(this.typeint == 0) {
			int i1 = this.rand.nextInt(100);
			if(i1 <= 35) {
				this.typeint = 1;
			} else if(i1 <= 60) {
				this.typeint = 2;
			} else if(i1 <= 85) {
				this.typeint = 3;
			} else if(i1 <= 96) {
				this.typeint = 4;
			} else if(i1 <= 98) {
				this.typeint = 5;
			} else {
				this.typeint = 6;
			}
		}

		if(!this.typechosen) {
			if(this.typeint == 1) {
				this.texture = "/mob/dolphin.png";
				this.dolphinspeed = 1.5D;
				this.temper = 50;
			} else if(this.typeint == 2) {
				this.texture = "/mob/dolphin2.png";
				this.dolphinspeed = 2.5D;
				this.temper = 100;
			} else if(this.typeint == 3) {
				this.texture = "/mob/dolphin3.png";
				this.dolphinspeed = 3.5D;
				this.temper = 150;
			} else if(this.typeint == 4) {
				this.texture = "/mob/dolphin4.png";
				this.dolphinspeed = 4.5D;
				this.temper = 200;
			} else if(this.typeint == 5) {
				this.texture = "/mob/dolphin5.png";
				this.dolphinspeed = 5.5D;
				this.temper = 250;
			} else if(this.typeint == 6) {
				this.texture = "/mob/dolphin6.png";
				this.dolphinspeed = 6.5D;
				this.temper = 300;
			}
		}

		this.typechosen = true;
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		if(itemStack2 != null && itemStack2.itemID == Item.fishRaw.shiftedIndex) {
			if(--itemStack2.stackSize == 0) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
			}

			if((this.temper -= 25) < 1) {
				this.temper = 1;
			}

			if((this.health += 15) > this.maxhealth) {
				this.health = this.maxhealth;
			}

			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			if(!this.adult) {
				this.b += 0.01F;
			}

			return true;
		} else if(itemStack2 != null && itemStack2.itemID == Item.fishCooked.shiftedIndex && this.tamed && this.adult) {
			if(--itemStack2.stackSize == 0) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
			}

			if((this.health += 25) > this.maxhealth) {
				this.health = this.maxhealth;
			}

			this.eaten = true;
			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			return true;
		} else if(this.adult) {
			entityPlayer1.rotationYaw = this.rotationYaw;
			entityPlayer1.rotationPitch = this.rotationPitch;
			entityPlayer1.posY = this.posY;
			entityPlayer1.mountEntity(this);
			return true;
		} else {
			return false;
		}
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.adult && this.rand.nextInt(50) == 0) {
			this.b += 0.01F;
			if(this.b >= 1.5F) {
				this.adult = true;
			}
		}

		if(!this.hungry && this.rand.nextInt(100) == 0) {
			this.hungry = true;
		}

		if(this.deathTime == 0 && !this.tamed || this.hungry) {
			EntityItem entityItem1 = this.getClosestFish(this, 12.0D);
			if(entityItem1 != null) {
				this.MoveToNextEntity(entityItem1);
				EntityItem entityItem2 = this.getClosestFish(this, 2.0D);
				if(this.rand.nextInt(20) == 0 && entityItem2 != null && this.deathTime == 0) {
					entityItem2.setEntityDead();
					if((this.temper -= 25) < 1) {
						this.temper = 1;
					}

					this.health = this.maxhealth;
				}
			}
		}

		if(this.ReadyforParenting(this)) {
			int i9 = 0;
			List list10 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(8.0D, 2.0D, 8.0D));

			for(int i3 = 0; i3 < list10.size(); ++i3) {
				Entity entity4 = (Entity)list10.get(i3);
				if(entity4 instanceof EntityDolphin) {
					++i9;
				}
			}

			if(i9 <= 1) {
				List list11 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(4.0D, 2.0D, 4.0D));

				for(int i12 = 0; i12 < list10.size(); ++i12) {
					Entity entity5 = (Entity)list11.get(i12);
					if(entity5 instanceof EntityDolphin && entity5 != this) {
						EntityDolphin entityDolphin6 = (EntityDolphin)entity5;
						if(this.ReadyforParenting(this) && this.ReadyforParenting(entityDolphin6)) {
							if(this.rand.nextInt(100) == 0) {
								++this.gestationtime;
							}

							if(this.gestationtime > 50) {
								EntityDolphin entityDolphin7 = new EntityDolphin(this.worldObj);
								entityDolphin7.setPosition(this.posX, this.posY, this.posZ);
								this.worldObj.entityJoinedWorld(entityDolphin7);
								this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
								this.eaten = false;
								entityDolphin6.eaten = false;
								this.gestationtime = 0;
								entityDolphin6.gestationtime = 0;
								int i8 = this.Genetics(this, entityDolphin6);
								entityDolphin7.bred = true;
								entityDolphin7.b = 0.35F;
								entityDolphin7.adult = false;
								entityDolphin7.setType(i8);
								break;
							}
						}
					}
				}

			}
		}
	}

	public boolean ReadyforParenting(EntityDolphin entityDolphin1) {
		return entityDolphin1.riddenByEntity == null && entityDolphin1.ridingEntity == null && entityDolphin1.tamed && entityDolphin1.eaten && entityDolphin1.adult;
	}

	private int Genetics(EntityDolphin entityDolphin1, EntityDolphin entityDolphin2) {
		if(entityDolphin1.typeint == entityDolphin2.typeint) {
			return entityDolphin1.typeint;
		} else {
			int i3 = entityDolphin1.typeint + entityDolphin2.typeint;
			boolean z4 = this.rand.nextInt(3) == 0;
			boolean z5 = this.rand.nextInt(10) == 0;
			return i3 < 5 && z4 ? i3 : ((i3 == 5 || i3 == 6) && z5 ? i3 : 0);
		}
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("Tamed", this.tamed);
		nBTTagCompound1.setInteger("TypeInt", this.typeint);
		nBTTagCompound1.setBoolean("Adult", this.adult);
		nBTTagCompound1.setBoolean("Bred", this.bred);
		nBTTagCompound1.setFloat("Age", this.b);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.tamed = nBTTagCompound1.getBoolean("Tamed");
		this.typeint = nBTTagCompound1.getInteger("TypeInt");
		this.adult = nBTTagCompound1.getBoolean("Adult");
		this.bred = nBTTagCompound1.getBoolean("Bred");
		this.b = nBTTagCompound1.getFloat("Age");
	}

	protected Entity findPlayerToAttack() {
		if(this.worldObj.difficultySetting > 0 && this.b >= 1.0F && ((Boolean)mod_mocreatures.attackdolphins.get()).booleanValue() && this.rand.nextInt(50) == 0) {
			EntityLiving entityLiving1 = this.FindTarget(this, 12.0D);
			if(entityLiving1 != null && entityLiving1.inWater) {
				return entityLiving1;
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
			if(entity9 instanceof EntityShark && ((Boolean)mod_mocreatures.attackdolphins.get()).booleanValue()) {
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
		}

	}

	protected int getDropItemId() {
		return Item.fishRaw.shiftedIndex;
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.dolphinfreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}

	public void setEntityDead() {
		if(!this.tamed && !this.bred || this.health <= 0) {
			super.setEntityDead();
		}
	}

	protected String getLivingSound() {
		return "dolphin";
	}

	protected String getHurtSound() {
		return "dolphinhurt";
	}

	protected String getDeathSound() {
		return "dolphindying";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected String getUpsetSound() {
		return "dolphinupset";
	}
}
