package net.minecraft.src;

public class SlotFurnace extends Slot {
	private EntityPlayer field_27007_d;

	public SlotFurnace(EntityPlayer entityPlayer1, IInventory iInventory2, int i3, int i4, int i5) {
		super(iInventory2, i3, i4, i5);
		this.field_27007_d = entityPlayer1;
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return false;
	}

	public void onPickupFromSlot(ItemStack itemStack1) {
		itemStack1.func_28142_b(this.field_27007_d.worldObj, this.field_27007_d);
		if(itemStack1.itemID == Item.ingotIron.shiftedIndex) {
			this.field_27007_d.addStat(AchievementList.field_27108_k, 1);
		}

		if(itemStack1.itemID == Item.fishCooked.shiftedIndex) {
			this.field_27007_d.addStat(AchievementList.field_27103_p, 1);
		}

		super.onPickupFromSlot(itemStack1);
	}
}
