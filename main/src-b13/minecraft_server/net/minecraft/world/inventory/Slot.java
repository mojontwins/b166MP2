package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class Slot {
	private final int slotIndex;
	public final IInventory inventory;
	public int slotNumber;
	public int xDisplayPosition;
	public int yDisplayPosition;

	public Slot(IInventory iInventory1, int i2, int i3, int i4) {
		this.inventory = iInventory1;
		this.slotIndex = i2;
		this.xDisplayPosition = i3;
		this.yDisplayPosition = i4;
	}

	public void onSlotChange(ItemStack itemStack1, ItemStack itemStack2) {
		if (itemStack1 != null && itemStack2 != null) {
			if (itemStack1.itemID == itemStack2.itemID) {
				int i3 = itemStack2.stackSize - itemStack1.stackSize;
				if (i3 > 0) {
					this.onCrafting(itemStack1, i3);
				}

			}
		}
	}

	protected void onCrafting(ItemStack itemStack1, int i2) {
	}

	protected void onCrafting(ItemStack itemStack1) {
	}

	public void onPickupFromSlot(EntityPlayer entityPlayer, ItemStack itemStack1) {
		this.onPickupFromSlot(itemStack1);
	}
	
	public void onPickupFromSlot(ItemStack itemStack1) {
		this.onSlotChanged();
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return true;
	}

	public ItemStack getStack() {
		return this.inventory.getStackInSlot(this.slotIndex);
	}

	public boolean getHasStack() {
		return this.getStack() != null;
	}

	public void putStack(ItemStack itemStack1) {
		this.inventory.setInventorySlotContents(this.slotIndex, itemStack1);
		this.onSlotChanged();
	}

	public void onSlotChanged() {
		this.inventory.onInventoryChanged();
	}

	public int getSlotStackLimit() {
		return this.inventory.getInventoryStackLimit();
	}

	public int getBackgroundIconIndex() {
		return -1;
	}

	public ItemStack decrStackSize(int i1) {
		return this.inventory.decrStackSize(this.slotIndex, i1);
	}

	public boolean isHere(IInventory iInventory1, int i2) {
		return iInventory1 == this.inventory && i2 == this.slotIndex;
	}

	public boolean isSlotInInventory(IInventory par1IInventory, int par2) {
		return par1IInventory == this.inventory && par2 == this.slotIndex;
	}

	public boolean canTakeStack(EntityPlayer var1) {
		return true;
	}
}
