package net.minecraft.world.entity;

import net.minecraft.world.entity.animal.IAnimals;
import net.minecraft.world.level.World;

public class EntityAmbientCreature extends EntityLiving implements IAnimals {
	public EntityAmbientCreature(World var1) {
		super(var1);
	}

	@Override
	public int getMaxHealth() {
		return 10;
	}
}
