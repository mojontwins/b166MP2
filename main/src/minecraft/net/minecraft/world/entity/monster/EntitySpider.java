package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DataWatchers;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EnumCreatureAttribute;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.World;

public class EntitySpider extends EntityMob implements IMob {
	public EntitySpider(World world1) {
		super(world1);
		this.texture = "/mob/spider.png";
		this.setSize(1.4F, 0.9F);
		this.moveSpeed = 0.8F;
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(DataWatchers.DW_STATUS, Byte.valueOf((byte)0));
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
	}

	public void onUpdate() {
		super.onUpdate();
		if(!this.worldObj.isRemote) {
			this.collidedHorizontally(this.isCollidedHorizontally);
		}

	}

	public int getMaxHealth() {
		return 16;
	}

	public double getMountedYOffset() {
		return (double)this.height * 0.75D - 0.5D;
	}

	protected boolean canTriggerWalking() {
		return false;
	}

	protected Entity findPlayerToAttack() {
		float f1 = this.getBrightness(1.0F);
		if(f1 < 0.5F) {
			double d2 = 16.0D;
			return this.worldObj.getClosestVulnerablePlayerToEntity(this, d2);
		} else {
			return null;
		}
	}

	protected String getLivingSound() {
		return "mob.spider";
	}

	protected String getHurtSound() {
		return "mob.spider";
	}

	protected String getDeathSound() {
		return "mob.spiderdeath";
	}

	protected void attackEntity(Entity entity1, float f2) {
		float f3 = this.getBrightness(1.0F);
		if(f3 > 0.5F && this.rand.nextInt(100) == 0) {
			this.entityToAttack = null;
		} else {
			if(f2 > 2.0F && f2 < 6.0F && this.rand.nextInt(10) == 0) {
				if(this.onGround) {
					double d4 = entity1.posX - this.posX;
					double d6 = entity1.posZ - this.posZ;
					float f8 = MathHelper.sqrt_double(d4 * d4 + d6 * d6);
					this.motionX = d4 / (double)f8 * 0.5D * (double)0.8F + this.motionX * (double)0.2F;
					this.motionZ = d6 / (double)f8 * 0.5D * (double)0.8F + this.motionZ * (double)0.2F;
					this.motionY = (double)0.4F;
				}
			} else {
				super.attackEntity(entity1, f2);
			}

		}
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	protected int getDropItemId() {
		return Item.silk.shiftedIndex;
	}

	protected void dropFewItems(boolean z1, int i2) {
		super.dropFewItems(z1, i2);
		if(z1 && (this.rand.nextInt(3) == 0 || this.rand.nextInt(1 + i2) > 0)) {
			//this.dropItem(Item.spiderEye.shiftedIndex, 1);
		}

	}

	public boolean isOnLadder() {
		return this.isCollidedHorizontally();
	}

	public void setInWeb() {
	}

	public float spiderScaleAmount() {
		return 1.0F;
	}

	public EnumCreatureAttribute getCreatureAttribute() {
		return EnumCreatureAttribute.ARTHROPOD;
	}

	public boolean isCollidedHorizontally() {
		return (this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS) & 1) != 0;
	}

	public void collidedHorizontally(boolean z1) {
		byte b2 = this.dataWatcher.getWatchableObjectByte(DataWatchers.DW_STATUS);
		if(z1) {
			b2 = (byte)(b2 | 1);
		} else {
			b2 &= -2;
		}

		this.dataWatcher.updateObject(DataWatchers.DW_STATUS, b2);
	}
}
