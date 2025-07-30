package net.minecraft.world.item.crafting;

import java.util.HashMap;

import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;

public class ShapedRecipes implements IRecipe {
	private int recipeWidth;
	private int recipeHeight;
	private ItemStack[] recipeItems;
	private ItemStack recipeOutput;
	public final int recipeOutputItemID;

	public ShapedRecipes(int w, int h, ItemStack[] inputStacks, ItemStack outputStack) {
		this.recipeOutputItemID = outputStack.itemID;
		this.recipeWidth = w;
		this.recipeHeight = h;
		this.recipeItems = inputStacks;
		this.recipeOutput = outputStack;
	}

	public ItemStack getRecipeOutput() {
		return this.recipeOutput;
	}

	public String toString() {
		String res = "R ";
		for(int i = 0; i < this.recipeItems.length; i ++) {
			ItemStack itemStack = this.recipeItems[i];
			res += itemStack == null ? "- " : itemStack.itemID + ":" + itemStack.itemDamage + " ";
		}
		return res;
	}
	
	public boolean matches(InventoryCrafting ic) {
		for(int w = 0; w <= 3 - this.recipeWidth; ++w) {
			for(int h = 0; h <= 3 - this.recipeHeight; ++h) {
				if(this.checkMatch(ic, w, h, true)) {
					return true;
				}

				if(this.checkMatch(ic, w, h, false)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean checkMatch(InventoryCrafting ic, int w, int h, boolean mirrored) {
		for(int x = 0; x < 3; ++x) {
			for(int y = 0; y < 3; ++y) {
				int xRe = x - w;
				int yRe = y - h;
				
				ItemStack recipeStack = null;
				if(xRe >= 0 && yRe >= 0 && xRe < this.recipeWidth && yRe < this.recipeHeight) {
					if(mirrored) {
						recipeStack = this.recipeItems[this.recipeWidth - xRe - 1 + yRe * this.recipeWidth];
					} else {
						recipeStack = this.recipeItems[xRe + yRe * this.recipeWidth];
					}
				}

				ItemStack craftingStack = ic.getStackInRowAndColumn(x, y);
				if(craftingStack != null || recipeStack != null) {
					if(craftingStack == null && recipeStack != null || craftingStack != null && recipeStack == null) {
						return false;
					}

					if(recipeStack.itemID != craftingStack.itemID) {
						return false;
					}

					if(recipeStack.getItemDamage() != -1 && recipeStack.getItemDamage() != craftingStack.getItemDamage()) {
						return false;
					}
				}
			}
		}
		
		return true;
	}

	public ItemStack getCraftingResult(InventoryCrafting ic) {
		return new ItemStack(this.recipeOutput.itemID, this.recipeOutput.stackSize, this.recipeOutput.getItemDamage());
	}

	public int getRecipeSize() {
		return this.recipeWidth * this.recipeHeight;
	}

	public ItemStack[] getRecipeItems() {
		return recipeItems;
	}

	public void setRecipeItems(ItemStack[] recipeItems) {
		this.recipeItems = recipeItems;
	}

	public int getRecipeWidth() {
		return recipeWidth;
	}

	public void setRecipeWidth(int recipeWidth) {
		this.recipeWidth = recipeWidth;
	}

	public int getRecipeHeight() {
		return recipeHeight;
	}

	public void setRecipeHeight(int recipeHeight) {
		this.recipeHeight = recipeHeight;
	}
	
	public String getSimplifiedString() {
		String result = "";
		HashMap<Integer,Integer> foundIDs = new HashMap<Integer,Integer>();
		int curEncoded = 0;
		
		for(int i = 0; i < this.recipeItems.length; i ++) {
			ItemStack itemStack = this.recipeItems[i];
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
