package net.minecraft.world.level.creative;

import net.minecraft.world.item.Item;

final class CreativeTabSearch extends CreativeTabs {
	CreativeTabSearch(int par1, String par2Str) {
		super(par1, par2Str);
	}

	/**
	 * the itemID for the item to be displayed on the tab
	 */
	public int getTabIconItemIndex() {
		return Item.compass.shiftedIndex;
	}
}
