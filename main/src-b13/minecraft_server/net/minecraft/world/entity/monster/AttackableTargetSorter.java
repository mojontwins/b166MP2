package net.minecraft.world.entity.monster;

import java.util.Comparator;

import net.minecraft.world.entity.Entity;

public class AttackableTargetSorter implements Comparator<Object> {
	Entity theEntity = null;
	
	public AttackableTargetSorter(Entity theEntity) {
		this.theEntity = theEntity;
	}

	public int compareTargets(Entity entity1, Entity entity2) {
			double d1 = this.theEntity.getDistanceSqToEntity(entity1);
			double d2 = this.theEntity.getDistanceSqToEntity(entity2);

			if (d1 < d2) {
				return -1;
			}

			return d1 <= d2 ? 0 : 1;
	}
	
	public int compare(Object object1, Object object2) {
		return this.compareTargets((Entity)object1, (Entity)object2);
	}
}
