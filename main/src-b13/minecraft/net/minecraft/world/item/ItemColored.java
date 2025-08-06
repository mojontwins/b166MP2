package net.minecraft.world.item;

import net.minecraft.world.level.tile.Block;

public class ItemColored extends ItemBlock {
	private final Block blockRef = Block.blocksList[this.getBlockID()];
	private String[] blockNames;

	public ItemColored(int i1, boolean z2) {
		super(i1);
		if(z2) {
			this.setMaxDamage(0);
			this.setHasSubtypes(true);
		}

	}

	public int getColorFromDamage(int i1, int i2) {
		return this.blockRef.getRenderColor(i1);
	}

	public int getIconFromDamage(int i1) {
		return this.blockRef.getBlockTextureFromSideAndMetadata(0, i1);
	}

	public int getMetadata(int i1) {
		return i1;
	}

	public ItemColored setBlockNames(String[] string1) {
		this.blockNames = string1;
		return this;
	}

	public String getItemNameIS(ItemStack itemStack1) {
		if(this.blockNames == null) {
			return super.getItemNameIS(itemStack1);
		} else {
			int i2 = itemStack1.getItemDamage();
			return i2 >= 0 && i2 < this.blockNames.length ? super.getItemNameIS(itemStack1) + "." + this.blockNames[i2] : super.getItemNameIS(itemStack1);
		}
	}
}
