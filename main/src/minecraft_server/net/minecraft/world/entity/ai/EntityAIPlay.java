package net.minecraft.world.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.EntityVillager;
import net.minecraft.world.phys.Vec3D;

public class EntityAIPlay extends EntityAIBase {
	private EntityVillager villagerObj;
	private EntityLiving targetVillager;
	private float field_48358_c;
	private int field_48356_d;

	public EntityAIPlay(EntityVillager entityVillager1, float f2) {
		this.villagerObj = entityVillager1;
		this.field_48358_c = f2;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(this.villagerObj.getGrowingAge() >= 0) {
			return false;
		} else if(this.villagerObj.getRNG().nextInt(400) != 0) {
			return false;
		} else {
			List<Entity> list1 = this.villagerObj.worldObj.getEntitiesWithinAABB(EntityVillager.class, this.villagerObj.boundingBox.expand(6.0D, 3.0D, 6.0D));
			double d2 = Double.MAX_VALUE;
			Iterator<Entity> iterator4 = list1.iterator();

			while(iterator4.hasNext()) {
				Entity entity5 = (Entity)iterator4.next();
				if(entity5 != this.villagerObj) {
					EntityVillager entityVillager6 = (EntityVillager)entity5;
					if(!entityVillager6.getIsPlayingFlag() && entityVillager6.getGrowingAge() < 0) {
						double d7 = entityVillager6.getDistanceSqToEntity(this.villagerObj);
						if(d7 <= d2) {
							d2 = d7;
							this.targetVillager = entityVillager6;
						}
					}
				}
			}

			if(this.targetVillager == null) {
				Vec3D vec3D9 = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);
				if(vec3D9 == null) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean continueExecuting() {
		return this.field_48356_d > 0;
	}

	public void startExecuting() {
		if(this.targetVillager != null) {
			this.villagerObj.setIsPlayingFlag(true);
		}

		this.field_48356_d = 1000;
	}

	public void resetTask() {
		this.villagerObj.setIsPlayingFlag(false);
		this.targetVillager = null;
	}

	public void updateTask() {
		--this.field_48356_d;
		if(this.targetVillager != null) {
			if(this.villagerObj.getDistanceSqToEntity(this.targetVillager) > 4.0D) {
				this.villagerObj.getNavigator().tryMoveToEntityLiving(this.targetVillager, this.field_48358_c);
			}
		} else if(this.villagerObj.getNavigator().noPath()) {
			Vec3D vec3D1 = RandomPositionGenerator.findRandomTarget(this.villagerObj, 16, 3);
			if(vec3D1 == null) {
				return;
			}

			this.villagerObj.getNavigator().tryMoveToXYZ(vec3D1.xCoord, vec3D1.yCoord, vec3D1.zCoord, this.field_48358_c);
		}

	}
}
