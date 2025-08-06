package net.minecraft.world.item.crafting;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

public class RecipesCrafting {
	public void addRecipes(CraftingManager craftingManager1) {
		craftingManager1.addRecipe(new ItemStack(Block.chest), new Object[]{"###", "# #", "###", '#', Block.planks});
		craftingManager1.addRecipe(new ItemStack(Block.stoneOvenIdle), new Object[]{"###", "# #", "###", '#', Block.cobblestone});
		craftingManager1.addRecipe(new ItemStack(Block.workbench), new Object[]{"##", "##", '#', Block.planks});
		
		craftingManager1.addRecipe(new ItemStack(Block.sandStone), new Object[]{"##", "##", '#', Block.sand});
		craftingManager1.addRecipe(new ItemStack(Block.sandStone, 4, 2), new Object[]{"##", "##", '#', Block.sandStone});
		craftingManager1.addRecipe(new ItemStack(Block.sandStone, 1, 1), new Object[]{"#", "#", '#', new ItemStack(Block.stairSingle, 1, 1)});

		craftingManager1.addRecipe(new ItemStack(Block.fenceIron, 16), new Object[]{"###", "###", '#', Item.ingotIron});
		craftingManager1.addRecipe(new ItemStack(Block.thinGlass, 16), new Object[]{"###", "###", '#', Block.glass});
		
		//craftingManager1.addRecipe(new ItemStack(Block.redstoneLampIdle, 1), new Object[]{" R ", "RGR", " R ", 'R', Item.redstone, 'G', Block.glowStone});
	}
}
