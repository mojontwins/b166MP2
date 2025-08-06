package net.minecraft.world.item;

import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityPig;
import net.minecraft.world.level.creative.CreativeTabs;

public class ItemSaddle extends Item {
	public ItemSaddle(int i1) {
		super(i1);
		this.maxStackSize = 1;
		
		this.displayOnCreativeTab = CreativeTabs.tabTransport;
	}

	public void useItemOnEntity(ItemStack itemStack1, EntityLiving entityLiving2) {
		if(entityLiving2 instanceof EntityPig) {
			EntityPig entityPig3 = (EntityPig)entityLiving2;
			if(!entityPig3.getSaddled() && !entityPig3.isChild()) {
				entityPig3.setSaddled(true);
				--itemStack1.stackSize;
			}
		}

	}

	public boolean hitEntity(ItemStack itemStack1, EntityLiving entityLiving2, EntityLiving entityLiving3) {
		this.useItemOnEntity(itemStack1, entityLiving2);
		return true;
	}
}
