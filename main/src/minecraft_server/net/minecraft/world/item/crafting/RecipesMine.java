package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

public class RecipesMine {
	public void addRecipes(CraftingManager cm) {
		cm.addRecipe(new ItemStack(Block.blockCoal, 1), new Object[] {"###", "###", "###", '#', Item.coal});
		cm.addRecipe(new ItemStack(Item.coal, 9), new Object[] {"#", '#', Block.blockCoal});
		
		cm.addRecipe(new ItemStack(Block.classicPistonBase, 1), new Object[]{" X ", "X#X", "XXX", 'X', Item.ingotIron, '#', Item.redstone});
		cm.addRecipe(new ItemStack(Block.classicStickyPistonBase, 1), new Object[]{" X ", "X#X", "XXX", 'X', Item.ingotGold, '#', Item.redstone});
		
		cm.addShapelessRecipe(new ItemStack(Item.recipeBook, 1), new Object[]{Item.book, new ItemStack(Item.dyePowder, 1, 0)});
		
	}
}
	
