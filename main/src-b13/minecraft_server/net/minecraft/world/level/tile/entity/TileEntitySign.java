package net.minecraft.world.level.tile.entity;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet130UpdateSign;

public class TileEntitySign extends TileEntity {
	public String[] signText = new String[]{"", "", "", ""};
	public int lineBeingEdited = -1;
	private boolean isEditable = true;

	public void writeToNBT(NBTTagCompound compoundTag) {
		super.writeToNBT(compoundTag);
		compoundTag.setString("Text1", this.signText[0]);
		compoundTag.setString("Text2", this.signText[1]);
		compoundTag.setString("Text3", this.signText[2]);
		compoundTag.setString("Text4", this.signText[3]);
	}

	public void readFromNBT(NBTTagCompound compoundTag) {
		this.isEditable = false;
		super.readFromNBT(compoundTag);

		for(int i2 = 0; i2 < 4; ++i2) {
			this.signText[i2] = compoundTag.getString("Text" + (i2 + 1));
			if(this.signText[i2].length() > 15) {
				this.signText[i2] = this.signText[i2].substring(0, 15);
			}
		}

	}

	public boolean isEditable() {
		return this.isEditable;
	}
	
	public void setEditable(boolean isEditable) {
		this.isEditable = isEditable;
	}

	public Packet getDescriptionPacket() {
		String[] string1 = new String[4];

		for(int i2 = 0; i2 < 4; ++i2) {
			string1[i2] = this.signText[i2];
		}

		return new Packet130UpdateSign(this.xCoord, this.yCoord, this.zCoord, string1);
	}

}
