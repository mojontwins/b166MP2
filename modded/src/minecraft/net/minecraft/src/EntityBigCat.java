package net.minecraft.src;

import java.util.List;

public class EntityBigCat extends EntityAnimal {
	public boolean lionboolean = false;
	protected int force;
	protected double attackRange;
	public int typeint;
	public boolean typechosen;
	public boolean adult;
	public float edad;
	public float heightF;
	public float widthF;
	public float lengthF;
	public boolean hungry;
	public boolean tamed;

	public EntityBigCat(World world1) {
		super(world1);
		this.texture = "/mob/lionf.png";
		this.edad = 0.35F;
		this.setSize(0.9F, 1.3F);
		this.health = 25;
		this.force = 1;
		this.attackRange = 1.0D;
		this.adult = true;
		this.hungry = true;
		this.tamed = false;
	}

	public void setType(int i1) {
		this.typeint = i1;
		this.typechosen = false;
		if(this.rand.nextInt(4) == 0) {
			this.adult = false;
		}

		this.chooseType();
	}

	public void chooseType() {
		if(this.typeint == 0) {
			if(this.rand.nextInt(4) == 0) {
				this.adult = false;
			}

			int i1 = this.rand.nextInt(100);
			if(i1 <= 5) {
				this.typeint = 1;
			} else if(i1 <= 25) {
				this.typeint = 2;
			} else if(i1 <= 50) {
				this.typeint = 3;
			} else if(i1 <= 70) {
				this.typeint = 4;
			} else if(i1 <= 75) {
				this.typeint = 7;
			} else {
				this.typeint = 5;
			}
		}

		if(!this.typechosen) {
			if(this.typeint == 1) {
				this.texture = "/mob/lionf.png";
				this.widthF = 1.0F;
				this.heightF = 1.0F;
				this.lengthF = 1.0F;
				this.moveSpeed = 1.4F;
				this.attackRange = 8.0D;
				this.force = 5;
				this.health = 25;
			} else if(this.typeint == 2) {
				this.texture = "/mob/lionf.png";
				this.widthF = 1.1F;
				this.heightF = 1.1F;
				this.lengthF = 1.0F;
				this.moveSpeed = 1.4F;
				this.attackRange = 4.0D;
				this.force = 5;
				this.health = 30;
			}

			if(this.typeint == 3) {
				this.texture = "/mob/panther.png";
				this.widthF = 0.9F;
				this.heightF = 0.9F;
				this.lengthF = 0.9F;
				this.moveSpeed = 1.6F;
				this.attackRange = 6.0D;
				this.force = 4;
				this.health = 20;
			} else if(this.typeint == 4) {
				this.texture = "/mob/cheetah.png";
				this.widthF = 0.7F;
				this.heightF = 0.9F;
				this.lengthF = 1.1F;
				this.moveSpeed = 1.9F;
				this.attackRange = 6.0D;
				this.force = 3;
				this.health = 20;
			} else if(this.typeint == 5) {
				this.texture = "/mob/tiger.png";
				this.widthF = 1.2F;
				this.heightF = 1.2F;
				this.lengthF = 1.2F;
				this.moveSpeed = 1.6F;
				this.attackRange = 8.0D;
				this.force = 6;
				this.health = 35;
			} else if(this.typeint == 6) {
				this.texture = "/mob/leopard.png";
				this.widthF = 0.8F;
				this.heightF = 0.8F;
				this.lengthF = 0.9F;
				this.moveSpeed = 1.7F;
				this.attackRange = 4.0D;
				this.force = 3;
				this.health = 25;
			} else if(this.typeint == 7) {
				this.texture = "/mob/tigerw.png";
				this.widthF = 1.2F;
				this.heightF = 1.2F;
				this.lengthF = 1.2F;
				this.moveSpeed = 1.7F;
				this.attackRange = 10.0D;
				this.force = 8;
				this.health = 40;
			}
		}

		this.typechosen = true;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		if(!this.adult && this.rand.nextInt(250) == 0) {
			this.edad += 0.01F;
			if(this.edad >= 1.0F) {
				this.adult = true;
			}
		}

		if(!this.hungry && this.rand.nextInt(100) == 0) {
			this.hungry = true;
		}

		if(this.deathTime == 0 && this.hungry) {
			EntityItem entityItem1 = this.getClosestItem(this, 12.0D, Item.porkRaw.shiftedIndex, Item.fishRaw.shiftedIndex);
			if(entityItem1 != null) {
				this.MoveToNextEntity(entityItem1);
				EntityItem entityItem2 = this.getClosestItem(this, 2.0D, Item.porkRaw.shiftedIndex, Item.fishRaw.shiftedIndex);
				if(this.rand.nextInt(80) == 0 && entityItem2 != null && this.deathTime == 0) {
					entityItem2.setEntityDead();
					this.health += 10;
					if(!this.adult && this.edad < 0.8F) {
						this.tamed = true;
					}

					this.worldObj.playSoundAtEntity(this, "eating", 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
					this.hungry = false;
				}
			}
		}

	}

	protected void updatePlayerActionState() {
		super.updatePlayerActionState();
		if(!this.hasAttacked && !this.hasPath() && this.tamed && this.ridingEntity == null) {
			EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, 18.0D);
			if(entityPlayer1 != null) {
				float f2 = entityPlayer1.getDistanceToEntity(this);
				if(f2 > 5.0F) {
					this.getPathOrWalkableBlock(entityPlayer1, f2);
				}
			}
		}

	}

