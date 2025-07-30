package org.mojontwins.retroopts;

import net.minecraft.src.NBTTagCompound;

public class RetroSettings {
	public static boolean enableHunger = true;
	public static boolean enableSprinting = true;
	
	public RetroSettings() {
		// TODO Auto-generated constructor stub
	}

	public static void loadRetroSettings(NBTTagCompound nbt) {
		RetroSettings.enableHunger = nbt.hasKey("enableHunger") ? 
				nbt.getBoolean("enableHunger") : true;
		RetroSettings.enableSprinting = nbt.hasKey("enableSprinting") ? 
				nbt.getBoolean("enableSprinting") : true;
	}
	
	public static void saveRetroSettings(NBTTagCompound nbt) {
		nbt.setBoolean("enableHunger", RetroSettings.enableHunger);
		nbt.setBoolean("enableSprinting", RetroSettings.enableSprinting);
	}
	
	public static void resetDefaults() {
		RetroSettings.enableHunger = true;
		RetroSettings.enableSprinting = true;
	}
}
