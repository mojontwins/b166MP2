package net.minecraft.world.entity.animal;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.IWaterMob;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.material.Material;

public class EntitySquid extends EntityWaterMob implements IWaterMob {
	public float xBodyRot = 0.0F;
	public float xBodyRotO = 0.0F;
	public float zBodyRot = 0.0F;
	public float zBodyRotO = 0.0F;
	public float tentacleMovement = 0.0F;
	public float oldTentacleMovement = 0.0F;
	public float tentacleAngle = 0.0F;
	public float oldTentacleAngle = 0.0F;
	private float speed = 0.0F;
	private float tentacleSpeed = 0.0F;
	private float rotateSpeed = 0.0F;
	private float tx = 0.0F;
	private float ty = 0.0F;
	private float tz = 0.0F;

	public EntitySquid(World world1) {
		super(world1);
		this.texture = "/mob/squid.png";
		this.setSize(0.95F, 0.95F);
		this.tentacleSpeed = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
	}

	public int getMaxHealth() {
		return 10;
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
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

	protected int getDropItemId() {
		return 0;
	}

	protected void dropFewItems(boolean z1, int i2) {
		int i3 = this.rand.nextInt(3 + i2) + 1;

		for(int i4 = 0; i4 < i3; ++i4) {
			this.entityDropItem(new ItemStack(Item.dyePowder, 1, 0), 0.0F);
		}

	}

	public boolean interact(EntityPlayer entityPlayer1) {
		return super.interact(entityPlayer1);
	}

	public boolean isInWater() {
		return this.worldObj.handleMaterialAcceleration(this.boundingBox.expand(0.0D, -0.6000000238418579D, 0.0D), Material.water, this);
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		this.xBodyRotO = this.xBodyRot;
		this.zBodyRotO = this.zBodyRot;
		this.oldTentacleMovement = this.tentacleMovement;
		this.oldTentacleAngle = this.tentacleAngle;
		this.tentacleMovement += this.tentacleSpeed;
		if(this.tentacleMovement > 6.2831855F) {
			this.tentacleMovement -= 6.2831855F;
			if(this.rand.nextInt(10) == 0) {
				this.tentacleSpeed = 1.0F / (this.rand.nextFloat() + 1.0F) * 0.2F;
			}
		}

		if(this.isInWater()) {
			float f1;
			if(this.tentacleMovement < (float)Math.PI) {
				f1 = this.tentacleMovement / (float)Math.PI;
				this.tentacleAngle = MathHelper.sin(f1 * f1 * (float)Math.PI) * (float)Math.PI * 0.25F;
				if((double)f1 > 0.75D) {
					this.speed = 1.0F;
					this.rotateSpeed = 1.0F;
				} else {
					this.rotateSpeed *= 0.8F;
				}
			} else {
				this.tentacleAngle = 0.0F;
				this.speed *= 0.9F;
				this.rotateSpeed *= 0.99F;
			}

			if(!this.worldObj.isRemote) {
				this.motionX = (double)(this.tx * this.speed);
				this.motionY = (double)(this.ty * this.speed);
				this.motionZ = (double)(this.tz * this.speed);
			}

			f1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
			this.renderYawOffset += (-((float)Math.atan2(this.motionX, this.motionZ)) * 180.0F / (float)Math.PI - this.renderYawOffset) * 0.1F;
			this.rotationYaw = this.renderYawOffset;
			this.zBodyRot += (float)Math.PI * this.rotateSpeed * 1.5F;
			this.xBodyRot += (-((float)Math.atan2((double)f1, this.motionY)) * 180.0F / (float)Math.PI - this.xBodyRot) * 0.1F;
		} else {
			this.tentacleAngle = MathHelper.abs(MathHelper.sin(this.tentacleMovement)) * (float)Math.PI * 0.25F;
			if(!this.worldObj.isRemote) {
				this.motionX = 0.0D;
				this.motionY -= 0.08D;
				this.motionY *= (double)0.98F;
				this.motionZ = 0.0D;
			}

			this.xBodyRot = (float)((double)this.xBodyRot + (double)(-90.0F - this.xBodyRot) * 0.02D);
		}

	}

	public void moveEntityWithHeading(float f1, float f2) {
		this.moveEntity(this.motionX, this.motionY, this.motionZ);
	}

	protected void updateEntityActionState() {
		++this.entityAge;
		if(this.entityAge > 100) {
			this.tx = this.ty = this.tz = 0.0F;
		} else if(this.rand.nextInt(50) == 0 || !this.inWater || this.tx == 0.0F && this.ty == 0.0F && this.tz == 0.0F) {
			float f1 = this.rand.nextFloat() * (float)Math.PI * 2.0F;
			this.tx = MathHelper.cos(f1) * 0.2F;
			this.ty = -0.1F + this.rand.nextFloat() * 0.2F;
			this.tz = MathHelper.sin(f1) * 0.2F;
		}

		this.despawnEntity();
	}

	public boolean getCanSpawnHere() {
		return GameRules.boolRule("enableSquids") && this.posY > 45.0D && this.posY < 63.0D && super.getCanSpawnHere();
	}
}
