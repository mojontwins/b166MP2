package net.minecraft.world.inventory;

import net.minecraft.world.item.ItemArmor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.tile.Block;

class SlotArmor extends Slot {
	final int armorType;
	final ContainerPlayer parent;

	SlotArmor(ContainerPlayer containerPlayer1, IInventory iInventory2, int i3, int i4, int i5, int i6) {
		super(iInventory2, i3, i4, i5);
		this.parent = containerPlayer1;
		this.armorType = i6;
	}

	public int getSlotStackLimit() {
		return 1;
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return itemStack1.getItem() instanceof ItemArmor ? 
				((ItemArmor)itemStack1.getItem()).armorType == this.armorType 
			: 
				(itemStack1.getItem().shiftedIndex == Block.pumpkin.blockID  ? 
						this.armorType == 0 
					: 
						false);
	}

	public int getBackgroundIconIndex() {
		return 15 + this.armorType * 16;
	}
}
