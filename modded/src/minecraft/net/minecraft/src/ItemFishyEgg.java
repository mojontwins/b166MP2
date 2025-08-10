package net.minecraft.src;

public class ItemFishyEgg extends Item {
	public ItemFishyEgg(int i1) {
		super(i1);
		this.maxStackSize = 16;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		--itemStack1.stackSize;
		if(!world2.multiplayerWorld) {
			EntityFishyEgg entityFishyEgg4 = new EntityFishyEgg(world2);
			entityFishyEgg4.setPosition(entityPlayer3.posX, entityPlayer3.posY, entityPlayer3.posZ);
			world2.entityJoinedWorld(entityFishyEgg4);
			entityFishyEgg4.motionY += (double)(world2.rand.nextFloat() * 0.05F);
			entityFishyEgg4.motionX += (double)((world2.rand.nextFloat() - world2.rand.nextFloat()) * 0.3F);
			entityFishyEgg4.motionZ += (double)((world2.rand.nextFloat() - world2.rand.nextFloat()) * 0.3F);
		}

		return itemStack1;
	}
}
