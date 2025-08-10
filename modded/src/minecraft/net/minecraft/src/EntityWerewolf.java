package net.minecraft.src;

public class EntityWerewolf extends EntityMob implements IMob {
	public boolean wereboolean = false;
	public boolean humanform;
	private boolean transforming;
	private int tcounter;
	public boolean hunched;
	public boolean isUndead;

	public EntityWerewolf(World world1) {
		super(world1);
		this.texture = "/mob/werehuman.png";
		this.setSize(0.9F, 1.3F);
		this.humanform = true;
		this.health = 15;
		this.transforming = false;
		this.tcounter = 0;
		this.hunched = false;
		this.isUndead = true;
	}

	protected Entity findPlayerToAttack() {
		if(this.humanform) {
			return null;
		} else {
			EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
			return entityPlayer1 != null && this.canEntityBeSeen(entityPlayer1) ? entityPlayer1 : null;
		}
	}

	protected void updatePlayerActionState() {
		if(!this.transforming) {
			super.updatePlayerActionState();
		}

	}

	protected void attackEntity(Entity entity1, float f2) {
		if(this.humanform) {
			this.playerToAttack = null;
		} else {
			if(f2 > 2.0F && f2 < 6.0F && this.rand.nextInt(15) == 0) {
				if(this.onGround) {
					this.hunched = true;
					double d3 = entity1.posX - this.posX;
					double d5 = entity1.posZ - this.posZ;
					float f7 = MathHelper.sqrt_double(d3 * d3 + d5 * d5);
					this.motionX = d3 / (double)f7 * 0.5D * (double)0.8F + this.motionX * 0.2000000029802322D;
					this.motionZ = d5 / (double)f7 * 0.5D * (double)0.8F + this.motionZ * 0.2000000029802322D;
					this.motionY = (double)0.4F;
				}
			} else {
				super.attackEntity(entity1, f2);
			}

		}
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(!this.humanform && entity1 != null && entity1 instanceof EntityPlayer) {
			EntityPlayer entityPlayer3 = (EntityPlayer)entity1;
			ItemStack itemStack4 = entityPlayer3.getCurrentEquippedItem();
			if(itemStack4 != null) {
				i2 = 1;
				if(itemStack4.itemID == Item.hoeGold.shiftedIndex) {
					i2 = 6;
				}

				if(itemStack4.itemID == Item.shovelGold.shiftedIndex) {
					i2 = 7;
				}

				if(itemStack4.itemID == Item.pickaxeGold.shiftedIndex) {
					i2 = 8;
				}

				if(itemStack4.itemID == Item.axeGold.shiftedIndex) {
					i2 = 9;
				}

				if(itemStack4.itemID == Item.swordGold.shiftedIndex) {
					i2 = 10;
				}
			}
		}

		return super.attackEntityFrom(entity1, i2);
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if((this.IsNight() && this.humanform || !this.IsNight() && !this.humanform) && this.rand.nextInt(250) == 0) {
			this.transforming = true;
		}

		if(this.humanform && this.playerToAttack != null) {
			this.playerToAttack = null;
		}

		if(this.playerToAttack != null && !this.humanform && this.playerToAttack.posX - this.posX > 3.0D && this.playerToAttack.posZ - this.posZ > 3.0D) {
			this.hunched = true;
		}

		if(this.hunched && this.rand.nextInt(50) == 0) {
			this.hunched = false;
		}

		if(this.transforming && this.rand.nextInt(3) == 0) {
			++this.tcounter;
			if(this.tcounter % 2 == 0) {
				this.posX += 0.3D;
				this.posY += (double)(this.tcounter / 30);
				this.attackEntityFrom(this, 1);
			}

			if(this.tcounter % 2 != 0) {
				this.posX -= 0.3D;
			}

			if(this.tcounter == 10) {
				this.worldObj.playSoundAtEntity(this, "weretransform", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			if(this.tcounter > 30) {
				this.Transform();
				this.tcounter = 0;
				this.transforming = false;
			}
		}

		if(this.rand.nextInt(300) == 0) {
			this.entityAge -= 100 * this.worldObj.difficultySetting;
			if(this.entityAge < 0) {
				this.entityAge = 0;
			}
		}

	}

	public boolean IsNight() {
		return !this.worldObj.isDaytime();
	}

	public void moveEntityWithHeading(float f1, float f2) {
		if(!this.humanform && this.onGround) {
			this.motionX *= 1.2D;
			this.motionZ *= 1.2D;
		}

		super.moveEntityWithHeading(f1, f2);
	}

	private void Transform() {
		if(this.deathTime <= 0) {
			int i1 = MathHelper.floor_double(this.posX);
			int i2 = MathHelper.floor_double(this.boundingBox.minY) + 1;
			int i3 = MathHelper.floor_double(this.posZ);
			float f4 = 0.1F;

			for(int i5 = 0; i5 < 30; ++i5) {
				double d6 = (double)((float)i1 + this.worldObj.rand.nextFloat());
				double d8 = (double)((float)i2 + this.worldObj.rand.nextFloat());
				double d10 = (double)((float)i3 + this.worldObj.rand.nextFloat());
				double d12 = d6 - (double)i1;
				double d14 = d8 - (double)i2;
				double d16 = d10 - (double)i3;
				double d18 = (double)MathHelper.sqrt_double(d12 * d12 + d14 * d14 + d16 * d16);
				d12 /= d18;
				d14 /= d18;
				d16 /= d18;
				double d20 = 0.5D / (d18 / (double)f4 + 0.1D);
				d20 *= (double)(this.worldObj.rand.nextFloat() * this.worldObj.rand.nextFloat() + 0.3F);
				d12 *= d20;
				d14 *= d20;
				d16 *= d20;
				this.worldObj.spawnParticle("explode", (d6 + (double)i1 * 1.0D) / 2.0D, (d8 + (double)i2 * 1.0D) / 2.0D, (d10 + (double)i3 * 1.0D) / 2.0D, d12, d14, d16);
			}

			if(this.humanform) {
				this.humanform = false;
				this.health = 40;
				this.transforming = false;
			} else {
				this.humanform = true;
				this.health = 15;
				this.transforming = false;
			}

		}
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("HumanForm", this.humanform);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.humanform = nBTTagCompound1.getBoolean("HumanForm");
	}

	protected String getLivingSound() {
		return this.humanform ? "werehumangrunt" : "werewolfgrunt";
	}

	protected String getHurtSound() {
		return this.humanform ? "werehumanhurt" : "werewolfhurt";
	}

	protected String getDeathSound() {
		return this.humanform ? "werehumandying" : "werewolfdying";
	}

	protected int getDropItemId() {
		int i1 = this.rand.nextInt(12);
		if(this.humanform) {
			switch(i1) {
			case 0:
				return Item.shovelWood.shiftedIndex;
			case 1:
				return Item.axeWood.shiftedIndex;
			case 2:
				return Item.swordWood.shiftedIndex;
			case 3:
				return Item.hoeWood.shiftedIndex;
			case 4:
				return Item.pickaxeWood.shiftedIndex;
			default:
				return Item.stick.shiftedIndex;
			}
		} else {
			switch(i1) {
			case 0:
				return Item.hoeSteel.shiftedIndex;
			case 1:
				return Item.shovelSteel.shiftedIndex;
			case 2:
				return Item.axeSteel.shiftedIndex;
			case 3:
				return Item.pickaxeSteel.shiftedIndex;
			case 4:
				return Item.swordSteel.shiftedIndex;
			case 5:
				return Item.hoeStone.shiftedIndex;
			case 6:
				return Item.shovelStone.shiftedIndex;
			case 7:
				return Item.axeStone.shiftedIndex;
			case 8:
				return Item.pickaxeStone.shiftedIndex;
			case 9:
				return Item.swordStone.shiftedIndex;
			default:
				return Item.appleGold.shiftedIndex;
			}
		}
	}

	public void onDeath(Entity entity1) {
		if(this.scoreValue > 0 && entity1 != null) {
			entity1.addToPlayerScore(this, this.scoreValue);
		}

		if(entity1 != null) {
			entity1.onKillEntity(this);
		}

		this.unused_flag = true;
		if(!this.worldObj.multiplayerWorld) {
			for(int i2 = 0; i2 < 2; ++i2) {
				int i3 = this.getDropItemId();
				if(i3 > 0) {
					this.dropItem(i3, 1);
				}
			}
		}

		this.worldObj.func_4079_a(this, 3.0F);
	}

	public int getMaxSpawnedInChunk() {
		return 1;
	}

	public void setEntityDead() {
		super.setEntityDead();
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.werewolffreq.get()).intValue() > 0 && this.worldObj.difficultySetting >= ((Integer)mod_mocreatures.wereSpawnDifficulty.get()).intValue() + 1 && super.getCanSpawnHere();
	}
}
