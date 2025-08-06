package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class InventoryCraftResult implements IInventory {
	private ItemStack[] stackResult = new ItemStack[1];

	public int getSizeInventory() {
		return 1;
	}

	public ItemStack getStackInSlot(int i1) {
		return this.stackResult[i1];
	}

	public String getInvName() {
		return "Result";
	}

	public ItemStack decrStackSize(int i1, int i2) {
		if(this.stackResult[i1] != null) {
			ItemStack itemStack3 = this.stackResult[i1];
			this.stackResult[i1] = null;
			return itemStack3;
		} else {
			return null;
		}
	}

	public ItemStack getStackInSlotOnClosing(int i1) {
		if(this.stackResult[i1] != null) {
			ItemStack itemStack2 = this.stackResult[i1];
			this.stackResult[i1] = null;
			return itemStack2;
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i1, ItemStack itemStack2) {
		this.stackResult[i1] = itemStack2;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void onInventoryChanged() {
	}

	public boolean isUseableByPlayer(EntityPlayer entityPlayer1) {
		return true;
	}

	public void openChest() {
	}

	public void closeChest() {
	}
}
