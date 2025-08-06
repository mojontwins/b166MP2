package net.minecraft.world.entity.animal;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;

public abstract class EntityWaterMob extends EntityCreature implements IAnimals {
	public EntityWaterMob(World world1) {
		super(world1);
	}

	public boolean canBreatheUnderwater() {
		return true;
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	public boolean getCanSpawnHere() {
		return this.worldObj.checkIfAABBIsClear(this.boundingBox);
	}

	public int getTalkInterval() {
		return 120;
	}

	protected boolean canDespawn() {
		return true;
	}

	protected int getExperiencePoints(EntityPlayer entityPlayer1) {
		return 1 + this.worldObj.rand.nextInt(3);
	}
}
