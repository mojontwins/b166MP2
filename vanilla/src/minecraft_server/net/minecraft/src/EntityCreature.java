package net.minecraft.src;

public class EntityCreature extends EntityLiving {
	private PathEntity pathToEntity;
	protected Entity playerToAttack;
	protected boolean hasAttacked = false;

	public EntityCreature(World world1) {
		super(world1);
	}

	protected boolean func_25026_u() {
		return false;
	}

	protected void updatePlayerActionState() {
		this.hasAttacked = this.func_25026_u();
		float f1 = 16.0F;
		if(this.playerToAttack == null) {
			this.playerToAttack = this.findPlayerToAttack();
			if(this.playerToAttack != null) {
				this.pathToEntity = this.worldObj.getPathToEntity(this, this.playerToAttack, f1);
			}
		} else if(!this.playerToAttack.isEntityAlive()) {
			this.playerToAttack = null;
		} else {
			float f2 = this.playerToAttack.getDistanceToEntity(this);
			if(this.canEntityBeSeen(this.playerToAttack)) {
				this.attackEntity(this.playerToAttack, f2);
			} else {
				this.func_28013_b(this.playerToAttack, f2);
			}
		}

		if(this.hasAttacked || this.playerToAttack == null || this.pathToEntity != null && this.rand.nextInt(20) != 0) {
			if(!this.hasAttacked && (this.pathToEntity == null && this.rand.nextInt(80) == 0 || this.rand.nextInt(80) == 0)) {
				boolean z21 = false;
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
						z21 = true;
					}
				}

				if(z21) {
					this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, i3, i4, i5, 10.0F);
				}
			}
		} else {
			this.pathToEntity = this.worldObj.getPathToEntity(this, this.playerToAttack, f1);
		}

		int i22 = MathHelper.floor_double(this.boundingBox.minY + 0.5D);
		boolean z23 = this.func_27011_Z();
		boolean z24 = this.handleLavaMovement();
		this.rotationPitch = 0.0F;
		if(this.pathToEntity != null && this.rand.nextInt(100) != 0) {
			Vec3D vec3D25 = this.pathToEntity.getPosition(this);
			double d26 = (double)(this.width * 2.0F);

			while(vec3D25 != null && vec3D25.squareDistanceTo(this.posX, vec3D25.yCoord, this.posZ) < d26 * d26) {
				this.pathToEntity.incrementPathIndex();
				if(this.pathToEntity.isFinished()) {
					vec3D25 = null;
					this.pathToEntity = null;
				} else {
					vec3D25 = this.pathToEntity.getPosition(this);
				}
			}

			this.isJumping = false;
			if(vec3D25 != null) {
				double d27 = vec3D25.xCoord - this.posX;
				double d28 = vec3D25.zCoord - this.posZ;
				double d12 = vec3D25.yCoord - (double)i22;
				float f14 = (float)(Math.atan2(d28, d27) * 180.0D / (double)(float)Math.PI) - 90.0F;
				float f15 = f14 - this.rotationYaw;

				for(this.moveForward = this.moveSpeed; f15 < -180.0F; f15 += 360.0F) {
				}

				while(f15 >= 180.0F) {
					f15 -= 360.0F;
				}

				if(f15 > 30.0F) {
					f15 = 30.0F;
				}

				if(f15 < -30.0F) {
					f15 = -30.0F;
				}

				this.rotationYaw += f15;
				if(this.hasAttacked && this.playerToAttack != null) {
					double d16 = this.playerToAttack.posX - this.posX;
					double d18 = this.playerToAttack.posZ - this.posZ;
					float f20 = this.rotationYaw;
					this.rotationYaw = (float)(Math.atan2(d18, d16) * 180.0D / (double)(float)Math.PI) - 90.0F;
					f15 = (f20 - this.rotationYaw + 90.0F) * (float)Math.PI / 180.0F;
					this.moveStrafing = -MathHelper.sin(f15) * this.moveForward * 1.0F;
					this.moveForward = MathHelper.cos(f15) * this.moveForward * 1.0F;
				}

				if(d12 > 0.0D) {
					this.isJumping = true;
				}
			}

			if(this.playerToAttack != null) {
				this.faceEntity(this.playerToAttack, 30.0F, 30.0F);
			}

			if(this.isCollidedHorizontally && !this.getGotPath()) {
				this.isJumping = true;
			}

			if(this.rand.nextFloat() < 0.8F && (z23 || z24)) {
				this.isJumping = true;
			}

		} else {
			super.updatePlayerActionState();
			this.pathToEntity = null;
		}
	}

	protected void attackEntity(Entity entity1, float f2) {
	}

	protected void func_28013_b(Entity entity1, float f2) {
	}

	protected float getBlockPathWeight(int i1, int i2, int i3) {
		return 0.0F;
	}

	protected Entity findPlayerToAttack() {
		return null;
	}

	public boolean getCanSpawnHere() {
		int i1 = MathHelper.floor_double(this.posX);
		int i2 = MathHelper.floor_double(this.boundingBox.minY);
		int i3 = MathHelper.floor_double(this.posZ);
		return super.getCanSpawnHere() && this.getBlockPathWeight(i1, i2, i3) >= 0.0F;
	}

	public boolean getGotPath() {
		return this.pathToEntity != null;
	}

	public void setPathToEntity(PathEntity pathEntity1) {
		this.pathToEntity = pathEntity1;
	}

	public Entity getEntityToAttack() {
		return this.playerToAttack;
	}

	public void setEntityToAttack(Entity entity1) {
		this.playerToAttack = entity1;
	}
}
