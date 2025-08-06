package net.minecraft.world.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityVillager;
import net.minecraft.world.entity.monster.EntityIronGolem;

public class EntityAIFollowGolem extends EntityAIBase {
	private EntityVillager theVillager;
	private EntityIronGolem theGolem;
	private int field_48402_c;
	private boolean field_48400_d = false;

	public EntityAIFollowGolem(EntityVillager entityVillager1) {
		this.theVillager = entityVillager1;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		if(this.theVillager.getGrowingAge() >= 0) {
			return false;
		} else if(!this.theVillager.worldObj.isDaytime()) {
			return false;
		} else {
			List<Entity> list1 = this.theVillager.worldObj.getEntitiesWithinAABB(EntityIronGolem.class, this.theVillager.boundingBox.expand(6.0D, 2.0D, 6.0D));
			if(list1.size() == 0) {
				return false;
			} else {
				Iterator<Entity> iterator2 = list1.iterator();

				while(iterator2.hasNext()) {
					Entity entity3 = (Entity)iterator2.next();
					EntityIronGolem entityIronGolem4 = (EntityIronGolem)entity3;
					if(entityIronGolem4.func_48117_D_() > 0) {
						this.theGolem = entityIronGolem4;
						break;
					}
				}

				return this.theGolem != null;
			}
		}
	}

	public boolean continueExecuting() {
		return this.theGolem.func_48117_D_() > 0;
	}

	public void startExecuting() {
		this.field_48402_c = this.theVillager.getRNG().nextInt(320);
		this.field_48400_d = false;
		this.theGolem.getNavigator().clearPathEntity();
	}

	public void resetTask() {
		this.theGolem = null;
		this.theVillager.getNavigator().clearPathEntity();
	}

	public void updateTask() {
		this.theVillager.getLookHelper().setLookPositionWithEntity(this.theGolem, 30.0F, 30.0F);
		if(this.theGolem.func_48117_D_() == this.field_48402_c) {
			this.theVillager.getNavigator().tryMoveToEntityLiving(this.theGolem, 0.15F);
			this.field_48400_d = true;
		}

		if(this.field_48400_d && this.theVillager.getDistanceSqToEntity(this.theGolem) < 4.0D) {
			this.theGolem.func_48116_a(false);
			this.theVillager.getNavigator().clearPathEntity();
		}

	}
}
