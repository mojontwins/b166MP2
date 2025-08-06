package net.minecraft.world.entity.monster;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.animal.IAnimals;
import net.minecraft.world.level.World;

public abstract class EntityGolem extends EntityCreature implements IAnimals {
	public EntityGolem(World world1) {
		super(world1);
	}

	protected void fall(float f1) {
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
	}

	protected String getLivingSound() {
		return "none";
	}

	protected String getHurtSound() {
		return "none";
	}

	protected String getDeathSound() {
		return "none";
	}

	public int getTalkInterval() {
		return 120;
	}

	protected boolean canDespawn() {
		return false;
	}
}
