package net.minecraft.src;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ShapelessRecipes implements IRecipe {
	private final ItemStack field_21138_a;
	private final List field_21137_b;

	public ShapelessRecipes(ItemStack itemStack1, List list2) {
		this.field_21138_a = itemStack1;
		this.field_21137_b = list2;
	}

	public ItemStack func_25077_b() {
		return this.field_21138_a;
	}

	public boolean func_21134_a(InventoryCrafting inventoryCrafting1) {
		ArrayList arrayList2 = new ArrayList(this.field_21137_b);

		for(int i3 = 0; i3 < 3; ++i3) {
			for(int i4 = 0; i4 < 3; ++i4) {
				ItemStack itemStack5 = inventoryCrafting1.func_21084_a(i4, i3);
				if(itemStack5 != null) {
					boolean z6 = false;
					Iterator iterator7 = arrayList2.iterator();

					while(iterator7.hasNext()) {
						ItemStack itemStack8 = (ItemStack)iterator7.next();
						if(itemStack5.itemID == itemStack8.itemID && (itemStack8.getItemDamage() == -1 || itemStack5.getItemDamage() == itemStack8.getItemDamage())) {
							z6 = true;
							arrayList2.remove(itemStack8);
							break;
						}
					}

					if(!z6) {
						return false;
					}
				}
			}
		}

		return arrayList2.isEmpty();
	}

	public ItemStack func_21136_b(InventoryCrafting inventoryCrafting1) {
		return this.field_21138_a.copy();
	}

	public int getRecipeSize() {
		return this.field_21137_b.size();
	}
}
