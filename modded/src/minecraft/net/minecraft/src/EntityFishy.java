package net.minecraft.src;

import java.util.List;

public class EntityFishy extends EntityCustomWM {
	public int typeint;
	public boolean typechosen;
	public float b;
	public boolean adult;
	public boolean tamed;
	public int gestationtime;
	public boolean eaten;
	public boolean hungry;

	public EntityFishy(World world1) {
		super(world1);
		this.texture = "/mob/fishy1.png";
		this.setSize(0.3F, 0.3F);
		this.health = 4;
		this.typeint = 0;
		this.typechosen = false;
		this.b = 1.0F;
		this.adult = false;
		this.tamed = false;
	}

	public void setTame() {
		this.tamed = true;
	}

	public boolean istamed() {
		return this.tamed;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.adult && this.rand.nextInt(100) == 0) {
			this.b += 0.02F;
			if(this.b >= 1.0F) {
				this.adult = true;
			}
		}

		if(!this.hungry && this.rand.nextInt(100) == 0) {
			this.hungry = true;
		}

		if(!this.tamed || this.hungry) {
			EntityItem entityItem1 = this.getClosestFish(this, 12.0D);
			if(entityItem1 != null) {
				this.MoveToNextEntity(entityItem1);
				EntityItem entityItem2 = this.getClosestFish(this, 3.0D);
				if(this.rand.nextInt(20) == 0 && entityItem2 != null) {
					entityItem2.setEntityDead();
					this.eaten = true;
				}
			}
		}

		if(this.ReadyforParenting(this)) {
			int i10 = 0;
			List list11 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(4.0D, 3.0D, 4.0D));

			for(int i3 = 0; i3 < list11.size(); ++i3) {
				Entity entity4 = (Entity)list11.get(i3);
				if(entity4 instanceof EntityFishy) {
					++i10;
				}
			}

			if(i10 <= 1) {
				List list12 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(4.0D, 2.0D, 4.0D));

				for(int i13 = 0; i13 < list11.size(); ++i13) {
					Entity entity5 = (Entity)list12.get(i13);
					if(entity5 instanceof EntityFishy && entity5 != this) {
						EntityFishy entityFishy6 = (EntityFishy)entity5;
						if(this.ReadyforParenting(this) && this.ReadyforParenting(entityFishy6) && this.typeint == entityFishy6.typeint) {
							if(this.rand.nextInt(100) == 0) {
								++this.gestationtime;
							}

							if(this.gestationtime > 50) {
								int i7 = this.rand.nextInt(3) + 1;

								for(int i8 = 0; i8 < i7; ++i8) {
									EntityFishy entityFishy9 = new EntityFishy(this.worldObj);
									entityFishy9.setPosition(this.posX, this.posY, this.posZ);
									this.worldObj.entityJoinedWorld(entityFishy9);
									this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
									this.eaten = false;
									entityFishy6.eaten = false;
									this.gestationtime = 0;
									entityFishy6.gestationtime = 0;
									entityFishy9.tamed = true;
									entityFishy9.b = 0.2F;
									entityFishy9.adult = false;
									entityFishy9.setType(this.typeint);
								}

								return;
							}
						}
					}
				}

			}
		}
	}

	public boolean ReadyforParenting(EntityFishy entityFishy1) {
		return entityFishy1.tamed && entityFishy1.eaten && entityFishy1.adult;
	}

	protected Entity findPlayerToAttack() {
		if(this.worldObj.difficultySetting > 0 && this.b >= 1.0F && this.typeint == 10) {
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
			if(entity9 instanceof EntityLiving && !(entity9 instanceof EntityCustomWM) && !(entity9 instanceof EntitySharkEgg) && !(entity9 instanceof EntityFishyEgg) && !(entity9 instanceof EntityPlayer) && (!(entity9 instanceof EntityHorse) || ((Boolean)mod_mocreatures.attackhorses.get()).booleanValue()) && (!(entity9 instanceof EntityWolf) || ((Boolean)mod_mocreatures.attackwolves.get()).booleanValue())) {
				double d10 = entity9.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
				if((d2 < 0.0D || d10 < d2 * d2) && (d4 == -1.0D || d10 < d4) && ((EntityLiving)entity9).canEntityBeSeen(entity1)) {
					d4 = d10;
					entityLiving6 = (EntityLiving)entity9;
				}
			}
		}

		return entityLiving6;
	}

	public void setEntityDead() {
		if(!this.tamed || this.health <= 0) {
			super.setEntityDead();
		}
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(super.attackEntityFrom(entity1, i2)) {
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
		if((double)f2 < 2.0D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity1.attackEntityFrom(this, 1);
		}

	}

	public void setType(int i1) {
		this.typeint = i1;
		this.typechosen = false;
		this.chooseType();
	}

	public void chooseType() {
		if(this.typeint == 0) {
			int i1 = this.rand.nextInt(100);
			if(i1 <= 9) {
				this.typeint = 1;
			} else if(i1 <= 19) {
				this.typeint = 2;
			} else if(i1 <= 29) {
				this.typeint = 3;
			} else if(i1 <= 39) {
				this.typeint = 4;
			} else if(i1 <= 49) {
				this.typeint = 5;
			} else if(i1 <= 59) {
				this.typeint = 6;
			} else if(i1 <= 69) {
				this.typeint = 7;
			} else if(i1 <= 79) {
				this.typeint = 8;
			} else if(i1 <= 89) {
				this.typeint = 9;
			} else {
				this.typeint = 10;
			}

			if(!((Boolean)mod_mocreatures.spawnpiranha.get()).booleanValue() && this.typeint == 10) {
				this.typeint = 1;
			}
		}

		if(!this.typechosen) {
			if(this.typeint == 1) {
				this.texture = "/mob/fishy1.png";
			} else if(this.typeint == 2) {
				this.texture = "/mob/fishy2.png";
			} else if(this.typeint == 3) {
				this.texture = "/mob/fishy3.png";
			} else if(this.typeint == 4) {
				this.texture = "/mob/fishy4.png";
			} else if(this.typeint == 5) {
				this.texture = "/mob/fishy5.png";
			} else if(this.typeint == 6) {
				this.texture = "/mob/fishy6.png";
			} else if(this.typeint == 7) {
				this.texture = "/mob/fishy7.png";
			} else if(this.typeint == 8) {
				this.texture = "/mob/fishy8.png";
			} else if(this.typeint == 9) {
				this.texture = "/mob/fishy9.png";
			} else if(this.typeint == 10) {
				this.texture = "/mob/fishy10.png";
			}
		}

		this.typechosen = true;
	}

	protected void dropFewItems() {
		int i1 = this.rand.nextInt(100);
		if(i1 < 70 && this.adult) {
			this.entityDropItem(new ItemStack(Item.fishRaw.shiftedIndex, 1, 0), 0.0F);
		} else {
			int i2 = this.rand.nextInt(2) + 1;

			for(int i3 = 0; i3 < i2; ++i3) {
				this.entityDropItem(new ItemStack(mod_mocreatures.fishyegg, 1, 0), 0.0F);
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("Tamed", this.tamed);
		nBTTagCompound1.setInteger("TypeInt", this.typeint);
		nBTTagCompound1.setFloat("Age", this.b);
		nBTTagCompound1.setBoolean("Adult", this.adult);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.tamed = nBTTagCompound1.getBoolean("Tamed");
		this.typeint = nBTTagCompound1.getInteger("TypeInt");
		this.b = nBTTagCompound1.getFloat("Age");
		this.adult = nBTTagCompound1.getBoolean("Adult");
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.fishfreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}
}
