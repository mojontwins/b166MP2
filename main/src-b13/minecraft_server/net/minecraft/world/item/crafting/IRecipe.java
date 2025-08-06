package net.minecraft.world.item.crafting;

import net.minecraft.world.inventory.InventoryCrafting;
import net.minecraft.world.item.ItemStack;

public interface IRecipe {
	boolean matches(InventoryCrafting inventoryCrafting1);

	ItemStack getCraftingResult(InventoryCrafting inventoryCrafting1);

	int getRecipeSize();

	ItemStack getRecipeOutput();
	
	public String getSimplifiedString();
}
