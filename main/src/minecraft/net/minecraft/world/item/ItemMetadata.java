package net.minecraft.world.item;

import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.IGetNameBasedOnMeta;

public class ItemMetadata extends ItemBlock {
	private Block blockObj;
	private int defaultSideForIcon;

	public ItemMetadata(int itemID, Block blockObj, int defaultSideForIcon) {
		super(itemID);
		this.blockObj = blockObj;
		this.defaultSideForIcon = defaultSideForIcon;
		
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}
	
	public ItemMetadata(int itemID, Block blockObj) {
		this(itemID, blockObj, 2);
	}
	
	@Override
	public int getIconFromDamage(int i1) {
		return this.blockObj.getBlockTextureFromSideAndMetadata(this.defaultSideForIcon, i1);
	}

	@Override
	public int getMetadata(int i1) {
		return i1;
	}
	
	@Override
	public String getItemNameIS(ItemStack stack) {
		if(this.blockObj instanceof IGetNameBasedOnMeta) {
			return ((IGetNameBasedOnMeta) blockObj).getName(stack.itemDamage);
		} else {
			return super.getItemNameIS(stack);
		}
	}
}
