package com.mojontwins.minecraft.worldedit;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class ItemMagicWand extends Item {

	public ItemMagicWand(int i1) {
		super(i1);
	}
	
	public boolean onItemUse(ItemStack itemStack, EntityPlayer entityPlayer, World world, int x, int y, int z, int face, float xWithinFace, float yWithinFace, float zWithinFace) {
		if(entityPlayer.isSneaking()) {
			WorldEdit.setCorner2(x, y, z);
			world.getWorldAccess(0).showString("2nd point set to " + x + ", " + y + ", " + z);
		} else {
			WorldEdit.setCorner1(x, y, z);
			world.getWorldAccess(0).showString("1st point set to " + x + ", " + y + ", " + z);
		}
		return true;
	}

}
