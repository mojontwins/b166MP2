package net.minecraft.world.entity.monster;

import net.minecraft.util.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.Vec3D;

public abstract class EntityMocFlyerMob extends EntityMocMob {
	protected int c;
	private PathEntity entitypath;
	public double speedModifier;

	public EntityMocFlyerMob(World world) {
		super(world);
		this.isCollidedVertically = false;
		this.speedModifier = 0.03D;
		this.setSize(1.5F, 1.5F);
		this.c = 3;
		this.health = 10;
	}

	protected void attackEntity(Entity entity, float f) {
		if(this.attackTime <= 0 && (double)f < 2.5D && entity.boundingBox.maxY > this.boundingBox.minY && entity.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity.attackEntityFrom(DamageSource.causeMobDamage(this), this.c);
		}

	}

	protected void fall(float f) {
	}

	protected Entity findPlayerToAttack() {
		EntityPlayer entityplayer = this.worldObj.getClosestPlayerToEntity(this, 20.0D);
		return entityplayer != null && this.canEntityBeSeen(entityplayer) ? entityplayer : null;
	}

	public boolean getCanSpawnHere() {
		return super.getCanSpawnHere();
	}

	public boolean isOnLadder() {
		return false;
	}

	public void moveEntityWithHeading(float f, float f1) {
		double d2;
		if(this.handleWaterMovement()) {
			d2 = this.posY;
			this.moveFlying(f, f1, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)0.8F;
			this.motionY *= (double)0.8F;
			this.motionZ *= (double)0.8F;
		} else if(this.handleLavaMovement()) {
			d2 = this.posY;
			this.moveFlying(f, f1, 0.02F);
			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= 0.5D;
			this.motionY *= 0.5D;
			this.motionZ *= 0.5D;
		} else {
			float d21 = 0.91F;
			if(this.onGround) {
				d21 = 0.5460001F;
				int f3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if(f3 > 0) {
					d21 = Block.blocksList[f3].slipperiness * 0.91F;
				}
			}

			float f31 = 0.162771F / (d21 * d21 * d21);
			this.moveFlying(f, f1, this.onGround ? 0.1F * f31 : 0.02F);
			d21 = 0.91F;
			if(this.onGround) {
				d21 = 0.5460001F;
				int d3 = this.worldObj.getBlockId(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ));
				if(d3 > 0) {
					d21 = Block.blocksList[d3].slipperiness * 0.91F;
				}
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			this.motionX *= (double)d21;
			this.motionY *= (double)d21;
			this.motionZ *= (double)d21;
			if(this.isCollidedHorizontally) {
				this.motionY = 0.2D;
			}

			if(this.rand.nextInt(30) == 0) {
				this.motionY = -0.25D;
			}
		}

		d2 = this.posX - this.prevPosX;
		double d31 = this.posZ - this.prevPosZ;
		float f4 = MathHelper.sqrt_double(d2 * d2 + d31 * d31) * 4.0F;
		if(f4 > 1.0F) {
			f4 = 1.0F;
		}

	}

	protected void updateEntityActionState() {
		this.hasAttacked = false;
		float f = 16.0F;
		if(this.entityToAttack == null) {
			this.entityToAttack = this.findPlayerToAttack();
			if(this.entityToAttack != null) {
				this.entitypath = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f, true, false, false, true);
			}
		} else if(!this.entityToAttack.isEntityAlive()) {
			this.entityToAttack = null;
		} else {
			float i = this.entityToAttack.getDistanceToEntity(this);
			if(this.canEntityBeSeen(this.entityToAttack)) {
				this.attackEntity(this.entityToAttack, i);
			}
		}

		if(!this.hasAttacked && this.entityToAttack != null && (this.entitypath == null || this.rand.nextInt(10) == 0)) {
			this.entitypath = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f, true, false, false, true);
		} else if(this.entitypath == null && this.rand.nextInt(80) == 0 || this.rand.nextInt(80) == 0) {
			boolean z20 = false;
			int flag1 = -1;
			int flag2 = -1;
			int vec3d = -1;
			float d1 = -99999.0F;

			for(int i1 = 0; i1 < 10; ++i1) {
				int d2 = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0D);
				int k1 = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0D);
				int d3 = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0D);
				float f3 = this.getBlockPathWeight(d2, k1, d3);
				if(f3 > d1) {
					d1 = f3;
					flag1 = d2;
					flag2 = k1;
					vec3d = d3;
					z20 = true;
				}
			}

			if(z20) {
				this.entitypath = this.worldObj.getEntityPathToXYZ(this, flag1, flag2, vec3d, 10.0F, true, false, false, true);
			}
		}

		int i21 = MathHelper.floor_double(this.boundingBox.minY);
		boolean z22 = this.handleWaterMovement();
		boolean z23 = this.handleLavaMovement();
		this.rotationPitch = 0.0F;
		if(this.entitypath != null && this.rand.nextInt(100) != 0) {
			Vec3D vec3D24 = this.entitypath.getCurrentNodeVec3d(this);
			double d25 = (double)(this.width * 2.0F);

			while(vec3D24 != null && vec3D24.squareDistanceTo(this.posX, vec3D24.yCoord, this.posZ) < d25 * d25) {
				this.entitypath.incrementPathIndex();
				if(this.entitypath.isFinished()) {
					vec3D24 = null;
					this.entitypath = null;
				} else {
					vec3D24 = this.entitypath.getCurrentNodeVec3d(this);
				}
			}

			this.isJumping = false;
			if(vec3D24 != null) {
				d25 = vec3D24.xCoord - this.posX;
				double d26 = vec3D24.zCoord - this.posZ;
				double d27 = vec3D24.yCoord - (double)i21;
				float f4 = (float)(Math.atan2(d26, d25) * 180.0D / 3.141592741012573D) - 90.0F;
				float f5 = f4 - this.rotationYaw;

				for(this.moveForward = this.moveSpeed; f5 < -180.0F; f5 += 360.0F) {
				}

				while(f5 >= 180.0F) {
					f5 -= 360.0F;
				}

				if(f5 > 30.0F) {
					f5 = 30.0F;
				}

				if(f5 < -30.0F) {
					f5 = -30.0F;
				}

				this.rotationYaw += f5;
				if(this.hasAttacked && this.entityToAttack != null) {
					double d4 = this.entityToAttack.posX - this.posX;
					double d5 = this.entityToAttack.posZ - this.posZ;
					float f6 = this.rotationYaw;
					this.rotationYaw = (float)(Math.atan2(d5, d4) * 180.0D / 3.141592741012573D) - 90.0F;
					float f7 = (f6 - this.rotationYaw + 90.0F) * 3.141593F / 180.0F;
					this.moveStrafing = -MathHelper.sin(f7) * this.moveForward * 1.0F;
					this.moveForward = MathHelper.cos(f7) * this.moveForward * 1.0F;
				}

				if(d27 > 0.0D) {
					this.isJumping = true;
				}
			}

			if(this.entityToAttack != null) {
				this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
			}

			if(this.isCollidedHorizontally) {
				this.isJumping = true;
			}

			if(this.rand.nextFloat() < 0.8F && (z22 || z23)) {
				this.isJumping = true;
			}

		} else {
			super.updateEntityActionState();
			this.entitypath = null;
		}
	}
}
