package net.minecraft.world.item;

import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.IDyeableBlock;

public class ItemDyeable extends ItemBlock {
	private final IDyeableBlock blockRef = (IDyeableBlock) Block.blocksList[this.getBlockID()];
	
	public ItemDyeable(int itemID) {
		super(itemID);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getIconFromDamage(int damage) {
		return Block.cloth.getBlockTextureFromSideAndMetadata(2, this.blockRef.getMetadataFromDye(damage));
	}

	public int getMetadata(int meta) {
		return meta;
	}

	public String getItemNameIS(ItemStack theStack) {
		return super.getItemName() + "." + ItemDye.dyeColorNames[this.blockRef.getMetadataFromDye(theStack.getItemDamage())];
	}
}