	private void getPathOrWalkableBlock(Entity entity1, float f2) {
		PathEntity pathEntity3 = this.worldObj.getPathToEntity(this, entity1, 16.0F);
		if(pathEntity3 == null && f2 > 12.0F) {
			int i4 = MathHelper.floor_double(entity1.posX) - 2;
			int i5 = MathHelper.floor_double(entity1.posZ) - 2;
			int i6 = MathHelper.floor_double(entity1.boundingBox.minY);

			for(int i7 = 0; i7 <= 4; ++i7) {
				for(int i8 = 0; i8 <= 4; ++i8) {
					if((i7 < 1 || i8 < 1 || i7 > 3 || i8 > 3) && this.worldObj.func_28100_h(i4 + i7, i6 - 1, i5 + i8) && !this.worldObj.func_28100_h(i4 + i7, i6, i5 + i8) && !this.worldObj.func_28100_h(i4 + i7, i6 + 1, i5 + i8)) {
						this.setLocationAndAngles((double)((float)(i4 + i7) + 0.5F), (double)i6, (double)((float)(i5 + i8) + 0.5F), this.rotationYaw, this.rotationPitch);
						return;
					}
				}
			}
		} else {
			this.setPathToEntity(pathEntity3);
		}

	}

	protected boolean MoveToNextEntity(Entity entity1) {
		if(entity1 != null) {
			int i2 = MathHelper.floor_double(entity1.posX);
			int i3 = MathHelper.floor_double(entity1.posY);
			int i4 = MathHelper.floor_double(entity1.posZ);
			this.faceItem(i2, i3, i4, 30.0F);
			double d5;
			if(this.posX < (double)i2) {
				d5 = entity1.posX - this.posX;
				if(d5 > 0.5D) {
					this.motionX += 0.03D;
				}
			} else {
				d5 = this.posX - entity1.posX;
				if(d5 > 0.5D) {
					this.motionX -= 0.03D;
				}
			}

			if(this.posZ < (double)i4) {
				d5 = entity1.posZ - this.posZ;
				if(d5 > 0.5D) {
					this.motionZ += 0.03D;
				}
			} else {
				d5 = this.posZ - entity1.posZ;
				if(d5 > 0.5D) {
					this.motionZ -= 0.03D;
				}
			}

			return true;
		} else {
			return false;
		}
	}

	public void faceItem(int i1, int i2, int i3, float f4) {
		double d5 = (double)i1 - this.posX;
		double d7 = (double)i3 - this.posZ;
		double d9 = (double)i2 - this.posY;
		double d11 = (double)MathHelper.sqrt_double(d5 * d5 + d7 * d7);
		float f13 = (float)(Math.atan2(d7, d5) * 180.0D / 3.141592741012573D) - 90.0F;
		float f14 = (float)(Math.atan2(d9, d11) * 180.0D / 3.141592741012573D);
		this.rotationPitch = -this.b(this.rotationPitch, f14, f4);
		this.rotationYaw = this.b(this.rotationYaw, f13, f4);
	}

