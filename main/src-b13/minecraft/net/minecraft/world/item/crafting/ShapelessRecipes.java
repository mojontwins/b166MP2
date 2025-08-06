package net.minecraft.world.item.crafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;

public class ShapelessRecipes implements IRecipe {
	private final ItemStack recipeOutput;
	private final List<ItemStack> recipeItems;

	public List<ItemStack> getRecipeItems() {
		return recipeItems;
	}

	public ShapelessRecipes(ItemStack itemStack1, List<ItemStack> list2) {
		this.recipeOutput = itemStack1;
		this.recipeItems = list2;
	}

	public ItemStack getRecipeOutput() {
		return this.recipeOutput;
	}

	public boolean matches(InventoryCrafting inventoryCrafting1) {
		ArrayList<ItemStack> arrayList2 = new ArrayList<ItemStack>(this.recipeItems);

		for(int i3 = 0; i3 < 3; ++i3) {
			for(int i4 = 0; i4 < 3; ++i4) {
				ItemStack itemStack5 = inventoryCrafting1.getStackInRowAndColumn(i4, i3);
				if(itemStack5 != null) {
					boolean z6 = false;
					Iterator<ItemStack> iterator7 = arrayList2.iterator();

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

	public ItemStack getCraftingResult(InventoryCrafting inventoryCrafting1) {
		return this.recipeOutput.copy();
	}

	public int getRecipeSize() {
		return this.recipeItems.size();
	}
	
	public String getSimplifiedString() {
		String result = "";
		HashMap<Integer,Integer> foundIDs = new HashMap<Integer,Integer>();
		int curEncoded = 0;
		
		Iterator<ItemStack> it = this.recipeItems.iterator();
		while(it.hasNext()) {
			ItemStack itemStack = it.next();
			if(itemStack != null) {
				int id = itemStack.getItem().shiftedIndex;
				int encoded;
				if(foundIDs.get(id) != null) {
					encoded = foundIDs.get(id);
				} else {
					encoded = curEncoded ++;
					foundIDs.put(id, encoded);
				}
				result += "" + encoded;
			} else result += " ";
		}
		
		return result;
	}
}
