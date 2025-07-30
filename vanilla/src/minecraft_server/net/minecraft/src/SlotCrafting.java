package net.minecraft.src;

public class SlotCrafting extends Slot {
	private final IInventory craftMatrix;
	private EntityPlayer field_25004_e;

	public SlotCrafting(EntityPlayer entityPlayer1, IInventory iInventory2, IInventory iInventory3, int i4, int i5, int i6) {
		super(iInventory3, i4, i5, i6);
		this.field_25004_e = entityPlayer1;
		this.craftMatrix = iInventory2;
	}

	public boolean isItemValid(ItemStack itemStack1) {
		return false;
	}

	public void onPickupFromSlot(ItemStack itemStack1) {
		itemStack1.func_28142_b(this.field_25004_e.worldObj, this.field_25004_e);
		if(itemStack1.itemID == Block.workbench.blockID) {
			this.field_25004_e.addStat(AchievementList.field_25130_d, 1);
		} else if(itemStack1.itemID == Item.pickaxeWood.shiftedIndex) {
			this.field_25004_e.addStat(AchievementList.field_27110_i, 1);
		} else if(itemStack1.itemID == Block.stoneOvenIdle.blockID) {
			this.field_25004_e.addStat(AchievementList.field_27109_j, 1);
		} else if(itemStack1.itemID == Item.hoeWood.shiftedIndex) {
			this.field_25004_e.addStat(AchievementList.field_27107_l, 1);
		} else if(itemStack1.itemID == Item.bread.shiftedIndex) {
			this.field_25004_e.addStat(AchievementList.field_27106_m, 1);
		} else if(itemStack1.itemID == Item.cake.shiftedIndex) {
			this.field_25004_e.addStat(AchievementList.field_27105_n, 1);
		} else if(itemStack1.itemID == Item.pickaxeStone.shiftedIndex) {
			this.field_25004_e.addStat(AchievementList.field_27104_o, 1);
		} else if(itemStack1.itemID == Item.swordWood.shiftedIndex) {
			this.field_25004_e.addStat(AchievementList.field_27101_r, 1);
		}

		for(int i2 = 0; i2 < this.craftMatrix.getSizeInventory(); ++i2) {
			ItemStack itemStack3 = this.craftMatrix.getStackInSlot(i2);
			if(itemStack3 != null) {
				this.craftMatrix.decrStackSize(i2, 1);
				if(itemStack3.getItem().hasContainerItem()) {
					this.craftMatrix.setInventorySlotContents(i2, new ItemStack(itemStack3.getItem().getContainerItem()));
				}
			}
		}

	}
}
