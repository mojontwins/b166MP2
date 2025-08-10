package net.minecraft.src;

import java.util.List;

public class EntityHorse extends EntityAnimal {
	private int nightmareInt;
	private boolean bred;
	public boolean eatenpumpkin;
	private int gestationtime;
	public boolean hasreproduced;
	private int maxhealth;
	private int temper;
	private double HorseSpeed;
	private double HorseJump;
	public int typeint;
	public boolean typechosen;
	public boolean tamed;
	public boolean horseboolean = false;
	public boolean rideable;
	public boolean isjumping;
	public float fwingb;
	public float fwingc;
	public float fwingd;
	public float fwinge;
	public float fwingh;
	public boolean chestedhorse;
	private IInventory localhorsechest;
	public ItemStack[] localstack;
	private boolean eatinghaystack;
	public float b;
	public boolean adult;

	public EntityHorse(World world1) {
		super(world1);
		this.setSize(1.4F, 1.6F);
		this.health = 20;
		this.rideable = false;
		this.isjumping = false;
		this.tamed = false;
		this.texture = "/mob/horseb.png";
		this.typeint = 0;
		this.HorseSpeed = 0.8D;
		this.HorseJump = 0.4D;
		this.temper = 100;
		this.typechosen = false;
		this.fwingb = 0.0F;
		this.fwingc = 0.0F;
		this.fwingh = 1.0F;
		this.localstack = new ItemStack[27];
		this.maxhealth = 20;
		this.hasreproduced = false;
		this.gestationtime = 0;
		this.eatenpumpkin = false;
		this.bred = false;
		this.nightmareInt = 0;
		this.isImmuneToFire = false;
		this.adult = true;
		this.b = 0.35F;
	}

	public void setEntityDead() {
		if(!this.tamed && !this.bred || this.health <= 0) {
			super.setEntityDead();
		}
	}

	public void setType(int i1) {
		this.typeint = i1;
		this.typechosen = false;
		this.chooseType();
	}

	public void chooseType() {
		int i1 = ((Integer)mod_mocreatures.pegasusChanceS.get()).intValue();
		if(this.typeint == 0) {
			if(this.rand.nextInt(5) == 0) {
				this.adult = false;
			}

			int i2 = this.rand.nextInt(100);
			if(i2 <= 51 - i1) {
				this.typeint = 1;
			} else if(i2 <= 86 - i1) {
				this.typeint = 2;
			} else if(i2 <= 95 - i1) {
				this.typeint = 3;
			} else if(i2 <= 99 - i1) {
				this.typeint = 4;
			} else {
				this.typeint = 5;
			}
		}

		if(!this.typechosen) {
			if(this.typeint == 1) {
				this.HorseSpeed = 0.9D;
				this.texture = "/mob/horseb.png";
				this.maxhealth = 25;
			} else if(this.typeint == 2) {
				this.HorseSpeed = 1.0D;
				this.temper = 200;
				this.HorseJump = 0.5D;
				this.texture = "/mob/horsebrownb.png";
				this.maxhealth = 30;
			} else if(this.typeint == 3) {
				this.HorseSpeed = 1.1D;
				this.temper = 300;
				this.HorseJump = 0.6D;
				this.texture = "/mob/horseblackb.png";
				this.maxhealth = 35;
			} else if(this.typeint == 4) {
				this.HorseSpeed = 1.3D;
				this.HorseJump = 0.6D;
				this.temper = 400;
				this.texture = "/mob/horsegoldb.png";
				this.maxhealth = 40;
			} else if(this.typeint == 5) {
				this.HorseSpeed = 1.2D;
				this.temper = 500;
				this.texture = "/mob/horsewhiteb.png";
				this.maxhealth = 40;
			} else if(this.typeint == 6) {
				this.HorseSpeed = 0.9D;
				this.temper = 600;
				this.texture = "/mob/horsepackb.png";
				this.maxhealth = 40;
			} else if(this.typeint == 7) {
				this.HorseSpeed = 1.3D;
				this.temper = 700;
				this.HorseJump = 0.6D;
				this.texture = "/mob/horsenightb.png";
				this.maxhealth = 50;
				this.isImmuneToFire = true;
			} else if(this.typeint == 8) {
				this.HorseSpeed = 1.3D;
				this.temper = 800;
				this.texture = "/mob/horsebpb.png";
				this.maxhealth = 50;
				this.isImmuneToFire = true;
			}
		}

		this.typechosen = true;
	}

