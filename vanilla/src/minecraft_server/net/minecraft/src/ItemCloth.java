package net.minecraft.src;

public class ItemCloth extends ItemBlock {
	public ItemCloth(int i1) {
		super(i1);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	public int getMetadata(int i1) {
		return i1;
	}
}
