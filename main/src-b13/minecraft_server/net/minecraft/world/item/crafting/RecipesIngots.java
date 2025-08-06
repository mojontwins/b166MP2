package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

public class RecipesIngots {
	private Object[][] recipeItems = new Object[][]{
		{Block.blockGold, new ItemStack(Item.ingotGold, 9)}, 
		{Block.blockSteel, new ItemStack(Item.ingotIron, 9)}, 
		{Block.blockDiamond, new ItemStack(Item.diamond, 9)}, 
		{Block.blockLapis, new ItemStack(Item.dyePowder, 9, 4)},
	};

	public void addRecipes(CraftingManager craftingManager1) {
		for(int i2 = 0; i2 < this.recipeItems.length; ++i2) {
			Block block3 = (Block)this.recipeItems[i2][0];
			ItemStack itemStack4 = (ItemStack)this.recipeItems[i2][1];
			craftingManager1.addRecipe(new ItemStack(block3), new Object[]{"###", "###", "###", '#', itemStack4});
			craftingManager1.addRecipe(itemStack4, new Object[]{"#", '#', block3});
		}

		craftingManager1.addRecipe(new ItemStack(Item.ingotGold), new Object[]{"###", "###", "###", '#', Item.goldNugget});
		craftingManager1.addRecipe(new ItemStack(Item.goldNugget, 9), new Object[]{"#", '#', Item.ingotGold});
		
	}
}
