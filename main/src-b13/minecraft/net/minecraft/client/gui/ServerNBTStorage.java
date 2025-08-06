package net.minecraft.client.gui;

import com.mojang.nbt.NBTTagCompound;

public class ServerNBTStorage {
	public String name;
	public String host;
	public String playerCount;
	public String motd;
	public long lag;
	public boolean polled = false;

	public ServerNBTStorage(String string1, String string2) {
		this.name = string1;
		this.host = string2;
	}

	public NBTTagCompound getCompoundTag() {
		NBTTagCompound compoundTag = new NBTTagCompound();
		compoundTag.setString("name", this.name);
		compoundTag.setString("ip", this.host);
		return compoundTag;
	}

	public static ServerNBTStorage createServerNBTStorage(NBTTagCompound nBTTagCompound0) {
		return new ServerNBTStorage(nBTTagCompound0.getString("name"), nBTTagCompound0.getString("ip"));
	}
}
