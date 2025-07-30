package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.World;

public abstract class EntityMob extends EntityCreature implements IMob {
	public int attackStrength = 2;

	public EntityMob(World world1) {
		super(world1);
		this.experienceValue = 5;
	}

	public void onLivingUpdate() {
		float f1 = this.getBrightness(1.0F);
		if(f1 > 0.5F) {
			this.entityAge += 2;
		}

		super.onLivingUpdate();
	}

	public void onUpdate() {
		super.onUpdate();
		if(!this.worldObj.isRemote && this.worldObj.difficultySetting == 0) {
			this.setDead();
		}

	}

	protected Entity findPlayerToAttack() {
		EntityPlayer entityPlayer1 = this.worldObj.getClosestVulnerablePlayerToEntity(this, 16.0D);
		return entityPlayer1 != null && this.canEntityBeSeen(entityPlayer1) ? entityPlayer1 : null;
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		if(super.attackEntityFrom(damageSource1, i2)) {
			Entity entity3 = damageSource1.getEntity();
			if(this.riddenByEntity != entity3 && this.ridingEntity != entity3) {
				if(entity3 != this) {
					this.entityToAttack = entity3;
				}

				return true;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public boolean attackEntityAsMob(Entity entity1) {
		int i2 = this.attackStrength;
		
		// TODO: Increase i2 on damageBoost or decrease on Weakness

		return entity1.attackEntityFrom(DamageSource.causeMobDamage(this), i2);
	}

	protected void attackEntity(Entity entity1, float f2) {
		if(this.attackTime <= 0 && f2 < 2.0F && entity1.boundingBox.maxY > this.boundingBox.minY && entity1.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			this.attackEntityAsMob(entity1);
		}

	}

	public float getBlockPathWeight(int i1, int i2, int i3) {
		return 0.5F - this.worldObj.getLightBrightness(i1, i2, i3);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	protected boolean isValidLightLevel() {
		int i1 = MathHelper.floor_double(this.posX);
		int i2 = MathHelper.floor_double(this.boundingBox.minY);
		int i3 = MathHelper.floor_double(this.posZ);
		if(this.worldObj.getSavedLightValue(EnumSkyBlock.Sky, i1, i2, i3) > this.rand.nextInt(32)) {
			return false;
		} else {
			int i4 = this.worldObj.getBlockLightValue(i1, i2, i3);
			if(this.worldObj.isThundering()) {
				int i5 = this.worldObj.skylightSubtracted;
				this.worldObj.skylightSubtracted = 10;
				i4 = this.worldObj.getBlockLightValue(i1, i2, i3);
				this.worldObj.skylightSubtracted = i5;
			}

			return i4 <= this.rand.nextInt(8);
		}
	}

	public boolean getCanSpawnHere() {
		return this.isValidLightLevel() && super.getCanSpawnHere();
	}
}
