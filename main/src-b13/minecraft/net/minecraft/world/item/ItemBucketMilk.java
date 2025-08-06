package net.minecraft.world.item;

import net.minecraft.world.entity.EnumAction;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;

public class ItemBucketMilk extends Item {
	public ItemBucketMilk(int i1) {
		super(i1);
		this.setMaxStackSize(1);
		
		this.displayOnCreativeTab = CreativeTabs.tabMisc;
	}

	public ItemStack onFoodEaten(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		--itemStack1.stackSize;
		if(!world2.isRemote) {
			//entityPlayer3.clearActivePotions();
		}

		return itemStack1.stackSize <= 0 ? new ItemStack(Item.bucketEmpty) : itemStack1;
	}

	public int getMaxItemUseDuration(ItemStack itemStack1) {
		return 32;
	}

	public EnumAction getItemUseAction(ItemStack itemStack1) {
		return EnumAction.drink;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		entityPlayer3.setItemInUse(itemStack1, this.getMaxItemUseDuration(itemStack1));
		return itemStack1;
	}
}
