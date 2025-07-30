package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.animal.EntityTameable;

public class EntityAITargetNonTamed extends EntityAINearestAttackableTarget {
	private EntityTameable field_48390_g;

	public EntityAITargetNonTamed(EntityTameable entityTameable1, Class<?> class2, float f3, int i4, boolean z5) {
		super(entityTameable1, class2, f3, i4, z5);
		this.field_48390_g = entityTameable1;
	}

	public boolean shouldExecute() {
		return this.field_48390_g.isTamed() ? false : super.shouldExecute();
	}
}
