package net.minecraft.src;

import java.util.List;

public class EntityCustomWM extends EntityWaterMob {
	private PathEntity a;
	private int outOfWater = 0;
	private boolean tamed = true;
	private int temper = 50;

	public EntityCustomWM(World world1) {
		super(world1);
	}

	public boolean handleWaterMovement() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
	}

	public boolean gettingOutOfWater() {
		int i1 = (int)this.posX;
		int i2 = (int)this.posY;
		int i3 = (int)this.posZ;
		boolean z4 = true;
		int i5 = this.worldObj.getBlockId(i1, i2 + 1, i3);
		return i5 == 0;
	}

	public double speed() {
		return 1.5D;
	}

	public int tametemper() {
		return this.temper;
	}

	public boolean istamed() {
		return this.tamed;
	}

	public void setTame() {
		this.tamed = true;
	}

	public void moveEntityWithHeading(float f1, float f2) {
		if(this.handleWaterMovement()) {
			if(this.riddenByEntity != null && !this.istamed()) {
				if(this.rand.nextInt(5) == 0 && !this.isJumping) {
					this.motionY += 0.4D;
					this.isJumping = true;
				}

				if(this.rand.nextInt(10) == 0) {
					this.motionX += this.rand.nextDouble() / 30.0D;
					this.motionZ += this.rand.nextDouble() / 10.0D;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				if(this.rand.nextInt(50) == 0) {
					this.worldObj.playSoundAtEntity(this, this.getUpsetSound(), 1.0F, 1.0F + (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
					this.riddenByEntity.motionY += 0.9D;
					this.riddenByEntity.motionZ -= 0.3D;
					this.riddenByEntity = null;
				}

				if(this.onGround) {
					this.isJumping = false;
				}

				if(this.rand.nextInt(this.tametemper() * 8) == 0) {
					this.setTame();
				}
			}

			if(this.riddenByEntity != null && this.istamed()) {
				this.boundingBox.maxY = this.riddenByEntity.boundingBox.maxY;
				this.motionX += this.riddenByEntity.motionX * this.speed();
				this.motionZ += this.riddenByEntity.motionZ * this.speed();
				if(this.motionY != 0.0D) {
					this.motionY = 0.0D;
				}

				this.moveEntity(this.motionX, this.motionY, this.motionZ);
				this.rotationPitch = this.riddenByEntity.rotationPitch * 0.5F;
				this.prevRotationYaw = this.rotationYaw = this.riddenByEntity.rotationYaw;
				this.setRotation(this.rotationYaw, this.rotationPitch);
			}

			this.moveFlying(f1, f2, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			if(this.riddenByEntity == null) {
				this.motionX *= (double)0.8F;
				this.motionZ *= (double)0.8F;
			}
		} else if(this.handleLavaMovement()) {
			double d3 = this.posY;
			this.moveFlying(f1, f2, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
			this.motionY -= 0.02D;
			if(this.isCollidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6F - this.posY + d3, this.motionZ)) {
				this.motionY = 0.300000011920929D;
			}
		}

		float f12 = 0.91F;
		f12 = 0.5460001F;
		int i4 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
		if(i4 > 0) {
			f12 = Block.blocksList[i4].slipperiness * 0.91F;
		}

		float f5 = 0.162771F / (f12 * f12 * f12);
		this.moveFlying(f1, f2, 0.1F * f5);
		f12 = 0.91F;
		f12 = 0.5460001F;
		int i6 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
		if(i6 > 0) {
			f12 = Block.blocksList[i6].slipperiness * 0.91F;
		}

		if(this.isOnLadder()) {
			this.fallDistance = 0.0F;
			if(this.motionY < -0.15D) {
				this.motionY = -0.15D;
			}
		}

		this.moveEntity(this.motionX, this.motionY, this.motionZ);
		if(this.isCollidedHorizontally && this.isOnLadder()) {
			this.motionY = 0.2D;
		}

		this.motionX *= (double)f12;
		this.motionZ *= (double)f12;
		if(!this.handleWaterMovement()) {
			this.motionY -= 0.08D;
			this.motionY *= (double)0.98F;
		} else if(this.riddenByEntity == null) {
			this.motionY -= 0.02D;
			this.motionY *= 0.5D;
		}

		this.field_705_Q = this.field_704_R;
		double d7 = this.posX - this.prevPosX;
		double d9 = this.posZ - this.prevPosZ;
		float f11 = MathHelper.sqrt_double(d7 * d7 + d9 * d9) * 4.0F;
		if(f11 > 1.0F) {
			f11 = 1.0F;
		}

		this.field_704_R += (f11 - this.field_704_R) * 0.4F;
		this.field_703_S += this.field_704_R;
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
					this.motionX += 0.05D;
				}
			} else {
				d5 = this.posX - entity1.posX;
				if(d5 > 0.5D) {
					this.motionX -= 0.05D;
				}
			}

			if(this.posZ < (double)i4) {
				d5 = entity1.posZ - this.posZ;
				if(d5 > 0.5D) {
					this.motionZ += 0.05D;
				}
			} else {
				d5 = this.posZ - entity1.posZ;
				if(d5 > 0.5D) {
					this.motionZ -= 0.05D;
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

	protected void updatePlayerActionState() {
		if(this.riddenByEntity == null || !this.tamed) {
			this.hasAttacked = false;
			float f1 = 16.0F;
			if(this.playerToAttack == null) {
				this.playerToAttack = this.findPlayerToAttack();
				if(this.playerToAttack != null && this.playerToAttack.inWater) {
					this.a = this.worldObj.getPathToEntity(this, this.playerToAttack, f1);
				}
			} else if(this.playerToAttack.isEntityAlive() && this.playerToAttack.inWater) {
				float f2 = this.playerToAttack.getDistanceToEntity(this);
				if(this.canEntityBeSeen(this.playerToAttack)) {
					this.attackEntity(this.playerToAttack, f2);
				}
			} else {
				this.playerToAttack = null;
			}

			if(this.hasAttacked || this.playerToAttack == null || !this.playerToAttack.inWater || this.a != null && this.rand.nextInt(20) != 0) {
				if(this.a == null && this.rand.nextInt(80) == 0 || this.rand.nextInt(80) == 0) {
					boolean z20 = false;
					int i3 = -1;
					int i4 = -1;
					int i5 = -1;
					float f6 = -99999.0F;

					for(int i7 = 0; i7 < 10; ++i7) {
						int i8 = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0D);
						int i9 = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0D);
						int i10 = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0D);
						float f11 = this.getBlockPathWeight(i8, i9, i10);
						if(f11 > f6) {
							f6 = f11;
							i3 = i8;
							i4 = i9;
							i5 = i10;
							z20 = true;
						}
					}

					if(z20) {
						this.a = this.worldObj.getEntityPathToXYZ(this, i3, i4, i5, 10.0F);
					}
				}
			} else {
				this.a = this.worldObj.getPathToEntity(this, this.playerToAttack, f1);
			}

			int i21 = MathHelper.floor_double(this.boundingBox.minY);
			boolean z22 = this.handleWaterMovement();
			boolean z23 = this.handleLavaMovement();
			this.rotationPitch = 0.0F;
			if(this.a != null && this.rand.nextInt(100) != 0) {
				Vec3D vec3D24 = this.a.getPosition(this);
				double d25 = (double)(this.width * 2.0F);

				while(vec3D24 != null && vec3D24.squareDistanceTo(this.posX, vec3D24.yCoord, this.posZ) < d25 * d25) {
					this.a.incrementPathIndex();
					if(this.a.isFinished()) {
						vec3D24 = null;
						this.a = null;
					} else {
						vec3D24 = this.a.getPosition(this);
					}
				}

				this.isJumping = false;
				if(vec3D24 != null) {
					d25 = vec3D24.xCoord - this.posX;
					double d26 = vec3D24.zCoord - this.posZ;
					double d27 = vec3D24.yCoord - (double)i21;
					float f12 = (float)(Math.atan2(d26, d25) * 180.0D / 3.141592741012573D) - 90.0F;
					float f13 = f12 - this.rotationYaw;

					for(this.moveForward = this.moveSpeed; f13 < -180.0F; f13 += 360.0F) {
					}

					while(f13 >= 180.0F) {
						f13 -= 360.0F;
					}

					if(f13 > 30.0F) {
						f13 = 30.0F;
					}

					if(f13 < -30.0F) {
						f13 = -30.0F;
					}

					this.rotationYaw += f13;
					if(this.hasAttacked && this.playerToAttack != null) {
						double d14 = this.playerToAttack.posX - this.posX;
						double d16 = this.playerToAttack.posZ - this.posZ;
						float f18 = this.rotationYaw;
						this.rotationYaw = (float)(Math.atan2(d16, d14) * 180.0D / 3.141592741012573D) - 90.0F;
						float f19 = (f18 - this.rotationYaw + 90.0F) * 3.141593F / 180.0F;
						this.moveStrafing = -MathHelper.sin(f19) * this.moveForward * 1.0F;
						this.moveForward = MathHelper.cos(f19) * this.moveForward * 1.0F;
					}

					if(d27 > 0.0D && this.playerToAttack != null && this.playerToAttack.inWater) {
						this.isJumping = true;
					}
				}

				if(this.playerToAttack != null) {
					this.faceEntity(this.playerToAttack, 30.0F, 30.0F);
				}

				if(this.isCollidedHorizontally) {
					this.isJumping = true;
				}

				if(this.rand.nextFloat() < 0.8F && (z22 || z23)) {
					this.isJumping = true;
				}

			} else {
				super.updatePlayerActionState();
				this.a = null;
			}
		}
	}

	protected void fall(float f1) {
		if(!this.inWater) {
			super.fall(f1);
		}

	}

	public EntityItem getClosestFish(Entity entity1, double d2) {
		double d4 = -1.0D;
		EntityItem entityItem6 = null;
		List list7 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(d2, d2, d2));

		for(int i8 = 0; i8 < list7.size(); ++i8) {
			Entity entity9 = (Entity)list7.get(i8);
			if(entity9 instanceof EntityItem) {
				EntityItem entityItem10 = (EntityItem)entity9;
				if(entityItem10.item.itemID == Item.fishRaw.shiftedIndex && entityItem10.inWater) {
					double d11 = entityItem10.getDistanceSq(entity1.posX, entity1.posY, entity1.posZ);
					if((d2 < 0.0D || d11 < d2 * d2) && (d4 == -1.0D || d11 < d4)) {
						d4 = d11;
						entityItem6 = entityItem10;
					}
				}
			}
		}

		return entityItem6;
	}

	public void onLivingUpdate() {
		if(this.onGround && this.inWater && !this.gettingOutOfWater()) {
			this.motionY += 0.03D;
		}

		if(!this.inWater && this.rand.nextInt(20) == 0 && this.riddenByEntity == null) {
			++this.outOfWater;
			this.posY += (double)(this.outOfWater / 30);
			this.attackEntityFrom(this, 1);
		}

		if(this.health <= 0 || !this.inWater && this.riddenByEntity == null) {
			this.isJumping = false;
			this.moveStrafing = 0.0F;
			this.moveForward = 0.0F;
			this.randomYawVelocity = 0.0F;
		} else if(!this.isMultiplayerEntity) {
			this.updatePlayerActionState();
		}

		boolean z1 = this.handleWaterMovement();
		boolean z2 = this.gettingOutOfWater();
		if(this.isJumping && z1 && !z2) {
			this.motionY += 0.02D;
		}

		this.moveStrafing *= 0.98F;
		this.moveForward *= 0.98F;
		this.randomYawVelocity *= 0.9F;
		this.moveEntityWithHeading(this.moveStrafing, this.moveForward);
		List list3 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.2000000029802322D, 0.0D, 0.2000000029802322D));
		if(list3 != null && list3.size() > 0) {
			for(int i4 = 0; i4 < list3.size(); ++i4) {
				Entity entity5 = (Entity)list3.get(i4);
				if(entity5.canBePushed()) {
					entity5.applyEntityCollision(this);
				}
			}
		}

	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
	}

	protected String getLivingSound() {
		return null;
	}

	protected String getHurtSound() {
		return null;
	}

	protected String getDeathSound() {
		return null;
	}

	protected float getSoundVolume() {
		return 0.4F;
	}

	protected String getUpsetSound() {
		return null;
	}
}
