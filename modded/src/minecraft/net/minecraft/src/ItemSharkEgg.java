package net.minecraft.src;

public class ItemSharkEgg extends Item {
	public ItemSharkEgg(int i1) {
		super(i1);
		this.maxStackSize = 16;
	}

	public ItemStack onItemRightClick(ItemStack itemStack1, World world2, EntityPlayer entityPlayer3) {
		--itemStack1.stackSize;
		if(!world2.multiplayerWorld) {
			EntitySharkEgg entitySharkEgg4 = new EntitySharkEgg(world2);
			entitySharkEgg4.setPosition(entityPlayer3.posX, entityPlayer3.posY, entityPlayer3.posZ);
			world2.entityJoinedWorld(entitySharkEgg4);
			entitySharkEgg4.motionY += (double)(world2.rand.nextFloat() * 0.05F);
			entitySharkEgg4.motionX += (double)((world2.rand.nextFloat() - world2.rand.nextFloat()) * 0.3F);
			entitySharkEgg4.motionZ += (double)((world2.rand.nextFloat() - world2.rand.nextFloat()) * 0.3F);
		}

		return itemStack1;
	}
}
