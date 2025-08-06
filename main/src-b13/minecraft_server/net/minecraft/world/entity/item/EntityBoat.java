package net.minecraft.world.entity.item;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.AxisAlignedBB;

public class EntityBoat extends Entity {
	private int boatPosRotationIncrements;
	private double boatX;
	private double boatY;
	private double boatZ;
	private double boatYaw;
	private double boatPitch;
	private double velocityX;
	private double velocityY;
	private double velocityZ;

	public EntityBoat(World world1) {
		super(world1);
		this.preventEntitySpawning = true;
		this.setSize(1.5F, 0.6F);
		this.yOffset = this.height / 2.0F;
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
		this.dataWatcher.addObject(17, Integer.valueOf(0));
		this.dataWatcher.addObject(18, Integer.valueOf(1));
		this.dataWatcher.addObject(19, Integer.valueOf(0));
	}

	public AxisAlignedBB getCollisionBox(Entity entity1) {
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

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		if(!this.worldObj.isRemote && !this.isDead) {
			this.setForwardDirection(-this.getForwardDirection());
			this.setTimeSinceHit(10);
			this.setDamageTaken(this.getDamageTaken() + i2 * 10);
			this.setBeenAttacked();
			if(this.getDamageTaken() > 40) {
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

				this.setDead();
			}

			return true;
		} else {
			return true;
		}
	}

	public void performHurtAnimation() {
		this.setForwardDirection(-this.getForwardDirection());
		this.setTimeSinceHit(10);
		this.setDamageTaken(this.getDamageTaken() * 11);
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void setPositionAndRotation2(double d1, double d3, double d5, float f7, float f8, int i9) {
		this.boatX = d1;
		this.boatY = d3;
		this.boatZ = d5;
		this.boatYaw = (double)f7;
		this.boatPitch = (double)f8;
		this.boatPosRotationIncrements = i9 + 4;
		this.motionX = this.velocityX;
		this.motionY = this.velocityY;
		this.motionZ = this.velocityZ;
	}

	public void setVelocity(double d1, double d3, double d5) {
		this.velocityX = this.motionX = d1;
		this.velocityY = this.motionY = d3;
		this.velocityZ = this.motionZ = d5;
	}

	public void onUpdate() {
		super.onUpdate();
		if(this.getTimeSinceHit() > 0) {
			this.setTimeSinceHit(this.getTimeSinceHit() - 1);
		}

		if(this.getDamageTaken() > 0) {
			this.setDamageTaken(this.getDamageTaken() - 1);
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

		double d21 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
		double d6;
		double d8;
		if(d21 > 0.15D) {
			d6 = Math.cos((double)this.rotationYaw * Math.PI / 180.0D);
			d8 = Math.sin((double)this.rotationYaw * Math.PI / 180.0D);

			for(int i10 = 0; (double)i10 < 1.0D + d21 * 60.0D; ++i10) {
				double d11 = (double)(this.rand.nextFloat() * 2.0F - 1.0F);
				double d13 = (double)(this.rand.nextInt(2) * 2 - 1) * 0.7D;
				double d15;
				double d17;
				if(this.rand.nextBoolean()) {
					d15 = this.posX - d6 * d11 * 0.8D + d8 * d13;
					d17 = this.posZ - d8 * d11 * 0.8D - d6 * d13;
					this.worldObj.spawnParticle("splash", d15, this.posY - 0.125D, d17, this.motionX, this.motionY, this.motionZ);
				} else {
					d15 = this.posX + d6 + d8 * d11 * 0.7D;
					d17 = this.posZ + d8 - d6 * d11 * 0.7D;
					this.worldObj.spawnParticle("splash", d15, this.posY - 0.125D, d17, this.motionX, this.motionY, this.motionZ);
				}
			}
		}

		double d12;
		double d23;
		if(this.worldObj.isRemote) {
			if(this.boatPosRotationIncrements > 0) {
				d6 = this.posX + (this.boatX - this.posX) / (double)this.boatPosRotationIncrements;
				d8 = this.posY + (this.boatY - this.posY) / (double)this.boatPosRotationIncrements;
				d23 = this.posZ + (this.boatZ - this.posZ) / (double)this.boatPosRotationIncrements;

				for(d12 = this.boatYaw - (double)this.rotationYaw; d12 < -180.0D; d12 += 360.0D) {
				}

				while(d12 >= 180.0D) {
					d12 -= 360.0D;
				}

				this.rotationYaw = (float)((double)this.rotationYaw + d12 / (double)this.boatPosRotationIncrements);
				this.rotationPitch = (float)((double)this.rotationPitch + (this.boatPitch - (double)this.rotationPitch) / (double)this.boatPosRotationIncrements);
				--this.boatPosRotationIncrements;
				this.setPosition(d6, d8, d23);
				this.setRotation(this.rotationYaw, this.rotationPitch);
			} else {
				d6 = this.posX + this.motionX;
				d8 = this.posY + this.motionY;
				d23 = this.posZ + this.motionZ;
				this.setPosition(d6, d8, d23);
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
				d6 = d2 * 2.0D - 1.0D;
				this.motionY += (double)0.04F * d6;
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

			d6 = 0.4D;
			if(this.motionX < -d6) {
				this.motionX = -d6;
			}

			if(this.motionX > d6) {
				this.motionX = d6;
			}

			if(this.motionZ < -d6) {
				this.motionZ = -d6;
			}

			if(this.motionZ > d6) {
				this.motionZ = d6;
			}

			if(this.onGround) {
				this.motionX *= 0.5D;
				this.motionY *= 0.5D;
				this.motionZ *= 0.5D;
			}

			this.moveEntity(this.motionX, this.motionY, this.motionZ);
			if(this.isCollidedHorizontally && d21 > 0.2D) {
				if(!this.worldObj.isRemote) {
					this.setDead();

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
			d23 = this.prevPosX - this.posX;
			d12 = this.prevPosZ - this.posZ;
			if(d23 * d23 + d12 * d12 > 0.001D) {
				d8 = (double)((float)(Math.atan2(d12, d23) * 180.0D / Math.PI));
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
			List<Entity> list16 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand((double)0.2F, 0.0D, (double)0.2F));
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
				int i19 = MathHelper.floor_double(this.posY);
				int i20 = MathHelper.floor_double(this.posZ + ((double)(i24 / 2) - 0.5D) * 0.8D);
				if(this.worldObj.getBlockId(i25, i19, i20) == Block.snow.blockID) {
					this.worldObj.setBlockWithNotify(i25, i19, i20, 0);
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

	protected void writeEntityToNBT(NBTTagCompound compoundTag) {
	}

	protected void readEntityFromNBT(NBTTagCompound compoundTag) {
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public boolean interact(EntityPlayer entityPlayer1) {
		if(this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer && this.riddenByEntity != entityPlayer1) {
			return true;
		} else {
			if(!this.worldObj.isRemote) {
				entityPlayer1.mountEntity(this);
			}

			return true;
		}
	}

	public void setDamageTaken(int i1) {
		this.dataWatcher.updateObject(19, i1);
	}

	public int getDamageTaken() {
		return this.dataWatcher.getWatchableObjectInt(19);
	}

	public void setTimeSinceHit(int i1) {
		this.dataWatcher.updateObject(17, i1);
	}

	public int getTimeSinceHit() {
		return this.dataWatcher.getWatchableObjectInt(17);
	}

	public void setForwardDirection(int i1) {
		this.dataWatcher.updateObject(18, i1);
	}

	public int getForwardDirection() {
		return this.dataWatcher.getWatchableObjectInt(18);
	}
}
