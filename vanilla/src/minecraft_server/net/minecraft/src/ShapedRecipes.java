package net.minecraft.src;

public class ShapedRecipes implements IRecipe {
	private int field_21140_b;
	private int field_21144_c;
	private ItemStack[] field_21143_d;
	private ItemStack field_21142_e;
	public final int field_21141_a;

	public ShapedRecipes(int i1, int i2, ItemStack[] itemStack3, ItemStack itemStack4) {
		this.field_21141_a = itemStack4.itemID;
		this.field_21140_b = i1;
		this.field_21144_c = i2;
		this.field_21143_d = itemStack3;
		this.field_21142_e = itemStack4;
	}

	public ItemStack func_25077_b() {
		return this.field_21142_e;
	}

	public boolean func_21134_a(InventoryCrafting inventoryCrafting1) {
		for(int i2 = 0; i2 <= 3 - this.field_21140_b; ++i2) {
			for(int i3 = 0; i3 <= 3 - this.field_21144_c; ++i3) {
				if(this.func_21139_a(inventoryCrafting1, i2, i3, true)) {
					return true;
				}

				if(this.func_21139_a(inventoryCrafting1, i2, i3, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean func_21139_a(InventoryCrafting inventoryCrafting1, int i2, int i3, boolean z4) {
		for(int i5 = 0; i5 < 3; ++i5) {
			for(int i6 = 0; i6 < 3; ++i6) {
				int i7 = i5 - i2;
				int i8 = i6 - i3;
				ItemStack itemStack9 = null;
				if(i7 >= 0 && i8 >= 0 && i7 < this.field_21140_b && i8 < this.field_21144_c) {
					if(z4) {
						itemStack9 = this.field_21143_d[this.field_21140_b - i7 - 1 + i8 * this.field_21140_b];
					} else {
						itemStack9 = this.field_21143_d[i7 + i8 * this.field_21140_b];
					}
				}

				ItemStack itemStack10 = inventoryCrafting1.func_21084_a(i5, i6);
				if(itemStack10 != null || itemStack9 != null) {
					if(itemStack10 == null && itemStack9 != null || itemStack10 != null && itemStack9 == null) {
						return false;
					}

					if(itemStack9.itemID != itemStack10.itemID) {
						return false;
					}

					if(itemStack9.getItemDamage() != -1 && itemStack9.getItemDamage() != itemStack10.getItemDamage()) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public ItemStack func_21136_b(InventoryCrafting inventoryCrafting1) {
		return new ItemStack(this.field_21142_e.itemID, this.field_21142_e.stackSize, this.field_21142_e.getItemDamage());
	}

	public int getRecipeSize() {
		return this.field_21140_b * this.field_21144_c;
	}
}
