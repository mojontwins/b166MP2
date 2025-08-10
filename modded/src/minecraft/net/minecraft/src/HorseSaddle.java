package net.minecraft.src;

public class HorseSaddle extends Item {
	public HorseSaddle(int i1) {
		super(i1);
		this.maxStackSize = 1;
	}

	public void saddleEntity(ItemStack itemStack1, EntityLiving entityLiving2) {
		if(entityLiving2 instanceof EntityHorse) {
			EntityHorse entityHorse3 = (EntityHorse)entityLiving2;
			if(!entityHorse3.rideable && entityHorse3.adult) {
				entityHorse3.rideable = true;
				--itemStack1.stackSize;
			}
		}

	}

	public void hitEntity(ItemStack itemStack1, EntityLiving entityLiving2) {
		this.saddleEntity(itemStack1, entityLiving2);
	}
}
