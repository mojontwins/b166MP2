package net.minecraft.world.level;

import com.mojang.nbt.NBTTagCompound;

public abstract class WorldSavedData {
	public final String mapName;
	private boolean dirty;

	public WorldSavedData(String string1) {
		this.mapName = string1;
	}

	public abstract void readFromNBT(NBTTagCompound compoundTag);

	public abstract void writeToNBT(NBTTagCompound compoundTag);

	public void markDirty() {
		this.setDirty(true);
	}

	public void setDirty(boolean z1) {
		this.dirty = z1;
	}

	public boolean isDirty() {
		return this.dirty;
	}
}
