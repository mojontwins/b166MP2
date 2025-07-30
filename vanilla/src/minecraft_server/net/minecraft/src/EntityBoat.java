package net.minecraft.src;

import java.util.List;

public class EntityBoat extends Entity {
	public int damageTaken;
	public int field_9177_b;
	public int forwardDirection;
	private int field_9176_d;
	private double field_9174_e;
	private double field_9172_f;
	private double field_9175_aj;
	private double field_9173_ak;
	private double field_9171_al;

	public EntityBoat(World world1) {
		super(world1);
		this.damageTaken = 0;
		this.field_9177_b = 0;
		this.forwardDirection = 1;
		this.preventEntitySpawning = true;
		this.setSize(1.5F, 0.6F);
		this.yOffset = this.height / 2.0F;
	}

	protected boolean func_25017_l() {
		return false;
	}

	protected void entityInit() {
	}

	public AxisAlignedBB func_89_d(Entity entity1) {
		return entity1.boundingBox;
	}

	public AxisAlignedBB getBoundingBox() {
		return this.boundingBox;
	}

	public boolean canBePushed() {
		return true;
	}

	public EntityBoat(World world1, double d2, double d4, double d6) {
		this(world1);
		this.setPosition(d2, d4 + (double)this.yOffset, d6);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = d2;
		this.prevPosY = d4;
		this.prevPosZ = d6;
	}

	public double getMountedYOffset() {
		return (double)this.height * 0.0D - (double)0.3F;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		if(!this.worldObj.singleplayerWorld && !this.isDead) {
			this.forwardDirection = -this.forwardDirection;
			this.field_9177_b = 10;
			this.damageTaken += i2 * 10;
			this.setBeenAttacked();
			if(this.damageTaken > 40) {
				if(this.riddenByEntity != null) {
					this.riddenByEntity.mountEntity(this);
				}

				int i3;
				for(i3 = 0; i3 < 3; ++i3) {
					this.dropItemWithOffset(Block.planks.blockID, 1, 0.0F);
				}

				for(i3 = 0; i3 < 2; ++i3) {
					this.dropItemWithOffset(Item.stick.shiftedIndex, 1, 0.0F);
				}

				this.setEntityDead();
			}

			return true;
		} else {
			return true;
		}
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		super.onUpdate();
		if(this.field_9177_b > 0) {
			--this.field_9177_b;
		}

		if(this.damageTaken > 0) {
			--this.damageTaken;
		}

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		byte b1 = 5;
		double d2 = 0.0D;

		for(int i4 = 0; i4 < b1; ++i4) {
			double d5 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i4 + 0) / (double)b1 - 0.125D;
			double d7 = this.boundingBox.minY + (this.boundingBox.maxY - this.boundingBox.minY) * (double)(i4 + 1) / (double)b1 - 0.125D;
			AxisAlignedBB axisAlignedBB9 = AxisAlignedBB.getBoundingBoxFromPool(this.boundingBox.minX, d5, this.boundingBox.minZ, this.boundingBox.maxX, d7, this.boundingBox.maxZ);
			if(this.worldObj.isAABBInMaterial(axisAlignedBB9, Material.water)) {
				d2 += 1.0D / (double)b1;
			}
		}

