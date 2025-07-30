package net.minecraft.world.entity;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.level.World;

public abstract class EntityAgeable extends EntityCreature {
	public EntityAgeable(World world1) {
		super(world1);
	}

	protected void entityInit() {
		super.entityInit();
		this.dataWatcher.addObject(12, new Integer(0));
	}

	public int getGrowingAge() {
		return this.dataWatcher.getWatchableObjectInt(12);
	}

	public void setGrowingAge(int i1) {
		this.dataWatcher.updateObject(12, i1);
	}

	public void writeEntityToNBT(NBTTagCompound compoundTag) {
		super.writeEntityToNBT(compoundTag);
		compoundTag.setInteger("Age", this.getGrowingAge());
	}

	public void readEntityFromNBT(NBTTagCompound compoundTag) {
		super.readEntityFromNBT(compoundTag);
		this.setGrowingAge(compoundTag.getInteger("Age"));
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		int i1 = this.getGrowingAge();
		if(i1 < 0) {
			++i1;
			this.setGrowingAge(i1);
		} else if(i1 > 0) {
			--i1;
			this.setGrowingAge(i1);
		}

	}

	public boolean isChild() {
		return this.getGrowingAge() < 0;
	}
}
