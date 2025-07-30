package com.risugami.recipebook;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.inventory.Container;
import net.minecraft.world.inventory.IInventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CraftingInventoryRecipeBookCB extends Container {
	private IInventory inv;

	public CraftingInventoryRecipeBookCB(IInventory inventory) {
		this.inv = inventory;
		this.addSlot(new Slot(inventory, -1, 117 * GuiRecipeBook.COLUMNS + 8, 4));
		int slot = 0;

		for(int r = 0; r < GuiRecipeBook.ROWS; ++r) {
			for(int c = 0; c < GuiRecipeBook.COLUMNS; ++c) {
				this.addSlot(new Slot(inventory, slot++, 99 + c * 117, 24 + r * 55));

				for(int y = 0; y < 3; ++y) {
					for(int x = 0; x < 3; ++x) {
						this.addSlot(new Slot(inventory, slot++, 5 + x * 18 + c * 117, 6 + y * 18 + r * 55));
					}
				}
			}
		}

	}

	public boolean canInteractWith(EntityPlayer player) {
		return this.inv.isUseableByPlayer(player);
	}

	public ItemStack transferStackInSlot(int paramInt) {
		return null;
	}
}
