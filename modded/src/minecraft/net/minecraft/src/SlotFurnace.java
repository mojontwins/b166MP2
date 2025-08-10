package net.minecraft.src;

public class SlotFurnace extends Slot {
	private EntityPlayer thePlayer;

	public SlotFurnace(EntityPlayer paramgq, IInventory paramls, int paramInt1, int paramInt2, int paramInt3) {
		super(paramls, paramInt1, paramInt2, paramInt3);
		this.thePlayer = paramgq;
	}

	public boolean isItemValid(ItemStack paramiw) {
		return false;
	}

	public void onPickupFromSlot(ItemStack paramiw) {
		paramiw.onCrafting(this.thePlayer.worldObj, this.thePlayer);
		if(paramiw.itemID == Item.ingotIron.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.acquireIron, 1);
		}

		if(paramiw.itemID == Item.fishCooked.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.cookFish, 1);
		}

		ModLoader.TakenFromFurnace(this.thePlayer, paramiw);
		super.onPickupFromSlot(paramiw);
	}
}
