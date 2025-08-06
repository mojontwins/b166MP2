package net.minecraft.world.item;

import net.minecraft.world.entity.item.EnumToolMaterial;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

public class ItemPickaxe extends ItemTool {
	private static Block[] blocksEffectiveAgainst = new Block[]{
			Block.cobblestone, 
			Block.stairDouble,
			Block.stairSingle, 
			Block.stone, 
			Block.sandStone,
			Block.cobblestoneMossy, 
			Block.oreIron,
			Block.blockSteel, 
			Block.oreCoal, 
			Block.blockGold, 
			Block.oreGold,
			Block.oreDiamond, 
			Block.blockDiamond, 
			Block.ice, 
			Block.oreLapis,
			Block.blockLapis, 
			Block.oreRedstone, 
			Block.oreRedstoneGlowing, 
			Block.rail, 
			Block.railPowered,
			Block.railDetector
		};

	protected ItemPickaxe(int i1, EnumToolMaterial enumToolMaterial2) {
		super(i1, 2, enumToolMaterial2, blocksEffectiveAgainst);
	}
	
	protected ItemPickaxe(int i1, int damageModifier, EnumToolMaterial enumToolMaterial2) {
		super(i1, damageModifier, enumToolMaterial2, blocksEffectiveAgainst);
	}

	public boolean canHarvestBlock(Block block) {
		if(block == Block.glass) return true;
		
		return block == Block.obsidian ? 
				this.toolMaterial.getHarvestLevel() == 3 
			: 
				(block != Block.blockDiamond && block != Block.oreDiamond ? 
						(block != Block.blockGold && block != Block.oreGold ? 
								(block != Block.oreCoal  && block != Block.blockSteel && block != Block.oreIron  ? 
										(block != Block.blockLapis && block != Block.oreLapis  ? 
												(block != Block.oreRedstone && block != Block.oreRedstoneGlowing  ? 
														(block.blockMaterial == Material.rock ? 
																true 
															: 
																block.blockMaterial == Material.iron) 
													:
														this.toolMaterial.getHarvestLevel() >= 2) 
											:
												this.toolMaterial.getHarvestLevel() >= 1) 
									: 
										this.toolMaterial.getHarvestLevel() >= 1) 
							: 
								this.toolMaterial.getHarvestLevel() >= 2) 
					: 
				this.toolMaterial.getHarvestLevel() >= 2);
	}

	public float getStrVsBlock(ItemStack itemStack1, Block block2) {
		return block2 == null || block2.blockMaterial != Material.iron && block2.blockMaterial != Material.rock ? super.getStrVsBlock(itemStack1, block2) : this.efficiencyOnProperMaterial;
	}
}