	public float b(float f1, float f2, float f3) {
		float f4;
		for(f4 = f2 - f1; f4 < -180.0F; f4 += 360.0F) {
		}

		while(f4 >= 180.0F) {
			f4 -= 360.0F;
		}

		if(f4 > f3) {
			f4 = f3;
		}

		if(f4 < -f3) {
			f4 = -f3;
		}

		return f1 + f4;
	}

	public EntityItem getClosestItem(Entity entity1, double d2, int i4, int i5) {
		double d6 = -1.0D;
		EntityItem entityItem8 = null;
		List list9 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d2, d2, d2));

		for(int i10 = 0; i10 < list9.size(); ++i10) {
			Entity entity11 = (Entity)list9.get(i10);
			if(entity11 instanceof EntityItem) {
				EntityItem entityItem12 = (EntityItem)entity11;
				if(entityItem12.item.itemID == i4 || i5 == 0 || entityItem12.item.itemID == i5) {
					double d13 = entityItem12.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
					if((d2 < 0.0D || d13 < d2 * d2) && (d6 == -1.0D || d13 < d6)) {
						d6 = d13;
						entityItem8 = entityItem12;
					}
				}
			}
		}

		return entityItem8;
	}

	protected Entity findPlayerToAttack() {
		if(this.worldObj.difficultySetting > 0 && this.hungry) {
			EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, this.attackRange);
			if(this.tamed && entityPlayer1 != null) {
				return this.getMastersEnemy(entityPlayer1, 12.0D);
			}

			if(!this.tamed && entityPlayer1 != null && this.adult) {
				if(this.typeint == 1 || this.typeint == 5 || this.typeint == 7) {
					this.hungry = false;
					return entityPlayer1;
				}

				if(this.rand.nextInt(30) == 0) {
					this.hungry = false;
					return entityPlayer1;
				}
			}

			if(this.rand.nextInt(80) == 0) {
				EntityLiving entityLiving2 = this.getClosestTarget(this, 10.0D);
				this.hungry = false;
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
			if(entity9 instanceof EntityLiving && entity9 != entity1 && entity9 != entity1.riddenByEntity && entity9 != entity1.ridingEntity && !(entity9 instanceof EntityPlayer) && (this.adult || (double)entity9.width <= 0.5D && (double)entity9.height <= 0.5D) && (!(entity9 instanceof EntityMob) || this.tamed && this.adult) && (!(entity9 instanceof EntityHorse) || ((Boolean)mod_mocreatures.attackhorses.get()).booleanValue()) && (!(entity9 instanceof EntityWolf) || ((Boolean)mod_mocreatures.attackwolves.get()).booleanValue())) {
				if(entity9 instanceof EntityBigCat) {
					if(!this.adult) {
						continue;
					}

					EntityBigCat entityBigCat10 = (EntityBigCat)entity9;
					if(entityBigCat10.typeint == 7 || this.typeint != 2 && this.typeint == entityBigCat10.typeint || this.typeint == 2 && entityBigCat10.typeint == 1 || this.health < entityBigCat10.health) {
						continue;
					}
				}

				double d12 = entity9.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
				if((d2 < 0.0D || d12 < d2 * d2) && (d4 == -1.0D || d12 < d4) && ((EntityLiving)entity9).canEntityBeSeen(entity1)) {
					d4 = d12;
					entityLiving6 = (EntityLiving)entity9;
				}
			}
		}

		return entityLiving6;
	}

	public EntityCreature getMastersEnemy(EntityPlayer entityPlayer1, double d2) {
		double d4 = -1.0D;
		Object object6 = null;
		List list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(entityPlayer1, this.boundingBox.expand(d2, d2, d2));

		for(int i8 = 0; i8 < list7.size(); ++i8) {
			Entity entity9 = (Entity)list7.get(i8);
			if(entity9 instanceof EntityCreature && entity9 != this) {
				EntityCreature entityCreature10 = (EntityCreature)entity9;
				if(entityCreature10 != null && entityCreature10.playerToAttack == entityPlayer1) {
					return entityCreature10;
				}
			}
		}

		return (EntityCreature)object6;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(super.attackEntityFrom(entity1, i2)) {
			if(this.riddenByEntity != entity1 && this.ridingEntity != entity1) {
				if(entity1 != this && this.worldObj.difficultySetting > 0 && !this.tamed) {
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
		if(f2 > 2.0F && f2 < 6.0F && this.rand.nextInt(50) == 0) {
			if(this.onGround) {
				double d3 = entity1.posX - this.posX;
				double d5 = entity1.posZ - this.posZ;
				float f7 = MathHelper.sqrt_double(d3 * d3 + d5 * d5);
				this.motionX = d3 / (double)f7 * 0.5D * 0.8D + this.motionX * 0.2D;
				this.motionZ = d5 / (double)f7 * 0.5D * 0.8D + this.motionZ * 0.2D;
				this.motionY = 0.4D;
			}
		} else if((double)f2 < 2.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity1.attackEntityFrom(this, this.force);
			if(!(entity1 instanceof EntityPlayer)) {
				this.destroyDrops(this, 3.0D);
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setBoolean("LionBoolean", this.lionboolean);
		nBTTagCompound1.setInteger("TypeInt", this.typeint);
		nBTTagCompound1.setBoolean("Adult", this.adult);
		nBTTagCompound1.setBoolean("Tamed", this.tamed);
		nBTTagCompound1.setFloat("Edad", this.edad);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.lionboolean = nBTTagCompound1.getBoolean("LionBoolean");
		this.adult = nBTTagCompound1.getBoolean("Adult");
		this.typeint = nBTTagCompound1.getInteger("TypeInt");
		this.edad = nBTTagCompound1.getFloat("Edad");
		this.tamed = nBTTagCompound1.getBoolean("Tamed");
	}

	protected String getLivingSound() {
		return "liongrunt";
	}

	protected String getHurtSound() {
		return "lionhurt";
	}

	protected String getDeathSound() {
		return "liondeath";
	}

	protected int getDropItemId() {
		return mod_mocreatures.bigcatclaw.shiftedIndex;
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

	public int getMaxSpawnedInChunk() {
		return 4;
	}

	protected boolean canDespawn() {
		return !this.tamed;
	}

	public void setEntityDead() {
		super.setEntityDead();
	}

	public int checkNearBigKitties(double d1) {
		boolean z3 = false;
		List list4 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d1, d1, d1));

		for(int i5 = 0; i5 < list4.size(); ++i5) {
			Entity entity6 = (Entity)list4.get(i5);
			if(entity6 != this && entity6 instanceof EntityBigCat) {
				EntityBigCat entityBigCat7 = (EntityBigCat)entity6;
				int i8 = entityBigCat7.typeint;
				if(i8 == 2) {
					i8 = 1;
				}

				return i8;
			}
		}

		return 0;
	}

	public boolean NearSnowWithDistance(Entity entity1, Double double2) {
		AxisAlignedBB axisAlignedBB3 = entity1.boundingBox.expand(double2.doubleValue(), double2.doubleValue(), double2.doubleValue());
		int i4 = MathHelper.floor_double(axisAlignedBB3.minX);
		int i5 = MathHelper.floor_double(axisAlignedBB3.maxX + 1.0D);
		int i6 = MathHelper.floor_double(axisAlignedBB3.minY);
		int i7 = MathHelper.floor_double(axisAlignedBB3.maxY + 1.0D);
		int i8 = MathHelper.floor_double(axisAlignedBB3.minZ);
		int i9 = MathHelper.floor_double(axisAlignedBB3.maxZ + 1.0D);

		for(int i10 = i4; i10 < i5; ++i10) {
			for(int i11 = i6; i11 < i7; ++i11) {
				for(int i12 = i8; i12 < i9; ++i12) {
					int i13 = this.worldObj.getBlockId(i10, i11, i12);
					if(i13 != 0 && Block.blocksList[i13].blockMaterial == Material.snow) {
						return true;
					}
				}
			}
		}

		return false;
	}

	public boolean getCanSpawnHere() {
		boolean z1 = false;
		int i2;
		if(this.NearSnowWithDistance(this, 1.0D)) {
			i2 = 6;
		} else {
			i2 = this.checkNearBigKitties(12.0D);
			if(i2 == 7) {
				i2 = 5;
			}
		}

		this.setType(i2);
		return ((Integer)mod_mocreatures.lionfreq.get()).intValue() > 0 && super.getCanSpawnHere();
	}
}
