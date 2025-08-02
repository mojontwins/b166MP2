package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.entity.TileEntityDispenser;

public class ContainerDispenser extends Container {
	private TileEntityDispenser tileEntityDispenser;

	public ContainerDispenser(IInventory iInventory1, TileEntityDispenser tileEntityDispenser2) {
		this.tileEntityDispenser = tileEntityDispenser2;

		int i3;
		int i4;
		for(i3 = 0; i3 < 3; ++i3) {
			for(i4 = 0; i4 < 3; ++i4) {
				this.addSlot(new Slot(tileEntityDispenser2, i4 + i3 * 3, 62 + i4 * 18, 17 + i3 * 18));
			}
		}

		for(i3 = 0; i3 < 3; ++i3) {
			for(i4 = 0; i4 < 9; ++i4) {
				this.addSlot(new Slot(iInventory1, i4 + i3 * 9 + 9, 8 + i4 * 18, 84 + i3 * 18));
			}
		}

		for(i3 = 0; i3 < 9; ++i3) {
			this.addSlot(new Slot(iInventory1, i3, 8 + i3 * 18, 142));
		}

	}

	public boolean canInteractWith(EntityPlayer entityPlayer1) {
		return this.tileEntityDispenser.isUseableByPlayer(entityPlayer1);
	}

	public ItemStack transferStackInSlot(int i1) {
		ItemStack itemStack2 = null;
		Slot slot3 = (Slot)this.inventorySlots.get(i1);
		if(slot3 != null && slot3.getHasStack()) {
			ItemStack itemStack4 = slot3.getStack();
			itemStack2 = itemStack4.copy();
			if(i1 < 9) {
				if(!this.mergeItemStack(itemStack4, 9, 45, true)) {
					return null;
				}
			} else if(!this.mergeItemStack(itemStack4, 0, 9, false)) {
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
}
