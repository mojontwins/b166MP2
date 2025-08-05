package net.minecraft.world.item;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.creative.CreativeTabs;
import net.minecraft.world.level.tile.Block;

public class ItemShears extends Item {
	public ItemShears(int i1) {
		super(i1);
		this.setMaxStackSize(1);
		this.setMaxDamage(238);
		
		this.displayOnCreativeTab = CreativeTabs.tabTools;
	}

	public boolean onBlockDestroyed(ItemStack theStack, int blockID, int x, int y, int z, EntityLiving theLiving) {
		if(blockID != Block.leaves.blockID && 
				blockID != Block.web.blockID && 
				blockID != Block.tallGrass.blockID && 
				blockID != Block.vine.blockID /*&& 
				blockID != Block.tripWire.blockID*/
		) {
			return super.onBlockDestroyed(theStack, blockID, x, y, z, theLiving);
		} else {
			theStack.damageItem(1, theLiving);
			return true;
		}
	}

	public boolean canHarvestBlock(Block block) {
		return block.blockID == Block.web.blockID ||
				block.blockID == Block.leaves.blockID ||
				block.blockID == Block.tallGrass.blockID ||
				block.blockID == Block.vine.blockID;
	}

	public float getStrVsBlock(ItemStack theStack, Block block) {
		return block.blockID != Block.web.blockID && block.blockID != Block.leaves.blockID ? (block.blockID == Block.cloth.blockID ? 5.0F : super.getStrVsBlock(theStack, block)) : 15.0F;
	}
}
