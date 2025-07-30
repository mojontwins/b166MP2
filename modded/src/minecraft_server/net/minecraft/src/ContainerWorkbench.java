package net.minecraft.src;

public class ContainerWorkbench extends Container {
	public InventoryCrafting craftMatrix = new InventoryCrafting(this, 3, 3);
	public IInventory craftResult = new InventoryCraftResult();
	private World field_20150_c;
	private int field_20149_h;
	private int field_20148_i;
	private int field_20147_j;

	public ContainerWorkbench(InventoryPlayer inventoryPlayer1, World world2, int i3, int i4, int i5) {
		this.field_20150_c = world2;
		this.field_20149_h = i3;
		this.field_20148_i = i4;
		this.field_20147_j = i5;
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

		for(int i2 = 0; i2 < 9; ++i2) {
			ItemStack itemStack3 = this.craftMatrix.getStackInSlot(i2);
			if(itemStack3 != null) {
				entityPlayer1.dropPlayerItem(itemStack3);
			}
		}

	}

	public boolean canInteractWith(EntityPlayer entityPlayer1) {
		return this.field_20150_c.getBlockId(this.field_20149_h, this.field_20148_i, this.field_20147_j) != Block.workbench.blockID ? false : entityPlayer1.getDistanceSq((double)this.field_20149_h + 0.5D, (double)this.field_20148_i + 0.5D, (double)this.field_20147_j + 0.5D) <= 64.0D;
	}

	public ItemStack func_27086_a(int i1) {
		ItemStack itemStack2 = null;
		Slot slot3 = (Slot)this.inventorySlots.get(i1);
		if(slot3 != null && slot3.func_27006_b()) {
			ItemStack itemStack4 = slot3.getStack();
			itemStack2 = itemStack4.copy();
			if(i1 == 0) {
				this.func_28126_a(itemStack4, 10, 46, true);
			} else if(i1 >= 10 && i1 < 37) {
				this.func_28126_a(itemStack4, 37, 46, false);
			} else if(i1 >= 37 && i1 < 46) {
				this.func_28126_a(itemStack4, 10, 37, false);
			} else {
				this.func_28126_a(itemStack4, 10, 46, false);
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
