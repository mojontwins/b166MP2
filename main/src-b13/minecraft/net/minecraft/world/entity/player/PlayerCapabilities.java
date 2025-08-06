package net.minecraft.world.entity.player;

import com.mojang.nbt.NBTTagCompound;

public class PlayerCapabilities {
	public boolean disableDamage = false;
	public boolean isFlying = false;
	public boolean allowFlying = false;
	public boolean isCreativeMode = false;

	public void writeCapabilitiesToNBT(NBTTagCompound compoundTag) {
		NBTTagCompound nBTTagCompound2 = new NBTTagCompound();
		nBTTagCompound2.setBoolean("invulnerable", this.disableDamage);
		nBTTagCompound2.setBoolean("flying", this.isFlying);
		nBTTagCompound2.setBoolean("mayfly", this.allowFlying);
		nBTTagCompound2.setBoolean("instabuild", this.isCreativeMode);
		compoundTag.setTag("abilities", nBTTagCompound2);
	}

	public void readCapabilitiesFromNBT(NBTTagCompound compoundTag) {
		if(compoundTag.hasKey("abilities")) {
			NBTTagCompound nBTTagCompound2 = compoundTag.getCompoundTag("abilities");
			this.disableDamage = nBTTagCompound2.getBoolean("invulnerable");
			this.isFlying = nBTTagCompound2.getBoolean("flying");
			this.allowFlying = nBTTagCompound2.getBoolean("mayfly");
			this.isCreativeMode = nBTTagCompound2.getBoolean("instabuild");
		}

	}
	
	public String toString() {
		return "Capabilities: disableDamage=" + this.disableDamage + 
				", isFlying=" + this.isFlying +
				", allowFlying=" + this.allowFlying + 
				", isCreativeMode=" + this.isCreativeMode;
	}
}
