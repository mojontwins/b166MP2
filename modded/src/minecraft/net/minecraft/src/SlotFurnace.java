package net.minecraft.src;

public class SlotFurnace extends Slot {
	private EntityPlayer thePlayer;

	public SlotFurnace(EntityPlayer entityPlayer1, IInventory iInventory2, int i3, int i4, int i5) {
		super(iInventory2, i3, i4, i5);
		this.thePlayer = entityPlayer1;
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return false;
	}

	public void onPickupFromSlot(ItemStack itemStack1) {
		itemStack1.onCrafting(this.thePlayer.worldObj, this.thePlayer);
		if(itemStack1.itemID == Item.ingotIron.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.acquireIron, 1);
		}

		if(itemStack1.itemID == Item.fishCooked.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.cookFish, 1);
		}

		super.onPickupFromSlot(itemStack1);
	}
}
