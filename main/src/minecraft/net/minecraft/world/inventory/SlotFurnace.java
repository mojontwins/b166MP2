package net.minecraft.world.inventory;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class SlotFurnace extends Slot {
	private EntityPlayer thePlayer;
	private int field_48437_f;

	public SlotFurnace(EntityPlayer entityPlayer1, IInventory iInventory2, int i3, int i4, int i5) {
		super(iInventory2, i3, i4, i5);
		this.thePlayer = entityPlayer1;
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return false;
	}

	public ItemStack decrStackSize(int i1) {
		if(this.getHasStack()) {
			this.field_48437_f += Math.min(i1, this.getStack().stackSize);
		}

		return super.decrStackSize(i1);
	}

	public void onPickupFromSlot(ItemStack itemStack1) {
		this.onCrafting(itemStack1);
		super.onPickupFromSlot(itemStack1);
	}

	protected void onCrafting(ItemStack itemStack1, int i2) {
		this.field_48437_f += i2;
		this.onCrafting(itemStack1);
	}

	protected void onCrafting(ItemStack itemStack1) {
		itemStack1.onCrafting(this.thePlayer.worldObj, this.thePlayer, this.field_48437_f);
		this.field_48437_f = 0;

	}
}
