package net.minecraft.world.entity.monster;

import net.minecraft.world.item.ItemStack;

public interface IArmoredMob {
	public void setArmor(int type, ItemStack itemStack);
	
	public ItemStack getArmor(int type);
}
