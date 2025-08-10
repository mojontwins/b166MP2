package net.minecraft.src;

public class AnimalChest implements IInventory {
	private String localstring;
	public ItemStack[] cheststack;

	public AnimalChest(ItemStack[] itemStack1, String string2) {
		this.cheststack = itemStack1;
		this.localstring = string2;
	}

	public int getSizeInventory() {
		return 27;
	}

	public ItemStack getStackInSlot(int i1) {
		return this.cheststack[i1];
	}

	public ItemStack decrStackSize(int i1, int i2) {
		if(this.cheststack[i1] != null) {
			ItemStack itemStack3;
			if(this.cheststack[i1].stackSize <= i2) {
				itemStack3 = this.cheststack[i1];
				this.cheststack[i1] = null;
				this.onInventoryChanged();
				return itemStack3;
			} else {
				itemStack3 = this.cheststack[i1].splitStack(i2);
				if(this.cheststack[i1].stackSize == 0) {
					this.cheststack[i1] = null;
				}

				this.onInventoryChanged();
				return itemStack3;
			}
		} else {
			return null;
		}
	}

	public void setInventorySlotContents(int i1, ItemStack itemStack2) {
		this.cheststack[i1] = itemStack2;
		if(itemStack2 != null && itemStack2.stackSize > this.getInventoryStackLimit()) {
			itemStack2.stackSize = this.getInventoryStackLimit();
		}

		this.onInventoryChanged();
	}

	public String getInvName() {
		return this.localstring;
	}

	public int getInventoryStackLimit() {
		return 64;
	}

	public void onInventoryChanged() {
	}

	public boolean canInteractWith(EntityPlayer entityPlayer1) {
		return true;
	}
}
