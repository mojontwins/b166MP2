package net.minecraft.world.item;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;

public class ItemSoup extends ItemFood {
	public ItemSoup(int i1, int i2) {
		super(i1, i2, false);
		this.setMaxStackSize(1);
	}

	public ItemStack onFoodEaten(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		super.onFoodEaten(itemStack1, world2, entityPlayer3);
		return new ItemStack(Item.bowlEmpty);
	}
}
