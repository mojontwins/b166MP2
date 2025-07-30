package net.minecraft.world.item;

import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class ItemAxe extends ItemTool {
	protected static Block[] blocksEffectiveAgainst = new Block[]{Block.planks, Block.bookShelf, Block.wood, Block.chest, Block.stairDouble, Block.stairSingle, Block.pumpkin, Block.pumpkinLantern};

	protected ItemAxe(int i1, EnumToolMaterial enumToolMaterial2) {
		super(i1, 3, enumToolMaterial2, blocksEffectiveAgainst);
	}
	
	protected ItemAxe(int i1, int damageModifier, EnumToolMaterial enumToolMaterial) {
		super(i1, damageModifier, enumToolMaterial, blocksEffectiveAgainst);
	}

	public float getStrVsBlock(ItemStack itemStack1, Block block2) {
		return block2 != null && block2.blockMaterial == Material.wood ? this.efficiencyOnProperMaterial : super.getStrVsBlock(itemStack1, block2);
	}
	
	@Override
	public boolean canHarvestBlock(Block block) {
		return block == Block.leaves;
	}
}
