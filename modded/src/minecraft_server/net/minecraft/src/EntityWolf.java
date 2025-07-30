package net.minecraft.src;

import java.util.Iterator;
import java.util.List;

public class EntityWolf extends EntityAnimal {
	private boolean field_25039_a = false;
	private float field_25038_b;
	private float field_25044_c;
	private boolean isWet;
	private boolean field_25042_g;
	private float field_25041_h;
	private float field_25040_i;

	public EntityWolf(World world1) {
		super(world1);
		this.texture = "/mob/wolf.png";
		this.setSize(0.8F, 0.8F);
		this.moveSpeed = 1.1F;
		this.health = 8;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(16, (byte)0);
		this.dataWatcher.addObject(17, "");
		this.dataWatcher.addObject(18, new Integer(this.health));
	}

	protected boolean func_25017_l() {
		return false;
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("Angry", this.getIsAngry());
		nBTTagCompound1.setBoolean("Sitting", this.getIsSitting());
		if(this.getOwner() == null) {
			nBTTagCompound1.setString("Owner", "");
		} else {
			nBTTagCompound1.setString("Owner", this.getOwner());
		}

	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.setIsAngry(nBTTagCompound1.getBoolean("Angry"));
		this.setIsSitting(nBTTagCompound1.getBoolean("Sitting"));
		String string2 = nBTTagCompound1.getString("Owner");
		if(string2.length() > 0) {
			this.setOwner(string2);
			this.setIsTamed(true);
		}

	}

	protected boolean func_25020_s() {
		return !this.func_25030_y();
	}

	protected String getLivingSound() {
		return this.getIsAngry() ? "mob.wolf.growl" : (this.rand.nextInt(3) == 0 ? (this.func_25030_y() && this.dataWatcher.getWatchableObjectInteger(18) < 10 ? "mob.wolf.whine" : "mob.wolf.panting") : "mob.wolf.bark");
	}

	protected String getHurtSound() {
		return "mob.wolf.hurt";
	}

