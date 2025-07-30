package net.minecraft.world.entity.ai;

import java.util.Comparator;

import net.minecraft.world.entity.Entity;

public class EntityAINearestAttackableTargetSorter implements Comparator<Object> {
	private Entity theEntity;
	final EntityAINearestAttackableTarget parent;

	public EntityAINearestAttackableTargetSorter(EntityAINearestAttackableTarget entityAINearestAttackableTarget1, Entity entity2) {
		this.parent = entityAINearestAttackableTarget1;
		this.theEntity = entity2;
	}

	public int func_48469_a(Entity entity1, Entity entity2) {
		double d3 = this.theEntity.getDistanceSqToEntity(entity1);
		double d5 = this.theEntity.getDistanceSqToEntity(entity2);
		return d3 < d5 ? -1 : (d3 > d5 ? 1 : 0);
	}

	public int compare(Object object1, Object object2) {
		return this.func_48469_a((Entity)object1, (Entity)object2);
	}
}
