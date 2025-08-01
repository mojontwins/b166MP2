package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingManager;

public class ContainerPlayer extends Container {
	public InventoryCrafting craftMatrix;
	public IInventory craftResult;
	public boolean isLocalWorld;

	public ContainerPlayer(InventoryPlayer inventoryPlayer1) {
		this(inventoryPlayer1, true);
	}

	public ContainerPlayer(InventoryPlayer inventoryPlayer1, boolean z2) {
		this.craftMatrix = new InventoryCrafting(this, 2, 2);
		this.craftResult = new InventoryCraftResult();
		this.isLocalWorld = false;
		this.isLocalWorld = z2;
		this.addSlot(new SlotCrafting(inventoryPlayer1.player, this.craftMatrix, this.craftResult, 0, 144, 36));

		int i3;
		int i4;
		for(i3 = 0; i3 < 2; ++i3) {
			for(i4 = 0; i4 < 2; ++i4) {
				this.addSlot(new Slot(this.craftMatrix, i4 + i3 * 2, 88 + i4 * 18, 26 + i3 * 18));
			}
		}

		for(i3 = 0; i3 < 4; ++i3) {
			this.addSlot(new SlotArmor(this, inventoryPlayer1, inventoryPlayer1.getSizeInventory() - 1 - i3, 8, 8 + i3 * 18, i3));
		}

		for(i3 = 0; i3 < 3; ++i3) {
			for(i4 = 0; i4 < 9; ++i4) {
				this.addSlot(new Slot(inventoryPlayer1, i4 + (i3 + 1) * 9, 8 + i4 * 18, 84 + i3 * 18));
			}
		}

		for(i3 = 0; i3 < 9; ++i3) {
			this.addSlot(new Slot(inventoryPlayer1, i3, 8 + i3 * 18, 142));
		}

		this.onCraftMatrixChanged(this.craftMatrix);
	}

	public void onCraftMatrixChanged(IInventory iInventory1) {
		this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix));
	}

	public void onCraftGuiClosed(EntityPlayer entityPlayer1) {
		super.onCraftGuiClosed(entityPlayer1);

		for(int i2 = 0; i2 < 4; ++i2) {
			ItemStack itemStack3 = this.craftMatrix.getStackInSlotOnClosing(i2);
			if(itemStack3 != null) {
				entityPlayer1.dropPlayerItem(itemStack3);
			}
		}

		this.craftResult.setInventorySlotContents(0, (ItemStack)null);
	}

	public boolean canInteractWith(EntityPlayer entityPlayer1) {
		return true;
	}

	public ItemStack transferStackInSlot(int i1) {
		ItemStack itemStack2 = null;
		Slot slot3 = (Slot)this.inventorySlots.get(i1);
		if(slot3 != null && slot3.getHasStack()) {
			ItemStack itemStack4 = slot3.getStack();
			itemStack2 = itemStack4.copy();
			if(i1 == 0) {
				if(!this.mergeItemStack(itemStack4, 9, 45, true)) {
					return null;
				}

				slot3.onSlotChange(itemStack4, itemStack2);
			} else if(i1 >= 9 && i1 < 36) {
				if(!this.mergeItemStack(itemStack4, 36, 45, false)) {
					return null;
				}
			} else if(i1 >= 36 && i1 < 45) {
				if(!this.mergeItemStack(itemStack4, 9, 36, false)) {
					return null;
				}
			} else if(!this.mergeItemStack(itemStack4, 9, 45, false)) {
				return null;
			}

			if(itemStack4.stackSize == 0) {
				slot3.putStack((ItemStack)null);
			} else {
				slot3.onSlotChanged();
			}

			if(itemStack4.stackSize == itemStack2.stackSize) {
				return null;
			}

			slot3.onPickupFromSlot(itemStack4);
		}

		return itemStack2;
	}
	
	public boolean func_94530_a(ItemStack var1, Slot var2) {
		return var2.inventory != this.craftResult && super.func_94530_a(var1, var2);
	}
}
