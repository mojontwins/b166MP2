package net.minecraft.src;

public class EntityFlyerMob extends EntityMob {
	protected int c;
	private PathEntity entitypath;
	public double speedModifier;

	public EntityFlyerMob(World world1) {
		super(world1);
		this.isCollidedVertically = false;
		this.speedModifier = 0.03D;
		this.setSize(1.5F, 1.5F);
		this.c = 3;
		this.health = 10;
	}

	protected void fall(float f1) {
	}

	public void moveEntityWithHeading(float f1, float f2) {
		double d3;
		if(this.handleWaterMovement()) {
			d3 = this.posY;
			this.moveFlying(f1, f2, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)0.8F;
			this.motionY *= (double)0.8F;
			this.motionZ *= (double)0.8F;
		} else if(this.handleLavaMovement()) {
			d3 = this.posY;
			this.moveFlying(f1, f2, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float f8 = 0.91F;
			if(this.onGround) {
				f8 = 0.5460001F;
				int i4 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if(i4 > 0) {
					f8 = Block.blocksList[i4].slipperiness * 0.91F;
				}
			}

			float f9 = 0.162771F / (f8 * f8 * f8);
			this.moveFlying(f1, f2, this.onGround ? 0.1F * f9 : 0.02F);
			f8 = 0.91F;
			if(this.onGround) {
				f8 = 0.5460001F;
				int i5 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if(i5 > 0) {
					f8 = Block.blocksList[i5].slipperiness * 0.91F;
				}
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)f8;
			this.motionY *= (double)f8;
			this.motionZ *= (double)f8;
			if(this.isCollidedHorizontally) {
				this.motionY = 0.2D;
			}

			if(this.rand.nextInt(30) == 0) {
				this.motionY = -0.25D;
			}
		}

		this.field_705_Q = this.field_704_R;
		d3 = this.posX - this.prevPosX;
		double d10 = this.posZ - this.prevPosZ;
		float f7 = MathHelper.sqrt_double(d3 * d3 + d10 * d10) * 4.0F;
		if(f7 > 1.0F) {
			f7 = 1.0F;
		}

		this.field_704_R += (f7 - this.field_704_R) * 0.4F;
		this.field_703_S += this.field_704_R;
	}

	public boolean isOnLadder() {
		return false;
	}

	protected Entity findPlayerToAttack() {
		EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, 20.0D);
		return entityPlayer1 != null && this.canEntityBeSeen(entityPlayer1) ? entityPlayer1 : null;
	}

	protected void updatePlayerActionState() {
		this.hasAttacked = false;
		float f1 = 16.0F;
		if(this.playerToAttack == null) {
			this.playerToAttack = this.findPlayerToAttack();
			if(this.playerToAttack != null) {
				this.entitypath = this.worldObj.getPathToEntity(this, this.playerToAttack, f1);
			}
		} else if(!this.playerToAttack.isEntityAlive()) {
			this.playerToAttack = null;
		} else {
			float f2 = this.playerToAttack.getDistanceToEntity(this);
			if(this.canEntityBeSeen(this.playerToAttack)) {
				this.attackEntity(this.playerToAttack, f2);
			}
		}

		if(!this.hasAttacked && this.playerToAttack != null && (this.entitypath == null || this.rand.nextInt(10) == 0)) {
			this.entitypath = this.worldObj.getPathToEntity(this, this.playerToAttack, f1);
		} else if(this.entitypath == null && this.rand.nextInt(80) == 0 || this.rand.nextInt(80) == 0) {
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
				this.entitypath = this.worldObj.getEntityPathToXYZ(this, i3, i4, i5, 10.0F);
			}
		}

		int i21 = MathHelper.floor_double(this.boundingBox.minY);
		boolean z22 = this.handleWaterMovement();
		boolean z23 = this.handleLavaMovement();
		this.rotationPitch = 0.0F;
		if(this.entitypath != null && this.rand.nextInt(100) != 0) {
			Vec3D vec3D24 = this.entitypath.getPosition(this);
			double d25 = (double)(this.width * 2.0F);

			while(vec3D24 != null && vec3D24.squareDistanceTo(this.posX, vec3D24.yCoord, this.posZ) < d25 * d25) {
				this.entitypath.incrementPathIndex();
				if(this.entitypath.isFinished()) {
					vec3D24 = null;
					this.entitypath = null;
				} else {
					vec3D24 = this.entitypath.getPosition(this);
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

				if(d27 > 0.0D) {
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
			this.entitypath = null;
		}
	}

	protected void attackEntity(Entity entity1, float f2) {
		if((double)f2 < 2.5D && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity1.attackEntityFrom(this, this.c);
		}

	}

	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}
}
