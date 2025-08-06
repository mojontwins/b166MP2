package com.risugami.recipebook;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;

public class ItemRecipeBook extends Item {
	public ItemRecipeBook(int id) {
		super(id);
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
	}

	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
		//ModLoader.openGUI(player, new GuiRecipeBook(new InventoryRecipeBook(item)));
		player.displayGUIRecipeBook(item);
		return item;
	}

	public int getColorFromDamage(int metadata, int pass) {
		return 16752800;
	}
}
