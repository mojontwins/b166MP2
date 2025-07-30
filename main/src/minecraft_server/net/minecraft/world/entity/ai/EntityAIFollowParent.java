package net.minecraft.world.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.EntityAnimal;

public class EntityAIFollowParent extends EntityAIBase {
	EntityAnimal childAnimal;
	EntityAnimal parentAnimal;
	float field_48248_c;
	private int field_48246_d;

	public EntityAIFollowParent(EntityAnimal entityAnimal1, float f2) {
		this.childAnimal = entityAnimal1;
		this.field_48248_c = f2;
	}

	public boolean shouldExecute() {
		if(this.childAnimal.getGrowingAge() >= 0) {
			return false;
		} else {
			List<Entity> list1 = this.childAnimal.worldObj.getEntitiesWithinAABB(this.childAnimal.getClass(), this.childAnimal.boundingBox.expand(8.0D, 4.0D, 8.0D));
			EntityAnimal entityAnimal2 = null;
			double d3 = Double.MAX_VALUE;
			Iterator<Entity> iterator5 = list1.iterator();

			while(iterator5.hasNext()) {
				Entity entity6 = (Entity)iterator5.next();
				EntityAnimal entityAnimal7 = (EntityAnimal)entity6;
				if(entityAnimal7.getGrowingAge() >= 0) {
					double d8 = this.childAnimal.getDistanceSqToEntity(entityAnimal7);
					if(d8 <= d3) {
						d3 = d8;
						entityAnimal2 = entityAnimal7;
					}
				}
			}

			if(entityAnimal2 == null) {
				return false;
			} else if(d3 < 9.0D) {
				return false;
			} else {
				this.parentAnimal = entityAnimal2;
				return true;
			}
		}
	}

	public boolean continueExecuting() {
		if(!this.parentAnimal.isEntityAlive()) {
			return false;
		} else {
			double d1 = this.childAnimal.getDistanceSqToEntity(this.parentAnimal);
			return d1 >= 9.0D && d1 <= 256.0D;
		}
	}

	public void startExecuting() {
		this.field_48246_d = 0;
	}

	public void resetTask() {
		this.parentAnimal = null;
	}

	public void updateTask() {
		if(--this.field_48246_d <= 0) {
			this.field_48246_d = 10;
			this.childAnimal.getNavigator().tryMoveToEntityLiving(this.parentAnimal, this.field_48248_c);
		}
	}
}
