package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class InventoryLargeChest implements IInventory {
	private String name;
	private IInventory upperChest;
	private IInventory lowerChest;

	public InventoryLargeChest(String string1, IInventory iInventory2, IInventory iInventory3) {
		this.name = string1;
		if(iInventory2 == null) {
			iInventory2 = iInventory3;
		}

		if(iInventory3 == null) {
			iInventory3 = iInventory2;
		}

		this.upperChest = iInventory2;
		this.lowerChest = iInventory3;
	}

	public int getSizeInventory() {
		return this.upperChest.getSizeInventory() + this.lowerChest.getSizeInventory();
	}

	public String getInvName() {
		return this.name;
	}

	public ItemStack getStackInSlot(int i1) {
		return i1 >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlot(i1 - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlot(i1);
	}

	public ItemStack decrStackSize(int i1, int i2) {
		return i1 >= this.upperChest.getSizeInventory() ? this.lowerChest.decrStackSize(i1 - this.upperChest.getSizeInventory(), i2) : this.upperChest.decrStackSize(i1, i2);
	}

	public ItemStack getStackInSlotOnClosing(int i1) {
		return i1 >= this.upperChest.getSizeInventory() ? this.lowerChest.getStackInSlotOnClosing(i1 - this.upperChest.getSizeInventory()) : this.upperChest.getStackInSlotOnClosing(i1);
	}

	public void setInventorySlotContents(int i1, ItemStack itemStack2) {
		if(i1 >= this.upperChest.getSizeInventory()) {
			this.lowerChest.setInventorySlotContents(i1 - this.upperChest.getSizeInventory(), itemStack2);
		} else {
			this.upperChest.setInventorySlotContents(i1, itemStack2);
		}

	}

	public int getInventoryStackLimit() {
		return this.upperChest.getInventoryStackLimit();
	}

	public void onInventoryChanged() {
		this.upperChest.onInventoryChanged();
		this.lowerChest.onInventoryChanged();
	}

	public boolean isUseableByPlayer(EntityPlayer entityPlayer1) {
		return this.upperChest.isUseableByPlayer(entityPlayer1) && this.lowerChest.isUseableByPlayer(entityPlayer1);
	}

	public void openChest() {
		this.upperChest.openChest();
		this.lowerChest.openChest();
	}

	public void closeChest() {
		this.upperChest.closeChest();
		this.lowerChest.closeChest();
	}
}
