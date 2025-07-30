package net.minecraft.world.item;

import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.entity.projectile.EntitySnowball;
import net.minecraft.world.level.World;
import net.minecraft.world.level.creative.CreativeTabs;

public class ItemSnowball extends Item {
	public ItemSnowball(int i1) {
		super(i1);
		this.maxStackSize = 16;
		
		this.displayOnCreativeTab = CreativeTabs.tabMisc;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		if(!entityPlayer3.capabilities.isCreativeMode) {
			--itemStack1.stackSize;
		}

		world2.playSoundAtEntity(entityPlayer3, "random.bow", 0.5F, 0.4F / (itemRand.nextFloat() * 0.4F + 0.8F));
		if(!world2.isRemote) {
			world2.spawnEntityInWorld(new EntitySnowball(world2, entityPlayer3));
		}

		return itemStack1;
	}
}
