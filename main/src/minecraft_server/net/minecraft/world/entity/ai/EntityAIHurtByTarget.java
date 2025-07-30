package net.minecraft.world.entity.ai;

import java.util.Iterator;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.phys.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
	boolean field_48395_a;

	public EntityAIHurtByTarget(EntityLiving entityLiving1, boolean z2) {
		super(entityLiving1, 16.0F, false);
		this.field_48395_a = z2;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		return this.func_48376_a(this.taskOwner.getAITarget(), true);
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
		if(this.field_48395_a) {
			List<Entity> list1 = this.taskOwner.worldObj.getEntitiesWithinAABB(this.taskOwner.getClass(), AxisAlignedBB.getBoundingBoxFromPool(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ, this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D).expand((double)this.field_48379_d, 4.0D, (double)this.field_48379_d));
			Iterator<Entity> iterator2 = list1.iterator();

			while(iterator2.hasNext()) {
				Entity entity3 = (Entity)iterator2.next();
				EntityLiving entityLiving4 = (EntityLiving)entity3;
				if(this.taskOwner != entityLiving4 && entityLiving4.getAttackTarget() == null) {
					entityLiving4.setAttackTarget(this.taskOwner.getAITarget());
				}
			}
		}

		super.startExecuting();
	}
}