	public void onLivingUpdate() {
		if(this.rand.nextInt(300) == 0 && this.health <= this.maxhealth && this.deathTime < 0) {
			this.health += 5;
		}

		this.fwinge = this.fwingb;
		this.fwingd = this.fwingc;
		this.fwingc = (float)((double)this.fwingc + (double)(this.onGround ? -1 : 4) * 0.3D);
		if(this.fwingc < 0.0F) {
			this.fwingc = 0.0F;
		}

		if(this.fwingc > 1.0F) {
			this.fwingc = 1.0F;
		}

		if(!this.onGround && this.fwingh < 1.0F) {
			this.fwingh = 0.3F;
		}

		this.fwingh = (float)((double)this.fwingh * 0.9D);
		if(!this.onGround && this.motionY < 0.0D && (this.typeint == 5 || this.typeint == 8)) {
			this.motionY *= 0.6D;
		}

		this.fwingb += this.fwingh * 2.0F;
		super.onLivingUpdate();
		if(this.typeint == 7 && this.riddenByEntity != null && this.nightmareInt > 0 && this.rand.nextInt(2) == 0) {
			this.NightmareEffect();
		}

		if(!this.adult && this.rand.nextInt(200) == 0) {
			this.b += 0.01F;
			if(this.b >= 1.0F) {
				this.adult = true;
			}
		}

		if(this.ReadyforParenting(this)) {
			int i1 = 0;
			List list2 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(8.0D, 3.0D, 8.0D));

			for(int i3 = 0; i3 < list2.size(); ++i3) {
				Entity entity4 = (Entity)list2.get(i3);
				if(entity4 instanceof EntityHorse) {
					++i1;
				}
			}

			if(i1 <= 1) {
				List list9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(4.0D, 2.0D, 4.0D));

				for(int i10 = 0; i10 < list2.size(); ++i10) {
					Entity entity5 = (Entity)list9.get(i10);
					if(entity5 instanceof EntityHorse && entity5 != this) {
						EntityHorse entityHorse6 = (EntityHorse)entity5;
						if(this.ReadyforParenting(this) && this.ReadyforParenting(entityHorse6)) {
							if(this.rand.nextInt(100) == 0) {
								++this.gestationtime;
							}

							if(this.gestationtime > 50) {
								EntityHorse entityHorse7 = new EntityHorse(this.worldObj);
								entityHorse7.setPosition(this.posX, this.posY, this.posZ);
								this.worldObj.entityJoinedWorld(entityHorse7);
								this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
								this.eatenpumpkin = false;
								if(!((Boolean)mod_mocreatures.easybreeding.get()).booleanValue()) {
									this.hasreproduced = true;
								}

								entityHorse6.eatenpumpkin = false;
								this.gestationtime = 0;
								entityHorse6.gestationtime = 0;
								int i8 = this.HorseGenetics(this, entityHorse6);
								entityHorse7.bred = true;
								entityHorse7.adult = false;
								entityHorse7.setType(i8);
								break;
							}
						}
					}
				}

			}
		}
	}

	protected void updatePlayerActionState() {
		if(!this.isMultiplayerEntity && this.riddenByEntity == null && !this.eatinghaystack) {
			super.updatePlayerActionState();
		}

	}

	private void NightmareEffect() {
		int i1 = MathHelper.floor_double(this.posX);
		int i2 = MathHelper.floor_double(this.boundingBox.minY);
		int i3 = MathHelper.floor_double(this.posZ);
		this.worldObj.setBlockWithNotify(i1 - 1, i2, i3 - 1, Block.fire.blockID);
		--this.nightmareInt;
	}

	public boolean ReadyforParenting(EntityHorse entityHorse1) {
		return entityHorse1.riddenByEntity == null && entityHorse1.ridingEntity == null && entityHorse1.tamed && entityHorse1.eatenpumpkin && !entityHorse1.hasreproduced && entityHorse1.adult;
	}

	private int HorseGenetics(EntityHorse entityHorse1, EntityHorse entityHorse2) {
		if(entityHorse1.typeint == entityHorse2.typeint) {
			return entityHorse1.typeint;
		} else {
			int i3 = entityHorse1.typeint + entityHorse2.typeint;
			boolean z4 = ((Boolean)mod_mocreatures.easybreeding.get()).booleanValue();
			boolean z5 = this.rand.nextInt(3) == 0;
			return i3 == 7 && (z4 || z5) ? 6 : (i3 == 9 && (z4 || z5) ? 7 : (i3 != 10 || !z4 && !z5 ? (i3 == 12 && (z4 || z5) ? 8 : 0) : 5));
		}
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		return this.riddenByEntity != null && entity1 == this.riddenByEntity ? false : super.attackEntityFrom(entity1, i2);
	}

	public void moveEntityWithHeading(float f1, float f2) {
		EntityPlayer entityPlayer3;
		double d8;
		if(this.handleWaterMovement()) {
			if(this.riddenByEntity != null) {
				this.motionX += this.riddenByEntity.motionX * (this.HorseSpeed / 2.0D);
				this.motionZ += this.riddenByEntity.motionZ * (this.HorseSpeed / 2.0D);
				entityPlayer3 = (EntityPlayer)this.riddenByEntity;
				if(entityPlayer3.isJumping && !this.isjumping) {
					this.motionY += 0.5D;
					this.isjumping = true;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if(this.onGround) {
					this.isjumping = false;
				}

				this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
				if(this.rand.nextInt(20) == 0) {
					this.rotationYaw = this.riddenByEntity.rotationYaw;
				}

				this.setRotation(this.rotationYaw, this.rotationPitch);
				if(!this.tamed) {
					this.riddenByEntity = null;
				}
			}

			d8 = this.posY;
			this.moveFlying(f1, f2, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)0.8F;
			this.motionY *= (double)0.8F;
			this.motionZ *= (double)0.8F;
			this.motionY -= 0.02D;
			if(this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6F - this.posY + d8, this.motionZ)) {
				this.motionY = 0.300000011920929D;
			}
		} else if(this.handleLavaMovement()) {
			if(this.riddenByEntity != null) {
				this.motionX += this.riddenByEntity.motionX * (this.HorseSpeed / 2.0D);
				this.motionZ += this.riddenByEntity.motionZ * (this.HorseSpeed / 2.0D);
				entityPlayer3 = (EntityPlayer)this.riddenByEntity;
				if(entityPlayer3.isJumping && !this.isjumping) {
					this.motionY += 0.5D;
					this.isjumping = true;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if(this.onGround) {
					this.isjumping = false;
				}

				this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
				if(this.rand.nextInt(20) == 0) {
					this.rotationYaw = this.riddenByEntity.rotationYaw;
				}

				this.setRotation(this.rotationYaw, this.rotationPitch);
				if(!this.tamed) {
					this.riddenByEntity = null;
				}
			}

			d8 = this.posY;
			this.moveFlying(f1, f2, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
			this.motionY -= 0.02D;
			if(this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6F - this.posY + d8, this.motionZ)) {
				this.motionY = 0.300000011920929D;
			}
		} else {
			float f9 = 0.91F;
			if(this.onGround) {
				f9 = 0.5460001F;
				int i4 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if(i4 > 0) {
					f9 = Block.blocksList[i4].slipperiness * 0.91F;
				}
			}

			float f10 = 0.162771F / (f9 * f9 * f9);
			this.moveFlying(f1, f2, this.onGround ? 0.1F * f10 : 0.02F);
			f9 = 0.91F;
			if(this.onGround) {
				f9 = 0.5460001F;
				int i5 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if(i5 > 0) {
					f9 = Block.blocksList[i5].slipperiness * 0.91F;
				}
			}

			if(this.isOnLadder()) {
				this.fallDistance = 0.0F;
				if(this.motionY < -0.15D) {
					this.motionY = -0.15D;
				}
			}

			if(this.riddenByEntity != null && !this.tamed) {
				if(this.rand.nextInt(5) == 0 && !this.isjumping) {
					this.motionY += 0.4D;
					this.isjumping = true;
				}

				if(this.rand.nextInt(10) == 0) {
					this.motionX += this.rand.nextDouble() / 30.0D;
					this.motionZ += this.rand.nextDouble() / 10.0D;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if(this.rand.nextInt(50) == 0) {
					this.worldObj.playSoundAtEntity(this, "horsemad", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
					this.riddenByEntity.motionY += 0.9D;
					this.riddenByEntity.motionZ -= 0.3D;
					this.riddenByEntity = null;
				}

				if(this.onGround) {
					this.isjumping = false;
				}

				if(this.rand.nextInt(this.temper * 8) == 0) {
					this.tamed = true;
				}
			}

			if(this.riddenByEntity != null && this.tamed) {
				this.boundingBox.maxY = this.riddenByEntity.boundingBox.maxY;
				this.motionX += this.riddenByEntity.motionX * this.HorseSpeed;
				this.motionZ += this.riddenByEntity.motionZ * this.HorseSpeed;
				EntityPlayer entityPlayer11 = (EntityPlayer)this.riddenByEntity;
				if(entityPlayer11.isJumping && !this.isjumping) {
					this.motionY += this.HorseJump;
					this.isjumping = true;
				}

				if(entityPlayer11.isJumping && (this.typeint == 5 || this.typeint == 8)) {
					this.motionY += 0.1D;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if(this.onGround) {
					this.isjumping = false;
				}

				this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
				this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			if(this.isCollidedHorizontally && this.isOnLadder()) {
				this.motionY = 0.2D;
			}

			if((this.typeint == 5 || this.typeint == 8) && this.riddenByEntity != null && this.tamed) {
				this.motionY -= 0.08D;
				this.motionY *= 0.6000000000000001D;
			} else {
				this.motionY -= 0.08D;
				this.motionY *= (double)0.98F;
			}

			this.motionX *= (double)f9;
			this.motionZ *= (double)f9;
		}

		this.field_705_Q = this.field_704_R;
		d8 = this.posX - this.prevPosX;
		double d12 = this.posZ - this.prevPosZ;
		float f7 = MathHelper.sqrt_double(d8 * d8 + d12 * d12) * 4.0F;
		if(f7 > 1.0F) {
			f7 = 1.0F;
		}

		this.field_704_R += (f7 - this.field_704_R) * 0.4F;
		this.field_703_S += this.field_704_R;
	}

	protected void fall(float f1) {
		int i2 = (int)Math.ceil((double)(f1 - 3.0F));
		if(i2 > 0 && this.typeint != 5 && this.typeint != 8) {
			if(this.typeint >= 3) {
				i2 /= 3;
			}

			if(i2 > 0) {
				this.attackEntityFrom(this, i2);
			}

			if(this.riddenByEntity != null && i2 > 0) {
				this.riddenByEntity.attackEntityFrom(this, i2);
			}

			if(this.typeint == 5 || this.typeint == 8) {
				return;
			}

			int i3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY - 0.2000000029802322D - (double)this.prevRotationPitch), MathHelper.floor_double(this.posZ));
			if(i3 > 0) {
				StepSound stepSound4 = Block.blocksList[i3].stepSound;
				this.worldObj.playSoundAtEntity(this, stepSound4.func_1145_d(), stepSound4.getVolume() * 0.5F, stepSound4.getPitch() * 0.75F);
			}
		}

	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		ItemStack itemStack2 = entityPlayer1.inventory.getCurrentItem();
		if(itemStack2 != null && itemStack2.itemID == Item.wheat.shiftedIndex) {
			if(--itemStack2.stackSize == 0) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
			}

			if((this.temper -= 25) < 25) {
				this.temper = 25;
			}

			if((this.health += 5) > this.maxhealth) {
				this.health = this.maxhealth;
			}

			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			if(!this.adult) {
				this.b += 0.01F;
			}

			return true;
		} else if(itemStack2 != null && itemStack2.itemID == mod_mocreatures.sugarlump.shiftedIndex) {
			if(--itemStack2.stackSize == 0) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
			}

			if((this.temper -= 50) < 25) {
				this.temper = 25;
			}

			if((this.health += 10) > this.maxhealth) {
				this.health = this.maxhealth;
			}

			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			if(!this.adult) {
				this.b += 0.02F;
			}

			return true;
		} else if(itemStack2 != null && itemStack2.itemID == Item.bread.shiftedIndex) {
			if(--itemStack2.stackSize == 0) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
			}

			if((this.temper -= 100) < 25) {
				this.temper = 25;
			}

			if((this.health += 20) > this.maxhealth) {
				this.health = this.maxhealth;
			}

			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			if(!this.adult) {
				this.b += 0.03F;
			}

			return true;
		} else if(itemStack2 != null && itemStack2.itemID == Item.appleRed.shiftedIndex) {
			if(--itemStack2.stackSize == 0) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
			}

			this.tamed = true;
			this.health = this.maxhealth;
			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			if(!this.adult) {
				this.b += 0.05F;
			}

			return true;
		} else if(itemStack2 != null && this.tamed && itemStack2.itemID == Block.chest.blockID && (this.typeint == 6 || this.typeint == 8)) {
			if(this.chestedhorse) {
				return false;
			} else {
				if(--itemStack2.stackSize == 0) {
					entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
				}

				this.chestedhorse = true;
				this.worldObj.playSoundAtEntity(this, "mob.chickenplop", 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
				return true;
			}
		} else if(itemStack2 != null && this.tamed && itemStack2.itemID == mod_mocreatures.haystack.shiftedIndex) {
			if(--itemStack2.stackSize == 0) {
				entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
			}

			this.eatinghaystack = true;
			this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
			this.health = this.maxhealth;
			return true;
		} else if(itemStack2 != null && (itemStack2.itemID == Item.shovelStone.shiftedIndex || itemStack2.itemID == Block.torchWood.blockID) && this.chestedhorse) {
			this.localhorsechest = new AnimalChest(this.localstack, "HorseChest");
			entityPlayer1.displayGUIChest(this.localhorsechest);
			return true;
		} else if(itemStack2 != null && (itemStack2.itemID == Block.pumpkin.blockID || itemStack2.itemID == Item.bowlSoup.shiftedIndex)) {
			if(!this.hasreproduced && this.adult) {
				if(--itemStack2.stackSize == 0) {
					entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
				}

				this.eatenpumpkin = true;
				this.health = this.maxhealth;
				this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
				return true;
			} else {
				return false;
			}
		} else if(itemStack2 != null && this.tamed && itemStack2.itemID == Item.redstone.shiftedIndex && this.typeint == 7) {
			if(this.nightmareInt > 500) {
				return false;
			} else {
				if(--itemStack2.stackSize == 0) {
					entityPlayer1.inventory.setInventorySlotContents(entityPlayer1.inventory.currentItem, (ItemStack)null);
				}

				this.nightmareInt = 500;
				this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
				return true;
			}
		} else if(this.rideable && this.adult) {
			entityPlayer1.rotationYaw = this.rotationYaw;
			entityPlayer1.rotationPitch = this.rotationPitch;
			this.eatinghaystack = false;
			entityPlayer1.mountEntity(this);
			this.gestationtime = 0;
			return true;
		} else {
			return false;
		}
	}

	public void onDeath(Entity entity1) {
		if(this.scoreValue >= 0 && entity1 != null) {
			entity1.addToPlayerScore(this, this.scoreValue);
		}

		if(entity1 != null) {
			entity1.onKillEntity(this);
		}

		this.unused_flag = true;
		if(!this.worldObj.multiplayerWorld) {
			this.dropFewItems();
		}

		this.worldObj.func_4079_a(this, 3.0F);
		if(this.chestedhorse && (this.typeint == 6 || this.typeint == 8)) {
			int i2 = MathHelper.floor_double(this.posX);
			int i3 = MathHelper.floor_double(this.boundingBox.minY);
			int i4 = MathHelper.floor_double(this.posZ);
			this.HorseRemoval(this.worldObj, i2, i3, i4);
		}

	}

	public void HorseRemoval(World world1, int i2, int i3, int i4) {
		if(this.localstack != null) {
			this.localhorsechest = new AnimalChest(this.localstack, "HorseChest");

			for(int i5 = 0; i5 < this.localhorsechest.getSizeInventory(); ++i5) {
				ItemStack itemStack6 = this.localhorsechest.getStackInSlot(i5);
				if(itemStack6 != null) {
					float f7 = this.rand.nextFloat() * 0.8F + 0.1F;
					float f8 = this.rand.nextFloat() * 0.8F + 0.1F;
					float f9 = this.rand.nextFloat() * 0.8F + 0.1F;

					while(itemStack6.stackSize > 0) {
						int i10 = this.rand.nextInt(21) + 10;
						if(i10 > itemStack6.stackSize) {
							i10 = itemStack6.stackSize;
						}

						itemStack6.stackSize -= i10;
						EntityItem entityItem11 = new EntityItem(this.worldObj, (double)((float)i2 + f7), (double)((float)i3 + f8), (double)((float)i4 + f9), new ItemStack(itemStack6.itemID, i10, itemStack6.getItemDamage()));
						float f12 = 0.05F;
						entityItem11.motionX = (double)((float)this.rand.nextGaussian() * f12);
						entityItem11.motionY = (double)((float)this.rand.nextGaussian() * f12 + 0.2F);
						entityItem11.motionZ = (double)((float)this.rand.nextGaussian() * f12);
						this.worldObj.entityJoinedWorld(entityItem11);
					}
				}
			}

		}
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("Saddle", this.rideable);
		nBTTagCompound1.setBoolean("Tamed", this.tamed);
		nBTTagCompound1.setBoolean("HorseBoolean", this.horseboolean);
		nBTTagCompound1.setInteger("TypeInt", this.typeint);
		nBTTagCompound1.setBoolean("ChestedHorse", this.chestedhorse);
		nBTTagCompound1.setBoolean("HasReproduced", this.hasreproduced);
		nBTTagCompound1.setBoolean("Bred", this.bred);
		nBTTagCompound1.setBoolean("Adult", this.adult);
		nBTTagCompound1.setFloat("Age", this.b);
		if(this.typeint == 6 || this.typeint == 8) {
			NBTTagList nBTTagList2 = new NBTTagList();

			for(int i3 = 0; i3 < this.localstack.length; ++i3) {
				if(this.localstack[i3] != null) {
					NBTTagCompound nBTTagCompound4 = new NBTTagCompound();
					nBTTagCompound4.setByte("Slot", (byte)i3);
					this.localstack[i3].writeToNBT(nBTTagCompound4);
					nBTTagList2.setTag(nBTTagCompound4);
				}
			}

			nBTTagCompound1.setTag("Items", nBTTagList2);
		}

	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.rideable = nBTTagCompound1.getBoolean("Saddle");
		this.tamed = nBTTagCompound1.getBoolean("Tamed");
		this.bred = nBTTagCompound1.getBoolean("Bred");
		this.adult = nBTTagCompound1.getBoolean("Adult");
		this.horseboolean = nBTTagCompound1.getBoolean("HorseBoolean");
		this.chestedhorse = nBTTagCompound1.getBoolean("ChestedHorse");
		this.hasreproduced = nBTTagCompound1.getBoolean("HasReproduced");
		this.typeint = nBTTagCompound1.getInteger("TypeInt");
		this.b = nBTTagCompound1.getFloat("Age");
		if(this.typeint == 6 || this.typeint == 8) {
			NBTTagList nBTTagList2 = nBTTagCompound1.getTagList("Items");
			this.localstack = new ItemStack[27];

			for(int i3 = 0; i3 < nBTTagList2.tagCount(); ++i3) {
				NBTTagCompound nBTTagCompound4 = (NBTTagCompound)nBTTagList2.tagAt(i3);
				int i5 = nBTTagCompound4.getByte("Slot") & 255;
				if(i5 >= 0 && i5 < this.localstack.length) {
					this.localstack[i5] = new ItemStack(nBTTagCompound4);
				}
			}
		}

	}

	protected String getLivingSound() {
		return "horsegrunt";
	}

	protected String getHurtSound() {
		return "horsehurt";
	}

	protected String getDeathSound() {
		return "horsedying";
	}

	protected int getDropItemId() {
		return Item.leather.shiftedIndex;
	}

	public int getMaxSpawnedInChunk() {
		return 6;
	}

	public boolean getCanSpawnHere() {
		return ((Integer)mod_mocreatures.horsefreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}
}
