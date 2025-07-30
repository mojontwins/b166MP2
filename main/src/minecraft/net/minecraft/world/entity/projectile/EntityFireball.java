package net.minecraft.world.entity.projectile;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class EntityFireball extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private boolean inGround = false;
	public EntityLiving shootingEntity;
	private int ticksAlive;
	private int ticksInAir = 0;
	public double accelerationX;
	public double accelerationY;
	public double accelerationZ;

	public EntityFireball(World world1) {
		super(world1);
		this.setSize(1.0F, 1.0F);
	}

	protected void entityInit() {
	}

	public boolean isInRangeToRenderDist(double d1) {
		double d3 = this.boundingBox.getAverageEdgeLength() * 4.0D;
		d3 *= 64.0D;
		return d1 < d3 * d3;
	}

	public EntityFireball(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1);
		this.setSize(1.0F, 1.0F);
		this.setLocationAndAngles(d2, d4, d6, this.rotationYaw, this.rotationPitch);
		this.setPosition(d2, d4, d6);
		double d14 = (double)MathHelper.sqrt_double(d8 * d8 + d10 * d10 + d12 * d12);
		this.accelerationX = d8 / d14 * 0.1D;
		this.accelerationY = d10 / d14 * 0.1D;
		this.accelerationZ = d12 / d14 * 0.1D;
	}

	public EntityFireball(World world1, EntityLiving entityLiving2, double d3, double d5, double d7) {
		super(world1);
		this.shootingEntity = entityLiving2;
		this.setSize(1.0F, 1.0F);
		this.setLocationAndAngles(entityLiving2.posX, entityLiving2.posY, entityLiving2.posZ, entityLiving2.rotationYaw, entityLiving2.rotationPitch);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		d3 += this.rand.nextGaussian() * 0.4D;
		d5 += this.rand.nextGaussian() * 0.4D;
		d7 += this.rand.nextGaussian() * 0.4D;
		double d9 = (double)MathHelper.sqrt_double(d3 * d3 + d5 * d5 + d7 * d7);
		this.accelerationX = d3 / d9 * 0.1D;
		this.accelerationY = d5 / d9 * 0.1D;
		this.accelerationZ = d7 / d9 * 0.1D;
	}

	public void onUpdate() {
		if(this.worldObj.isRemote || (this.shootingEntity == null || !this.shootingEntity.isDead) && this.worldObj.blockExists((int)this.posX, (int)this.posY, (int)this.posZ)) {
			super.onUpdate();
			this.setFire(1);
			if(this.inGround) {
				int i1 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
				if(i1 == this.inTile) {
					++this.ticksAlive;
					if(this.ticksAlive == 600) {
						this.setDead();
					}

					return;
				}

				this.inGround = false;
				this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
				this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
				this.ticksAlive = 0;
				this.ticksInAir = 0;
			} else {
				++this.ticksInAir;
			}

			Vec3D vec3D15 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			Vec3D vec3D2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			MovingObjectPosition movingObjectPosition3 = this.worldObj.rayTraceBlocks(vec3D15, vec3D2);
			vec3D15 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			vec3D2 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			if(movingObjectPosition3 != null) {
				vec3D2 = Vec3D.createVector(movingObjectPosition3.hitVec.xCoord, movingObjectPosition3.hitVec.yCoord, movingObjectPosition3.hitVec.zCoord);
			}

			Entity entity4 = null;
			List<Entity> list5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d6 = 0.0D;

			for(int i8 = 0; i8 < list5.size(); ++i8) {
				Entity entity9 = (Entity)list5.get(i8);
				if(entity9.canBeCollidedWith() && (!entity9.isEntityEqual(this.shootingEntity) || this.ticksInAir >= 25)) {
					float f10 = 0.3F;
					AxisAlignedBB axisAlignedBB11 = entity9.boundingBox.expand((double)f10, (double)f10, (double)f10);
					MovingObjectPosition movingObjectPosition12 = axisAlignedBB11.calculateIntercept(vec3D15, vec3D2);
					if(movingObjectPosition12 != null) {
						double d13 = vec3D15.distanceTo(movingObjectPosition12.hitVec);
						if(d13 < d6 || d6 == 0.0D) {
							entity4 = entity9;
							d6 = d13;
						}
					}
				}
			}

			if(entity4 != null) {
				movingObjectPosition3 = new MovingObjectPosition(entity4);
			}

			if(movingObjectPosition3 != null) {
				this.throwableHitEntity(movingObjectPosition3);
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			float f16 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / (double)(float)Math.PI);

			for(this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f16) * 180.0D / (double)(float)Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
			}

			while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
				this.prevRotationPitch += 360.0F;
			}

			while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
				this.prevRotationYaw -= 360.0F;
			}

			while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
				this.prevRotationYaw += 360.0F;
			}

			this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
			this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;
			float f17 = 0.95F;
			if(this.isInWater()) {
				for(int i18 = 0; i18 < 4; ++i18) {
					float f19 = 0.25F;
					this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f19, this.posY - this.motionY * (double)f19, this.posZ - this.motionZ * (double)f19, this.motionX, this.motionY, this.motionZ);
				}

				f17 = 0.8F;
			}

			this.motionX += this.accelerationX;
			this.motionY += this.accelerationY;
			this.motionZ += this.accelerationZ;
			this.motionX *= (double)f17;
			this.motionY *= (double)f17;
			this.motionZ *= (double)f17;
			this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
			this.setPosition(this.posX, this.posY, this.posZ);
		} else {
			this.setDead();
		}
	}

	protected void throwableHitEntity(MovingObjectPosition movingObjectPosition1) {
		if(!this.worldObj.isRemote) {
			if(movingObjectPosition1.entityHit != null && movingObjectPosition1.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 4)) {
				;
			}

			this.worldObj.newExplosion((Entity)null, this.posX, this.posY, this.posZ, 1.0F, true);
			this.setDead();
		}

	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		compoundTag.setShort("xTile", (short)this.xTile);
		compoundTag.setShort("yTile", (short)this.yTile);
		compoundTag.setShort("zTile", (short)this.zTile);
		compoundTag.setByte("inTile", (byte)this.inTile);
		compoundTag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		this.xTile = compoundTag.getShort("xTile");
		this.yTile = compoundTag.getShort("yTile");
		this.zTile = compoundTag.getShort("zTile");
		this.inTile = compoundTag.getByte("inTile") & 255;
		this.inGround = compoundTag.getByte("inGround") == 1;
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	public float getCollisionBorderSize() {
		return 1.0F;
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		this.setBeenAttacked();
		if(damageSource1.getEntity() != null) {
			Vec3D vec3D3 = damageSource1.getEntity().getLookVec();
			if(vec3D3 != null) {
				this.motionX = vec3D3.xCoord;
				this.motionY = vec3D3.yCoord;
				this.motionZ = vec3D3.zCoord;
				this.accelerationX = this.motionX * 0.1D;
				this.accelerationY = this.motionY * 0.1D;
				this.accelerationZ = this.motionZ * 0.1D;
			}

			if(damageSource1.getEntity() instanceof EntityLiving) {
				System.out.println ("Changed owner! ");
				this.shootingEntity = (EntityLiving)damageSource1.getEntity();
			}

			return true;
		} else {
			return false;
		}
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public float getBrightness(float f1) {
		return 1.0F;
	}

	public int getBrightnessForRender(float f1) {
		return 15728880;
	}
}
