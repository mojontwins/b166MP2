package net.minecraft.src;

public class SlotCrafting extends Slot {
	private final IInventory craftMatrix;
	private EntityPlayer thePlayer;

	public SlotCrafting(EntityPlayer paramgq, IInventory paramls1, IInventory paramls2, int paramInt1, int paramInt2, int paramInt3) {
		super(paramls2, paramInt1, paramInt2, paramInt3);
		this.thePlayer = paramgq;
		this.craftMatrix = paramls1;
	}

	public boolean isItemValid(ItemStack paramiw) {
		return false;
	}

	public void onPickupFromSlot(ItemStack paramiw) {
		paramiw.onCrafting(this.thePlayer.worldObj, this.thePlayer);
		if(paramiw.itemID == Block.workbench.blockID) {
			this.thePlayer.addStat(AchievementList.buildWorkBench, 1);
		} else if(paramiw.itemID == Item.pickaxeWood.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.buildPickaxe, 1);
		} else if(paramiw.itemID == Block.stoneOvenIdle.blockID) {
			this.thePlayer.addStat(AchievementList.buildFurnace, 1);
		} else if(paramiw.itemID == Item.hoeWood.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.buildHoe, 1);
		} else if(paramiw.itemID == Item.bread.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.makeBread, 1);
		} else if(paramiw.itemID == Item.cake.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.bakeCake, 1);
		} else if(paramiw.itemID == Item.pickaxeStone.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.buildBetterPickaxe, 1);
		} else if(paramiw.itemID == Item.swordWood.shiftedIndex) {
			this.thePlayer.addStat(AchievementList.buildSword, 1);
		}

		ModLoader.TakenFromCrafting(this.thePlayer, paramiw);

		for(int i = 0; i < this.craftMatrix.getSizeInventory(); ++i) {
			ItemStack localiw = this.craftMatrix.getStackInSlot(i);
			if(localiw != null) {
				this.craftMatrix.decrStackSize(i, 1);
				if(localiw.getItem().hasContainerItem()) {
					this.craftMatrix.setInventorySlotContents(i, new ItemStack(localiw.getItem().getContainerItem()));
				}
			}
		}

	}
}
