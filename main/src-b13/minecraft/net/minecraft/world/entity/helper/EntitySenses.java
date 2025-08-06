package net.minecraft.world.entity.helper;

import java.util.ArrayList;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;

public class EntitySenses {
	EntityLiving entityObj;
	ArrayList<Entity> canSeeCachePositive = new ArrayList<Entity>();
	ArrayList<Entity> canSeeCacheNegative = new ArrayList<Entity>();

	public EntitySenses(EntityLiving entityLiving1) {
		this.entityObj = entityLiving1;
	}

	public void clearSensingCache() {
		this.canSeeCachePositive.clear();
		this.canSeeCacheNegative.clear();
	}

	public boolean canSee(Entity entity1) {
		if(this.canSeeCachePositive.contains(entity1)) {
			return true;
		} else if(this.canSeeCacheNegative.contains(entity1)) {
			return false;
		} else {
			//Profiler.startSection("canSee");
			boolean z2 = this.entityObj.canEntityBeSeen(entity1);
			//Profiler.endSection();
			if(z2) {
				this.canSeeCachePositive.add(entity1);
			} else {
				this.canSeeCacheNegative.add(entity1);
			}

			return z2;
		}
	}
}
