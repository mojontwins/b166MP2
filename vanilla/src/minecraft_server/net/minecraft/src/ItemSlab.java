package net.minecraft.src;

public class ItemSlab extends ItemBlock {
	public ItemSlab(int i1) {
		super(i1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getMetadata(int i1) {
		return i1;
	}
}