	protected String getDeathSound() {
		return "mob.wolf.death";
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected int getDropItemId() {
		return -1;
	}

	protected void updatePlayerActionState() {
		super.updatePlayerActionState();
		if(!this.hasAttacked && !this.getGotPath() && this.func_25030_y() && this.ridingEntity == null) {
			EntityPlayer entityPlayer3 = this.worldObj.getPlayerEntityByName(this.getOwner());
			if(entityPlayer3 != null) {
				float f2 = entityPlayer3.getDistanceToEntity(this);
				if(f2 > 5.0F) {
					this.setPathEntity(entityPlayer3, f2);
				}
			} else if(!this.func_27011_Z()) {
				this.setIsSitting(true);
			}
		} else if(this.playerToAttack == null && !this.getGotPath() && !this.func_25030_y() && this.worldObj.rand.nextInt(100) == 0) {
			List list1 = this.worldObj.getEntitiesWithinAABB(EntitySheep.class, AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
			if(!list1.isEmpty()) {
				this.setEntityToAttack((Entity)list1.get(this.worldObj.rand.nextInt(list1.size())));
			}
		}

		if(this.func_27011_Z()) {
			this.setIsSitting(false);
		}

		if(!this.worldObj.singleplayerWorld) {
			this.dataWatcher.updateObject(18, this.health);
		}

	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.field_25039_a = false;
		if(this.func_25021_O() && !this.getGotPath() && !this.getIsAngry()) {
			Entity entity1 = this.getCurrentTarget();
			if(entity1 instanceof EntityPlayer) {
				EntityPlayer entityPlayer2 = (EntityPlayer)entity1;
				ItemStack itemStack3 = entityPlayer2.inventory.getCurrentItem();
				if(itemStack3 != null) {
					if(!this.func_25030_y() && itemStack3.itemID == Item.bone.shiftedIndex) {
						this.field_25039_a = true;
					} else if(this.func_25030_y() && Item.itemsList[itemStack3.itemID] instanceof ItemFood) {
						this.field_25039_a = ((ItemFood)Item.itemsList[itemStack3.itemID]).func_25010_k();
					}
				}
			}
		}

		if(!this.field_9112_aN && this.isWet && !this.field_25042_g && !this.getGotPath() && this.onGround) {
			this.field_25042_g = true;
			this.field_25041_h = 0.0F;
			this.field_25040_i = 0.0F;
			this.worldObj.func_9206_a(this, (byte)8);
		}

	}

	public void onUpdate() {
		super.onUpdate();
		this.field_25044_c = this.field_25038_b;
		if(this.field_25039_a) {
			this.field_25038_b += (1.0F - this.field_25038_b) * 0.4F;
		} else {
			this.field_25038_b += (0.0F - this.field_25038_b) * 0.4F;
		}

		if(this.field_25039_a) {
			this.numTicksToChaseTarget = 10;
		}

		if(this.func_27008_Y()) {
			this.isWet = true;
			this.field_25042_g = false;
			this.field_25041_h = 0.0F;
			this.field_25040_i = 0.0F;
		} else if((this.isWet || this.field_25042_g) && this.field_25042_g) {
			if(this.field_25041_h == 0.0F) {
				this.worldObj.playSoundAtEntity(this, "mob.wolf.shake", this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
			}

			this.field_25040_i = this.field_25041_h;
			this.field_25041_h += 0.05F;
			if(this.field_25040_i >= 2.0F) {
				this.isWet = false;
				this.field_25042_g = false;
				this.field_25040_i = 0.0F;
				this.field_25041_h = 0.0F;
			}

			if(this.field_25041_h > 0.4F) {
				float f1 = (float)this.boundingBox.minY;
				int i2 = (int)(MathHelper.sin((this.field_25041_h - 0.4F) * (float)Math.PI) * 7.0F);

				for(int i3 = 0; i3 < i2; ++i3) {
					float f4 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					float f5 = (this.rand.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
					this.worldObj.spawnParticle("splash", this.posX + (double)f4, (double)(f1 + 0.8F), this.posZ + (double)f5, this.motionX, this.motionY, this.motionZ);
				}
			}
		}

	}

	public float getEyeHeight() {
		return this.height * 0.8F;
	}

	protected int func_25018_n_() {
		return this.getIsSitting() ? 20 : super.func_25018_n_();
	}

	private void setPathEntity(Entity entity1, float f2) {
		PathEntity pathEntity3 = this.worldObj.getPathToEntity(this, entity1, 16.0F);
		if(pathEntity3 == null && f2 > 12.0F) {
			int i4 = MathHelper.floor_double(entity1.posX) - 2;
			int i5 = MathHelper.floor_double(entity1.posZ) - 2;
			int i6 = MathHelper.floor_double(entity1.boundingBox.minY);

			for(int i7 = 0; i7 <= 4; ++i7) {
				for(int i8 = 0; i8 <= 4; ++i8) {
					if((i7 < 1 || i8 < 1 || i7 > 3 || i8 > 3) && this.worldObj.isBlockOpaqueCube(i4 + i7, i6 - 1, i5 + i8) && !this.worldObj.isBlockOpaqueCube(i4 + i7, i6, i5 + i8) && !this.worldObj.isBlockOpaqueCube(i4 + i7, i6 + 1, i5 + i8)) {
						this.setLocationAndAngles((double)((float)(i4 + i7) + 0.5F), (double)i6, (double)((float)(i5 + i8) + 0.5F), this.rotationYaw, this.rotationPitch);
						return;
					}
				}
			}
		} else {
			this.setPathToEntity(pathEntity3);
		}

	}

	protected boolean func_25026_u() {
		return this.getIsSitting() || this.field_25042_g;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		this.setIsSitting(false);
		if(entity1 != null && !(entity1 instanceof EntityPlayer) && !(entity1 instanceof EntityArrow)) {
			i2 = (i2 + 1) / 2;
		}

		if(!super.attackEntityFrom((Entity)entity1, i2)) {
			return false;
		} else {
			if(!this.func_25030_y() && !this.getIsAngry()) {
				if(entity1 instanceof EntityPlayer) {
					this.setIsAngry(true);
					this.playerToAttack = (Entity)entity1;
				}

				if(entity1 instanceof EntityArrow && ((EntityArrow)entity1).owner != null) {
					entity1 = ((EntityArrow)entity1).owner;
				}

				if(entity1 instanceof EntityLiving) {
					List list3 = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getBoundingBoxFromPool(this.posX, this.posY, this.posZ, this.posX + 1.0D, this.posY + 1.0D, this.posZ + 1.0D).expand(16.0D, 4.0D, 16.0D));
					Iterator iterator4 = list3.iterator();

					while(iterator4.hasNext()) {
						Entity entity5 = (Entity)iterator4.next();
						EntityWolf entityWolf6 = (EntityWolf)entity5;
						if(!entityWolf6.func_25030_y() && entityWolf6.playerToAttack == null) {
							entityWolf6.playerToAttack = (Entity)entity1;
							if(entity1 instanceof EntityPlayer) {
								entityWolf6.setIsAngry(true);
							}
						}
					}
				}
			} else if(entity1 != this && entity1 != null) {
				if(this.func_25030_y() && entity1 instanceof EntityPlayer && ((EntityPlayer)entity1).username.equalsIgnoreCase(this.getOwner())) {
					return true;
				}

				this.playerToAttack = (Entity)entity1;
			}

			return true;
		}
	}

	protected Entity findPlayerToAttack() {
		return this.getIsAngry() ? this.worldObj.getClosestPlayerToEntity(this, 16.0D) : null;
	}

	protected void attackEntity(Entity entity1, float f2) {
		if(f2 > 2.0F && f2 < 6.0F && this.rand.nextInt(10) == 0) {
			if(this.onGround) {
				double d8 = entity1.posX - this.posX;
				double d5 = entity1.posZ - this.posZ;
				float f7 = MathHelper.sqrt_double(d8 * d8 + d5 * d5);
				this.motionX = d8 / (double)f7 * 0.5D * (double)0.8F + this.motionX * (double)0.2F;
				this.motionZ = d5 / (double)f7 * 0.5D * (double)0.8F + this.motionZ * (double)0.2F;
				this.motionY = (double)0.4F;
			}
		} else if((double)f2 < 1.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			byte b3 = 2;
			if(this.func_25030_y()) {
				b3 = 4;
			}

			entity1.attackEntityFrom(this, b3);
		}

	}

	public boolean interact(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		if(!this.func_25030_y()) {
			if(itemStack2 != null && itemStack2.itemID == Item.bone.shiftedIndex && !this.getIsAngry()) {
				--itemStack2.stackSize;
				if(itemStack2.stackSize <= 0) {
					entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
				}

				if(!this.worldObj.singleplayerWorld) {
					if(this.rand.nextInt(3) == 0) {
						this.setIsTamed(true);
						this.setPathToEntity((PathEntity)null);
						this.setIsSitting(true);
						this.health = 20;
						this.setOwner(entityPlayer1.username);
						this.isNowTamed(true);
						this.worldObj.func_9206_a(this, (byte)7);
					} else {
						this.isNowTamed(false);
						this.worldObj.func_9206_a(this, (byte)6);
					}
				}

				return true;
			}
		} else {
			if(itemStack2 != null && Item.itemsList[itemStack2.itemID] instanceof ItemFood) {
				ItemFood itemFood3 = (ItemFood)Item.itemsList[itemStack2.itemID];
				if(itemFood3.func_25010_k() && this.dataWatcher.getWatchableObjectInteger(18) < 20) {
					--itemStack2.stackSize;
					if(itemStack2.stackSize <= 0) {
						entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
					}

					this.heal(((ItemFood)Item.porkRaw).getHealAmount());
					return true;
				}
			}

			if(entityPlayer1.username.equalsIgnoreCase(this.getOwner())) {
				if(!this.worldObj.singleplayerWorld) {
					this.setIsSitting(!this.getIsSitting());
					this.isJumping = false;
					this.setPathToEntity((PathEntity)null);
				}

				return true;
			}
		}

		return false;
	}

	void isNowTamed(boolean z1) {
		String string2 = "heart";
		if(!z1) {
			string2 = "smoke";
		}

		for(int i3 = 0; i3 < 7; ++i3) {
			double d4 = this.rand.nextGaussian() * 0.02D;
			double d6 = this.rand.nextGaussian() * 0.02D;
			double d8 = this.rand.nextGaussian() * 0.02D;
			this.worldObj.spawnParticle(string2, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d4, d6, d8);
		}

	}

	public int getMaxSpawnedInChunk() {
		return 8;
	}

	public String getOwner() {
		return this.dataWatcher.getWatchableObjectString(17);
	}

	public void setOwner(String string1) {
		this.dataWatcher.updateObject(17, string1);
	}

	public boolean getIsSitting() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
	}

	public void setIsSitting(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(16);
		if(z1) {
			this.dataWatcher.updateObject(16, (byte)(b2 | 1));
		} else {
			this.dataWatcher.updateObject(16, (byte)(b2 & -2));
		}

	}

	public boolean getIsAngry() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 2) != 0;
	}

	public void setIsAngry(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(16);
		if(z1) {
			this.dataWatcher.updateObject(16, (byte)(b2 | 2));
		} else {
			this.dataWatcher.updateObject(16, (byte)(b2 & -3));
		}

	}

	public boolean func_25030_y() {
		return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
	}

	public void setIsTamed(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(16);
		if(z1) {
			this.dataWatcher.updateObject(16, (byte)(b2 | 4));
		} else {
			this.dataWatcher.updateObject(16, (byte)(b2 & -5));
		}

	}
}
