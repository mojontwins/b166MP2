package net.minecraft.src;

class SlotArmor extends Slot {
	final int field_20102_a;
	final ContainerPlayer field_20101_b;

	SlotArmor(ContainerPlayer containerPlayer1, IInventory iInventory2, int i3, int i4, int i5, int i6) {
		super(iInventory2, i3, i4, i5);
		this.field_20101_b = containerPlayer1;
		this.field_20102_a = i6;
	}

	public int getSlotStackLimit() {
		return 1;
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return itemStack1.getItem() instanceof ItemArmor ? ((ItemArmor)itemStack1.getItem()).armorType == this.field_20102_a : (itemStack1.getItem().shiftedIndex == Block.pumpkin.blockID ? this.field_20102_a == 0 : false);
	}
}
