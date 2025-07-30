package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public interface IInventory {
	int getSizeInventory();

	ItemStack getStackInSlot(int i1);

	ItemStack decrStackSize(int i1, int i2);

	ItemStack getStackInSlotOnClosing(int i1);

	void setInventorySlotContents(int i1, ItemStack itemStack2);

	String getInvName();

	int getInventoryStackLimit();

	void onInventoryChanged();

	boolean isUseableByPlayer(EntityPlayer entityPlayer1);

	void openChest();

	void closeChest();
}
