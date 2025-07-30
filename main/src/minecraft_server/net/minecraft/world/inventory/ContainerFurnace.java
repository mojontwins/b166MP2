package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.FurnaceRecipes;
import net.minecraft.world.level.tile.entity.TileEntityFurnace;

public class ContainerFurnace extends Container {
	private TileEntityFurnace furnace;
	private int lastCookTime = 0;
	private int lastBurnTime = 0;
	private int lastItemBurnTime = 0;

	public ContainerFurnace(InventoryPlayer inventoryPlayer1, TileEntityFurnace tileEntityFurnace2) {
		this.furnace = tileEntityFurnace2;
		this.addSlot(new Slot(tileEntityFurnace2, 0, 56, 17));
		this.addSlot(new Slot(tileEntityFurnace2, 1, 56, 53));
		this.addSlot(new SlotFurnace(inventoryPlayer1.player, tileEntityFurnace2, 2, 116, 35));

		int i3;
		for(i3 = 0; i3 < 3; ++i3) {
			for(int i4 = 0; i4 < 9; ++i4) {
				this.addSlot(new Slot(inventoryPlayer1, i4 + i3 * 9 + 9, 8 + i4 * 18, 84 + i3 * 18));
			}
		}

		for(i3 = 0; i3 < 9; ++i3) {
			this.addSlot(new Slot(inventoryPlayer1, i3, 8 + i3 * 18, 142));
		}

	}

	public void onCraftGuiOpened(ICrafting iCrafting1) {
		super.onCraftGuiOpened(iCrafting1);
		iCrafting1.updateCraftingInventoryInfo(this, 0, this.furnace.furnaceCookTime);
		iCrafting1.updateCraftingInventoryInfo(this, 1, this.furnace.furnaceBurnTime);
		iCrafting1.updateCraftingInventoryInfo(this, 2, this.furnace.currentItemBurnTime);
	}

	public void updateCraftingResults() {
		super.updateCraftingResults();

		for(int i1 = 0; i1 < this.crafters.size(); ++i1) {
			ICrafting iCrafting2 = (ICrafting)this.crafters.get(i1);
			if(this.lastCookTime != this.furnace.furnaceCookTime) {
				iCrafting2.updateCraftingInventoryInfo(this, 0, this.furnace.furnaceCookTime);
			}

			if(this.lastBurnTime != this.furnace.furnaceBurnTime) {
				iCrafting2.updateCraftingInventoryInfo(this, 1, this.furnace.furnaceBurnTime);
			}

			if(this.lastItemBurnTime != this.furnace.currentItemBurnTime) {
				iCrafting2.updateCraftingInventoryInfo(this, 2, this.furnace.currentItemBurnTime);
			}
		}

		this.lastCookTime = this.furnace.furnaceCookTime;
		this.lastBurnTime = this.furnace.furnaceBurnTime;
		this.lastItemBurnTime = this.furnace.currentItemBurnTime;
	}

	public void updateProgressBar(int i1, int i2) {
		if(i1 == 0) {
			this.furnace.furnaceCookTime = i2;
		}

		if(i1 == 1) {
			this.furnace.furnaceBurnTime = i2;
		}

		if(i1 == 2) {
			this.furnace.currentItemBurnTime = i2;
		}

	}

	public boolean canInteractWith(EntityPlayer entityPlayer1) {
		return this.furnace.isUseableByPlayer(entityPlayer1);
	}

	public ItemStack transferStackInSlot(int i1) {
		ItemStack itemStack2 = null;
		Slot slot3 = (Slot)this.inventorySlots.get(i1);
		if(slot3 != null && slot3.getHasStack()) {
			ItemStack itemStack4 = slot3.getStack();
			itemStack2 = itemStack4.copy();
			if(i1 == 2) {
				if(!this.mergeItemStack(itemStack4, 3, 39, true)) {
					return null;
				}

				slot3.onSlotChange(itemStack4, itemStack2);
			} else if(i1 != 1 && i1 != 0) {
				if(FurnaceRecipes.smelting().getSmeltingResult(itemStack4.getItem().shiftedIndex) != null) {
					if(!this.mergeItemStack(itemStack4, 0, 1, false)) {
						return null;
					}
				} else if(TileEntityFurnace.isItemFuel(itemStack4)) {
					if(!this.mergeItemStack(itemStack4, 1, 2, false)) {
						return null;
					}
				} else if(i1 >= 3 && i1 < 30) {
					if(!this.mergeItemStack(itemStack4, 30, 39, false)) {
						return null;
					}
				} else if(i1 >= 30 && i1 < 39 && !this.mergeItemStack(itemStack4, 3, 30, false)) {
					return null;
				}
			} else if(!this.mergeItemStack(itemStack4, 3, 39, false)) {
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
