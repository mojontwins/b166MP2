package net.minecraft.world.entity.projectile;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public class EntityArrow extends Entity {
	private int xTile = -1;
	private int yTile = -1;
	private int zTile = -1;
	private int inTile = 0;
	private int inData = 0;
	private boolean inGround = false;
	public boolean doesArrowBelongToPlayer = false;
	public int arrowShake = 0;
	public Entity shootingEntity;
	protected int ticksInGround;
	private int ticksInAir = 0;
	private double damage = 2.0D;
	private int field_46027_au;
	public boolean arrowCritical = false;

	public EntityArrow(World world1) {
		super(world1);
		this.setSize(0.5F, 0.5F);
	}

	public EntityArrow(World world1, double d2, double d4, double d6) {
		super(world1);
		this.setSize(0.5F, 0.5F);
		this.setPosition(d2, d4, d6);
		this.yOffset = 0.0F;
	}

	public EntityArrow(World world1, EntityLiving entityLiving2, EntityLiving entityLiving3, float f4, float f5) {
		super(world1);
		this.shootingEntity = entityLiving2;
		this.doesArrowBelongToPlayer = entityLiving2 instanceof EntityPlayer;
		this.posY = entityLiving2.posY + (double)entityLiving2.getEyeHeight() - (double)0.1F;
		double d6 = entityLiving3.posX - entityLiving2.posX;
		double d8 = entityLiving3.posY + (double)entityLiving3.getEyeHeight() - (double)0.7F - this.posY;
		double d10 = entityLiving3.posZ - entityLiving2.posZ;
		double d12 = (double)MathHelper.sqrt_double(d6 * d6 + d10 * d10);
		if(d12 >= 1.0E-7D) {
			float f14 = (float)(Math.atan2(d10, d6) * 180.0D / (double)(float)Math.PI) - 90.0F;
			float f15 = (float)(-(Math.atan2(d8, d12) * 180.0D / (double)(float)Math.PI));
			double d16 = d6 / d12;
			double d18 = d10 / d12;
			this.setLocationAndAngles(entityLiving2.posX + d16, this.posY, entityLiving2.posZ + d18, f14, f15);
			this.yOffset = 0.0F;
			float f20 = (float)d12 * 0.2F;
			this.setArrowHeading(d6, d8 + (double)f20, d10, f4, f5);
		}
	}

	public EntityArrow(World world1, EntityLiving entityLiving2, float f3) {
		super(world1);
		this.shootingEntity = entityLiving2;
		this.doesArrowBelongToPlayer = entityLiving2 instanceof EntityPlayer;
		this.setSize(0.5F, 0.5F);
		this.setLocationAndAngles(entityLiving2.posX, entityLiving2.posY + (double)entityLiving2.getEyeHeight(), entityLiving2.posZ, entityLiving2.rotationYaw, entityLiving2.rotationPitch);
		this.posX -= (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.posY -= (double)0.1F;
		this.posZ -= (double)(MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * 0.16F);
		this.setPosition(this.posX, this.posY, this.posZ);
		this.yOffset = 0.0F;
		this.motionX = (double)(-MathHelper.sin(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionZ = (double)(MathHelper.cos(this.rotationYaw / 180.0F * (float)Math.PI) * MathHelper.cos(this.rotationPitch / 180.0F * (float)Math.PI));
		this.motionY = (double)(-MathHelper.sin(this.rotationPitch / 180.0F * (float)Math.PI));
		this.setArrowHeading(this.motionX, this.motionY, this.motionZ, f3 * 1.5F, 1.0F);
	}

	protected void entityInit() {
	}

	public void setArrowHeading(double d1, double d3, double d5, float f7, float f8) {
		float f9 = MathHelper.sqrt_double(d1 * d1 + d3 * d3 + d5 * d5);
		d1 /= (double)f9;
		d3 /= (double)f9;
		d5 /= (double)f9;
		d1 += this.rand.nextGaussian() * (double)0.0075F * (double)f8;
		d3 += this.rand.nextGaussian() * (double)0.0075F * (double)f8;
		d5 += this.rand.nextGaussian() * (double)0.0075F * (double)f8;
		d1 *= (double)f7;
		d3 *= (double)f7;
		d5 *= (double)f7;
		this.motionX = d1;
		this.motionY = d3;
		this.motionZ = d5;
		float f10 = MathHelper.sqrt_double(d1 * d1 + d5 * d5);
		this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(d1, d5) * 180.0D / (double)(float)Math.PI);
		this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(d3, (double)f10) * 180.0D / (double)(float)Math.PI);
		this.ticksInGround = 0;
	}

	public void setVelocity(double d1, double d3, double d5) {
		this.motionX = d1;
		this.motionY = d3;
		this.motionZ = d5;
		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f7 = MathHelper.sqrt_double(d1 * d1 + d5 * d5);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(d1, d5) * 180.0D / (double)(float)Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(d3, (double)f7) * 180.0D / (double)(float)Math.PI);
			this.prevRotationPitch = this.rotationPitch;
			this.prevRotationYaw = this.rotationYaw;
			this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
			this.ticksInGround = 0;
		}

	}

	public void onUpdate() {
		super.onUpdate();
		if(this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
			float f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / (double)(float)Math.PI);
			this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f1) * 180.0D / (double)(float)Math.PI);
		}

		int i15 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
		if(i15 > 0) {
			Block.blocksList[i15].setBlockBoundsBasedOnState(this.worldObj, this.xTile, this.yTile, this.zTile);
			AxisAlignedBB axisAlignedBB2 = Block.blocksList[i15].getCollisionBoundingBoxFromPool(this.worldObj, this.xTile, this.yTile, this.zTile);
			if(axisAlignedBB2 != null && axisAlignedBB2.isVecInside(Vec3D.createVector(this.posX, this.posY, this.posZ))) {
				this.inGround = true;
			}
		}

		if(this.arrowShake > 0) {
			--this.arrowShake;
		}

		if(this.inGround) {
			i15 = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
			int i18 = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
			if(i15 == this.inTile && i18 == this.inData) {
				++this.ticksInGround;
				if(this.ticksInGround == 1200) {
					this.setDead();
				}

			} else {
				this.inGround = false;
				this.motionX *= (double)(this.rand.nextFloat() * 0.2F);
				this.motionY *= (double)(this.rand.nextFloat() * 0.2F);
				this.motionZ *= (double)(this.rand.nextFloat() * 0.2F);
				this.ticksInGround = 0;
				this.ticksInAir = 0;
			}
		} else {
			++this.ticksInAir;
			Vec3D vec3D16 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			Vec3D vec3D17 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			MovingObjectPosition movingObjectPosition3 = this.worldObj.rayTraceBlocks_do_do(vec3D16, vec3D17, false, true);
			vec3D16 = Vec3D.createVector(this.posX, this.posY, this.posZ);
			vec3D17 = Vec3D.createVector(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
			if(movingObjectPosition3 != null) {
				vec3D17 = Vec3D.createVector(movingObjectPosition3.hitVec.xCoord, movingObjectPosition3.hitVec.yCoord, movingObjectPosition3.hitVec.zCoord);
			}

			Entity entity4 = null;
			List<Entity> list5 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0D, 1.0D, 1.0D));
			double d6 = 0.0D;

			int i8;
			float f10;
			for(i8 = 0; i8 < list5.size(); ++i8) {
				Entity entity9 = (Entity)list5.get(i8);
				if(entity9.canBeCollidedWith() && (entity9 != this.shootingEntity || this.ticksInAir >= 5)) {
					f10 = 0.3F;
					AxisAlignedBB axisAlignedBB11 = entity9.boundingBox.expand((double)f10, (double)f10, (double)f10);
					MovingObjectPosition movingObjectPosition12 = axisAlignedBB11.calculateIntercept(vec3D16, vec3D17);
					if(movingObjectPosition12 != null) {
						double d13 = vec3D16.distanceTo(movingObjectPosition12.hitVec);
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

			float f19;
			if(movingObjectPosition3 != null) {
				if(movingObjectPosition3.entityHit != null) {
					f19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					int i20 = (int)Math.ceil((double)f19 * this.damage);
					if(this.arrowCritical) {
						i20 += this.rand.nextInt(i20 / 2 + 2);
					}

					DamageSource damageSource21 = null;
					if(this.shootingEntity == null) {
						damageSource21 = DamageSource.causeArrowDamage(this, this);
					} else {
						damageSource21 = DamageSource.causeArrowDamage(this, this.shootingEntity);
					}

					if(this.isBurning()) {
						movingObjectPosition3.entityHit.setFire(5);
					}

					if(movingObjectPosition3.entityHit.attackEntityFrom(damageSource21, i20)) {
						if(movingObjectPosition3.entityHit instanceof EntityLiving) {
							++((EntityLiving)movingObjectPosition3.entityHit).arrowHitTempCounter;
							if(this.field_46027_au > 0) {
								float f23 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
								if(f23 > 0.0F) {
									movingObjectPosition3.entityHit.addVelocity(this.motionX * (double)this.field_46027_au * (double)0.6F / (double)f23, 0.1D, this.motionZ * (double)this.field_46027_au * (double)0.6F / (double)f23);
								}
							}
						}

						this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
						this.setDead();
					} else {
						this.motionX *= -0.10000000149011612D;
						this.motionY *= -0.10000000149011612D;
						this.motionZ *= -0.10000000149011612D;
						this.rotationYaw += 180.0F;
						this.prevRotationYaw += 180.0F;
						this.ticksInAir = 0;
					}
				} else {
					this.xTile = movingObjectPosition3.blockX;
					this.yTile = movingObjectPosition3.blockY;
					this.zTile = movingObjectPosition3.blockZ;
					this.inTile = this.worldObj.getBlockId(this.xTile, this.yTile, this.zTile);
					this.inData = this.worldObj.getBlockMetadata(this.xTile, this.yTile, this.zTile);
					this.motionX = (double)((float)(movingObjectPosition3.hitVec.xCoord - this.posX));
					this.motionY = (double)((float)(movingObjectPosition3.hitVec.yCoord - this.posY));
					this.motionZ = (double)((float)(movingObjectPosition3.hitVec.zCoord - this.posZ));
					f19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ);
					this.posX -= this.motionX / (double)f19 * (double)0.05F;
					this.posY -= this.motionY / (double)f19 * (double)0.05F;
					this.posZ -= this.motionZ / (double)f19 * (double)0.05F;
					this.worldObj.playSoundAtEntity(this, "random.bowhit", 1.0F, 1.2F / (this.rand.nextFloat() * 0.2F + 0.9F));
					this.inGround = true;
					this.arrowShake = 7;
					this.arrowCritical = false;
				}
			}

			if(this.arrowCritical) {
				for(i8 = 0; i8 < 4; ++i8) {
					this.worldObj.spawnParticle("crit", this.posX + this.motionX * (double)i8 / 4.0D, this.posY + this.motionY * (double)i8 / 4.0D, this.posZ + this.motionZ * (double)i8 / 4.0D, -this.motionX, -this.motionY + 0.2D, -this.motionZ);
				}
			}

			this.posX += this.motionX;
			this.posY += this.motionY;
			this.posZ += this.motionZ;
			f19 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.rotationYaw = (float)(Math.atan2(this.motionX, this.motionZ) * 180.0D / (double)(float)Math.PI);

			for(this.rotationPitch = (float)(Math.atan2(this.motionY, (double)f19) * 180.0D / (double)(float)Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
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
			float f22 = 0.99F;
			f10 = 0.05F;
			if(this.isInWater()) {
				for(int i24 = 0; i24 < 4; ++i24) {
					float f25 = 0.25F;
					this.worldObj.spawnParticle("bubble", this.posX - this.motionX * (double)f25, this.posY - this.motionY * (double)f25, this.posZ - this.motionZ * (double)f25, this.motionX, this.motionY, this.motionZ);
				}

				f22 = 0.8F;
			}

			this.motionX *= (double)f22;
			this.motionY *= (double)f22;
			this.motionZ *= (double)f22;
			this.motionY -= (double)f10;
			this.setPosition(this.posX, this.posY, this.posZ);
		}
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		compoundTag.setShort("xTile", (short)this.xTile);
		compoundTag.setShort("yTile", (short)this.yTile);
		compoundTag.setShort("zTile", (short)this.zTile);
		compoundTag.setByte("inTile", (byte)this.inTile);
		compoundTag.setByte("inData", (byte)this.inData);
		compoundTag.setByte("shake", (byte)this.arrowShake);
		compoundTag.setByte("inGround", (byte)(this.inGround ? 1 : 0));
		compoundTag.setBoolean("player", this.doesArrowBelongToPlayer);
		compoundTag.setDouble("damage", this.damage);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		this.xTile = compoundTag.getShort("xTile");
		this.yTile = compoundTag.getShort("yTile");
		this.zTile = compoundTag.getShort("zTile");
		this.inTile = compoundTag.getByte("inTile") & 255;
		this.inData = compoundTag.getByte("inData") & 255;
		this.arrowShake = compoundTag.getByte("shake") & 255;
		this.inGround = compoundTag.getByte("inGround") == 1;
		this.doesArrowBelongToPlayer = compoundTag.getBoolean("player");
		if(compoundTag.hasKey("damage")) {
			this.damage = compoundTag.getDouble("damage");
		}

	}

	public void onCollideWithPlayer(EntityPlayer entityPlayer1) {
		if(!this.worldObj.isRemote) {
			if(this.inGround && this.doesArrowBelongToPlayer && this.arrowShake <= 0 && entityPlayer1.inventory.addItemStackToInventory(new ItemStack(Item.arrow, 1))) {
				this.worldObj.playSoundAtEntity(this, "random.pop", 0.2F, ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.0F) * 2.0F);
				entityPlayer1.onItemPickup(this, 1);
				this.setDead();
			}

		}
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public void setDamage(double d1) {
		this.damage = d1;
	}

	public double getDamage() {
		return this.damage;
	}

	public void func_46023_b(int i1) {
		this.field_46027_au = i1;
	}

	public boolean canAttackWithItem() {
		return false;
	}
}