		double d6;
		double d8;
		double d10;
		double d21;
		if(this.worldObj.singleplayerWorld) {
			if(this.field_9176_d > 0) {
				d21 = this.posX + (this.field_9174_e - this.posX) / (double)this.field_9176_d;
				d6 = this.posY + (this.field_9172_f - this.posY) / (double)this.field_9176_d;
				d8 = this.posZ + (this.field_9175_aj - this.posZ) / (double)this.field_9176_d;

				for(d10 = this.field_9173_ak - (double)this.rotationYaw; d10 < -180.0D; d10 += 360.0D) {
				}

				while(d10 >= 180.0D) {
					d10 -= 360.0D;
				}

				this.rotationYaw = (float)((double)this.rotationYaw + d10 / (double)this.field_9176_d);
				this.rotationPitch = (float)((double)this.rotationPitch + (this.field_9171_al - (double)this.rotationPitch) / (double)this.field_9176_d);
				--this.field_9176_d;
				this.setPosition(d21, d6, d8);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				d21 = this.posX + this.motionX;
				d6 = this.posY + this.motionY;
				d8 = this.posZ + this.motionZ;
				this.setPosition(d21, d6, d8);
				if(this.onGround) {
					this.motionX *= 0.5D;
					this.motionY *= 0.5D;
					this.motionZ *= 0.5D;
				}

				this.motionX *= (double)0.99F;
				this.motionY *= (double)0.95F;
				this.motionZ *= (double)0.99F;
			}

		} else {
			if(d2 < 1.0D) {
				d21 = d2 * 2.0D - 1.0D;
				this.motionY += (double)0.04F * d21;
			} else {
				if(this.motionY < 0.0D) {
					this.motionY /= 2.0D;
				}

				this.motionY += 0.007000000216066837D;
			}

			if(this.riddenByEntity != null) {
				this.motionX += this.riddenByEntity.motionX * 0.2D;
				this.motionZ += this.riddenByEntity.motionZ * 0.2D;
			}

			d21 = 0.4D;
			if(this.motionX < -d21) {
				this.motionX = -d21;
			}

			if(this.motionX > d21) {
				this.motionX = d21;
			}

			if(this.motionZ < -d21) {
				this.motionZ = -d21;
			}

			if(this.motionZ > d21) {
				this.motionZ = d21;
			}

			if(this.onGround) {
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			d6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
			if(d6 > 0.15D) {
				d8 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
				d10 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D);

				for(int i12 = 0; (double)i12 < 1.0D + d6 * 60.0D; ++i12) {
					double d13 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
					double d15 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;
					double d17;
					double d19;
					if(this.rand.nextBoolean()) {
						d17 = this.posX - d8 * d13 * 0.8D + d10 * d15;
						d19 = this.posZ - d10 * d13 * 0.8D - d8 * d15;
						this.worldObj.spawnParticle("splash", d17, this.posY - 0.125D, d19, this.motionX, this.motionY, this.motionZ);
					} else {
						d17 = this.posX + d8 + d10 * d13 * 0.7D;
						d19 = this.posZ + d10 - d8 * d13 * 0.7D;
						this.worldObj.spawnParticle("splash", d17, this.posY - 0.125D, d19, this.motionX, this.motionY, this.motionZ);
					}
				}
			}

			if(this.isCollidedHorizontally && d6 > 0.15D) {
				if(!this.worldObj.singleplayerWorld) {
					this.setEntityDead();

					int i22;
					for(i22 = 0; i22 < 3; ++i22) {
						this.dropItemWithOffset(Block.planks.blockID, 1, 0.0F);
					}

					for(i22 = 0; i22 < 2; ++i22) {
						this.dropItemWithOffset(Item.stick.shiftedIndex, 1, 0.0F);
					}
				}
			} else {
				this.motionX *= (double)0.99F;
				this.motionY *= (double)0.95F;
				this.motionZ *= (double)0.99F;
			}

			this.rotationPitch = 0.0F;
			d8 = (double)this.rotationYaw;
			d10 = this.prevPosX - this.posX;
			double d23 = this.prevPosZ - this.posZ;
			if(d10 * d10 + d23 * d23 > 0.001D) {
				d8 = (double)((float)(Math.atan2(d23, d10) * 180.0D / Math.PI));
			}

			double d14;
			for(d14 = d8 - (double)this.rotationYaw; d14 >= 180.0D; d14 -= 360.0D) {
			}

			while(d14 < -180.0D) {
				d14 += 360.0D;
			}

			if(d14 > 20.0D) {
				d14 = 20.0D;
			}

			if(d14 < -20.0D) {
				d14 = -20.0D;
			}

			this.rotationYaw = (float)((double)this.rotationYaw + d14);
			this.setRotation(this.rotationYaw, this.rotationPitch);
			List list16 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand((double)0.2F, 0.0D, (double)0.2F));
			int i24;
			if(list16 != null && list16.size() > 0) {
				for(i24 = 0; i24 < list16.size(); ++i24) {
					Entity entity18 = (Entity)list16.get(i24);
					if(entity18 != this.riddenByEntity && entity18.canBePushed() && entity18 instanceof EntityBoat) {
						entity18.applyEntityCollision(this);
					}
				}
			}

			for(i24 = 0; i24 < 4; ++i24) {
				int i25 = MathHelper.floor_double(this.posX + ((double)(i24 % 2) - 0.5D) * 0.8D);
				int i26 = MathHelper.floor_double(this.posY);
				int i20 = MathHelper.floor_double(this.posZ + ((double)(i24 / 2) - 0.5D) * 0.8D);
				if(this.worldObj.getBlockId(i25, i26, i20) == Block.snow.blockID) {
					this.worldObj.setBlockWithNotify(i25, i26, i20, 0);
				}
			}

			if(this.riddenByEntity != null && this.riddenByEntity.isDead) {
				this.riddenByEntity = null;
			}

		}
	}

	public void updateRiderPosition() {
		if(this.riddenByEntity != null) {
			double d1 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			double d3 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D) * 0.4D;
			this.riddenByEntity.setPosition(this.posX + d1, this.posY + this.getMountedYOffset() + this.riddenByEntity.getYOffset(), this.posZ + d3);
		}
	}

	protected void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
	}

	protected void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		if(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != entityPlayer1) {
			return true;
		} else {
			if(!this.worldObj.singleplayerWorld) {
				entityPlayer1.mountEntity(this);
			}

			return true;
		}
	}
}
