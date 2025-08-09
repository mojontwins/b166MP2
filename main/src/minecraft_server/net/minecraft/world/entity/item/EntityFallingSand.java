package net.minecraft.world.entity.item;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.BlockSand;

public class EntityFallingSand extends Entity {
	public int blockID;
	public int fallTime = 0;

	public EntityFallingSand(World world) {
		super(world);
	}

	public EntityFallingSand(World world, double x, double y, double z, int blockID) {
		super(world);
		this.blockID = blockID;
		this.preventEntitySpawning = true;
		this.setSize(0.98F, 0.98F);
		this.yOffset = this.height / 2.0F;
		this.setPosition(x, y, z);
		this.motionX = 0.0D;
		this.motionY = 0.0D;
		this.motionZ = 0.0D;
		this.prevPosX = x;
		this.prevPosY = y;
		this.prevPosZ = z;
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	protected void entityInit() {
	}

	public boolean canBeCollidedWith() {
		return !this.isDead;
	}

	public void onUpdate() {
		if(this.blockID == 0) {
			this.setDead();
		} else {
			this.prevPosX = this.posX;
			this.prevPosY = this.posY;
			this.prevPosZ = this.posZ;

			++this.fallTime;

			this.motionY -= (double)0.04F;
			this.moveEntity(this.motionX, this.motionY, this.motionZ);

			this.motionX *= (double)0.98F;
			this.motionY *= (double)0.98F;
			this.motionZ *= (double)0.98F;

			int x = MathHelper.floor_double(this.posX);
			int y = MathHelper.floor_double(this.posY);
			int z = MathHelper.floor_double(this.posZ);

			if(this.fallTime == 1 && this.worldObj.getBlockId(x, y, z) == this.blockID) {
				this.worldObj.setBlockWithNotify(x, y, z, 0);
			} /*else if(!this.worldObj.isRemote && this.fallTime == 1) {
				this.setDead();
			}*/ // <- this bit was not in beta

			if(this.onGround) {
				this.motionX *= (double)0.7F;
				this.motionZ *= (double)0.7F;
				this.motionY *= -0.5D;

				this.setDead();
				
				if(
					(
						!this.worldObj.canBlockBePlacedAt(this.blockID, x, y, z, true, 1) || 
						BlockSand.canFallBelow(this.worldObj, x, y - 1, z) || 
						!this.worldObj.setBlockWithNotify(x, y, z, this.blockID)
					) && !this.worldObj.isRemote
				) {
					this.dropItem(this.blockID, 1);
				}
			} else if(this.fallTime > 100 && !this.worldObj.isRemote && 
				(y < 1 || y > 256) || this.fallTime > 600) {
				this.dropItem(this.blockID, 1);
				this.setDead();
			}

		}
	}

	protected void writeEntityToNBT(NBTTagCompound compoundTag) {
		compoundTag.setShort("Tile", (short)this.blockID);
	}

	protected void readEntityFromNBT(NBTTagCompound compoundTag) {
		try {
			this.blockID = compoundTag.getShort("Tile") & 4095;
		} catch (ClassCastException e) {
			this.blockID = compoundTag.getByte("Tile") & 255;
		}
	}

	public float getShadowSize() {
		return 0.0F;
	}

	public World getWorld() {
		return this.worldObj;
	}
}
