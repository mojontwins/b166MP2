package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingManager;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockWorkbench;

public class ContainerWorkbench extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory craftResult = new InventoryCraftResult();
	private World worldObj;
	private int posX;
	private int posY;
	private int posZ;

	public ContainerWorkbench(InventoryPlayer inventoryPlayer1, World world2, int i3, int i4, int i5) {
		this.worldObj = world2;
		this.posX = i3;
		this.posY = i4;
		this.posZ = i5;
		this.addSlot(new SlotCrafting(inventoryPlayer1.player, this.craftMatrix, this.craftResult, 0, 124, 35));

		int i6;
		int i7;
		for(i6 = 0; i6 < 3; ++i6) {
			for(i7 = 0; i7 < 3; ++i7) {
				this.addSlot(new Slot(this.craftMatrix, i7 + i6 * 3, 30 + i7 * 18, 17 + i6 * 18));
			}
		}

		for(i6 = 0; i6 < 3; ++i6) {
			for(i7 = 0; i7 < 9; ++i7) {
				this.addSlot(new Slot(inventoryPlayer1, i7 + i6 * 9 + 9, 8 + i7 * 18, 84 + i6 * 18));
			}
		}

		for(i6 = 0; i6 < 9; ++i6) {
			this.addSlot(new Slot(inventoryPlayer1, i6, 8 + i6 * 18, 142));
		}

		this.onCraftMatrixChanged(this.craftMatrix);
	}

	public void onCraftMatrixChanged(IInventory iInventory1) {
		this.craftResult.setInventorySlotContents(0, CraftingManager.getInstance().findMatchingRecipe(this.craftMatrix));
	}

	public void onCraftGuiClosed(EntityPlayer entityPlayer1) {
		super.onCraftGuiClosed(entityPlayer1);
		if(!this.worldObj.isRemote) {
			for(int i2 = 0; i2 < 9; ++i2) {
				ItemStack itemStack3 = this.craftMatrix.getStackInSlotOnClosing(i2);
				if(itemStack3 != null) {
					entityPlayer1.dropPlayerItem(itemStack3);
				}
			}

		}
	}

	public boolean canInteractWith(EntityPlayer entityPlayer1) {
		Block block = Block.blocksList[this.worldObj.getBlockId(this.posX, this.posY, this.posZ)];
		if (block == null || !(block instanceof BlockWorkbench)) return false;
		return entityPlayer1.getDistanceSq((double)this.posX + 0.5D, (double)this.posY + 0.5D, (double)this.posZ + 0.5D) <= 64.0D;
	}

	public ItemStack transferStackInSlot(int i1) {
		ItemStack itemStack2 = null;
		Slot slot3 = (Slot)this.inventorySlots.get(i1);
		if(slot3 != null && slot3.getHasStack()) {
			ItemStack itemStack4 = slot3.getStack();
			itemStack2 = itemStack4.copy();
			if(i1 == 0) {
				if(!this.mergeItemStack(itemStack4, 10, 46, true)) {
					return null;
				}

				slot3.onSlotChange(itemStack4, itemStack2);
			} else if(i1 >= 10 && i1 < 37) {
				if(!this.mergeItemStack(itemStack4, 37, 46, false)) {
					return null;
				}
			} else if(i1 >= 37 && i1 < 46) {
				if(!this.mergeItemStack(itemStack4, 10, 37, false)) {
					return null;
				}
			} else if(!this.mergeItemStack(itemStack4, 10, 46, false)) {
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
