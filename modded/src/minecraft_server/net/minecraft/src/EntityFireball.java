package net.minecraft.src;

import java.util.List;

public class EntityFireball extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private boolean inGround = false;
	public int shake = 0;
	public EntityLiving owner;
	private int field_9190_an;
	private int ticksInAir = 0;
	public double field_9199_b;
	public double field_9198_c;
	public double field_9196_d;

	public EntityFireball(World world1) {
		super(world1);
		this.setSize(1.0F, 1.0F);
	}

	protected void entityInit() {
	}

	public EntityFireball(World world1, EntityLiving entityLiving2, double d3, double d5, double d7) {
		super(world1);
		this.owner = entityLiving2;
		this.setSize(1.0F, 1.0F);
		this.setLocationAndAngles(entityLiving2.posX, entityLiving2.posY, entityLiving2.posZ, entityLiving2.rotationYaw, entityLiving2.rotationPitch);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = this.motionY = this.motionZ = 0.0D;
		d3 += this.rand.nextGaussian() * 0.4D;
		d5 += this.rand.nextGaussian() * 0.4D;
		d7 += this.rand.nextGaussian() * 0.4D;
		double d9 = (double)MathHelper.sqrt_double(d3 * d3 + d5 * d5 + d7 * d7);
		this.field_9199_b = d3 / d9 * 0.1D;
		this.field_9198_c = d5 / d9 * 0.1D;
		this.field_9196_d = d7 / d9 * 0.1D;
	}

	public void onUpdate() {
		super.onUpdate();
		this.fire = 10;
		if(this.shake > 0) {
			--this.shake;
		}

		if(this.inGround) {
			int i1 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
			if(i1 == this.inTile) {
				++this.field_9190_an;
				if(this.field_9190_an == 1200) {
					this.setEntityDead();
				}

				return;
			}

			this.inGround = false;
			this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
			this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
			this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
			this.field_9190_an = 0;
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
		List list5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
		double d6 = 0.0D;

		for(int i8 = 0; i8 < list5.size(); ++i8) {
			Entity entity9 = (Entity)list5.get(i8);
			if(entity9.canBeCollidedWith() && (entity9 != this.owner || this.ticksInAir >= 25)) {
				float f10 = 0.3F;
				AxisAlignedBB axisAlignedBB11 = entity9.boundingBox.expand((double)f10, (double)f10, (double)f10);
				MovingObjectPosition movingObjectPosition12 = axisAlignedBB11.func_706_a(vec3D15, vec3D2);
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
			if(!this.worldObj.singleplayerWorld) {
				if(movingObjectPosition3.entityHit != null && movingObjectPosition3.entityHit.attackEntityFrom(this.owner, 0)) {
					;
				}

				this.worldObj.newExplosion((Entity)null, this.posX, this.posY, this.posZ, 1.0F, true);
			}

			this.setEntityDead();
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
		if(this.func_27011_Z()) {
			for(int i18 = 0; i18 < 4; ++i18) {
				float f19 = 0.25F;
				this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f19, this.posY - this.motionY * (double)f19, this.posZ - this.motionZ * (double)f19, this.motionX, this.motionY, this.motionZ);
			}

			f17 = 0.8F;
		}

		this.motionX += this.field_9199_b;
		this.motionY += this.field_9198_c;
		this.motionZ += this.field_9196_d;
		this.motionX *= (double)f17;
		this.motionY *= (double)f17;
		this.motionZ *= (double)f17;
		this.worldObj.spawnParticle("smoke", this.posX, this.posY + 0.5D, this.posZ, 0.0D, 0.0D, 0.0D);
		this.setPosition(this.posX, this.posY, this.posZ);
	}

	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		nBTTagCompound1.setShort("xTile", (short)this.xTile);
		nBTTagCompound1.setShort("yTile", (short)this.yTile);
		nBTTagCompound1.setShort("zTile", (short)this.zTile);
		nBTTagCompound1.setByte("inTile", (byte)this.inTile);
		nBTTagCompound1.setByte("shake", (byte)this.shake);
		nBTTagCompound1.setByte("inGround", (byte)(this.inGround ? 1 : 0));
	}

	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		this.xTile = nBTTagCompound1.getShort("xTile");
		this.yTile = nBTTagCompound1.getShort("yTile");
		this.zTile = nBTTagCompound1.getShort("zTile");
		this.inTile = nBTTagCompound1.getByte("inTile") & 255;
		this.shake = nBTTagCompound1.getByte("shake") & 255;
		this.inGround = nBTTagCompound1.getByte("inGround") == 1;
	}

	public boolean canBeCollidedWith() {
		return true;
	}

	public boolean attackEntityFrom(Entity entity1, int i2) {
		this.setBeenAttacked();
		if(entity1 != null) {
			Vec3D vec3D3 = entity1.getLookVec();
			if(vec3D3 != null) {
				this.motionX = vec3D3.xCoord;
				this.motionY = vec3D3.yCoord;
				this.motionZ = vec3D3.zCoord;
				this.field_9199_b = this.motionX * 0.1D;
				this.field_9198_c = this.motionY * 0.1D;
				this.field_9196_d = this.motionZ * 0.1D;
			}

			return true;
		} else {
			return false;
		}
	}
}
