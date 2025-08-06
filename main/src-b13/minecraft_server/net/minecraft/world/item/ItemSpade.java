package net.minecraft.world.item;

import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.level.tile.Block;

public class ItemSpade extends ItemTool {
	private static Block[] blocksEffectiveAgainst = new Block[]{
			Block.grass, 
			Block.dirt, 
			Block.sand, 
			Block.gravel, 
			Block.snow, 
			Block.blockSnow, 
			Block.blockClay, 
			Block.tilledField
		};

	public ItemSpade(int i1, EnumToolMaterial enumToolMaterial2) {
		super(i1, 1, enumToolMaterial2, blocksEffectiveAgainst);
	}

	public boolean canHarvestBlock(Block block) {
		return block == Block.snow || block == Block.blockSnow || block == Block.grass;
	}
}
