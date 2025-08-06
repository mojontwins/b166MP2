package net.minecraft.world.item;

import net.minecraft.world.level.tile.Block;

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

	public int getIconFromDamage(int i1) {
		return this.blockObj.getBlockTextureFromSideAndMetadata(this.defaultSideForIcon, i1);
	}

	public int getMetadata(int i1) {
		return i1;
	}
}
