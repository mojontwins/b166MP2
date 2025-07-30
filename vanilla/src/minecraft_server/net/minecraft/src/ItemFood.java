package net.minecraft.src;

public class ItemFood extends Item {
	private int healAmount;
	private boolean field_25011_bi;

	public ItemFood(int i1, int i2, boolean z3) {
		super(i1);
		this.healAmount = i2;
		this.field_25011_bi = z3;
		this.maxStackSize = 1;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		--itemStack1.stackSize;
		entityPlayer3.heal(this.healAmount);
		return itemStack1;
	}

	public int getHealAmount() {
		return this.healAmount;
	}

	public boolean func_25010_k() {
		return this.field_25011_bi;
	}
}
