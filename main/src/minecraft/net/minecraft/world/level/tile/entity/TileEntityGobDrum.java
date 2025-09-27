package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.world.level.World;

public class TileEntityGobDrum extends TileEntity {
	public byte note = 0;
	public boolean previousRedstoneState = false;

	public void writeToNBT(NBTTagCompound par1NBTTagCompound) {
		super.writeToNBT(par1NBTTagCompound);
		par1NBTTagCompound.setByte("note", this.note);
	}

	public void readFromNBT(NBTTagCompound par1NBTTagCompound) {
		super.readFromNBT(par1NBTTagCompound);
		this.note = par1NBTTagCompound.getByte("note");
		if(this.note < 0) {
			this.note = 0;
		}

		if(this.note > 24) {
			this.note = 24;
		}

	}

	public void changePitch() {
		this.note = (byte)((this.note + 1) % 25);
		this.onInventoryChanged();
	}

	public void triggerNote(World par1World, int par2, int par3, int par4) {
		par1World.getBlockMaterial(par2, par3 - 1, par4);
		byte byte0 = 1;
		par1World.playNoteAt(par2, par3, par4, byte0, this.note);
	}
}
