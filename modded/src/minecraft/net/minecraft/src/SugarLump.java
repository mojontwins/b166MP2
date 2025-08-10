package net.minecraft.src;

public class SugarLump extends Item {
	private int a;

	public SugarLump(int i1) {
		super(i1);
		this.maxStackSize = 32;
		this.a = 3;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		--itemStack1.stackSize;
		entityPlayer3.heal(this.a);
		return itemStack1;
	}
}
