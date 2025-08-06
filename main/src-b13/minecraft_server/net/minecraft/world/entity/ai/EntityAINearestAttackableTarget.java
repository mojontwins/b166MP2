package net.minecraft.world.entity.ai;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;

public class EntityAINearestAttackableTarget extends EntityAITarget {
	EntityLiving targetEntity;
	Class<?> targetClass;
	int field_48386_f;
	private EntityAINearestAttackableTargetSorter field_48387_g;

	public EntityAINearestAttackableTarget(EntityLiving entityLiving1, Class<?> class2, float f3, int i4, boolean z5) {
		this(entityLiving1, class2, f3, i4, z5, false);
	}

	public EntityAINearestAttackableTarget(EntityLiving entityLiving1, Class<?> class2, float f3, int i4, boolean z5, boolean z6) {
		super(entityLiving1, f3, z5, z6);
		this.targetClass = class2;
		this.field_48379_d = f3;
		this.field_48386_f = i4;
		this.field_48387_g = new EntityAINearestAttackableTargetSorter(this, entityLiving1);
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(this.field_48386_f > 0 && this.taskOwner.getRNG().nextInt(this.field_48386_f) != 0) {
			return false;
		} else {
			if(this.targetClass == EntityPlayer.class) {
				EntityPlayer entityPlayer1 = this.taskOwner.worldObj.getClosestVulnerablePlayerToEntity(this.taskOwner, (double)this.field_48379_d);
				if(this.func_48376_a(entityPlayer1, false)) {
					this.targetEntity = entityPlayer1;
					return true;
				}
			} else {
				List<?> list5 = this.taskOwner.worldObj.getEntitiesWithinAABB(this.targetClass, this.taskOwner.boundingBox.expand((double)this.field_48379_d, 4.0D, (double)this.field_48379_d));
				Collections.sort(list5, this.field_48387_g);
				Iterator<?> iterator2 = list5.iterator();

				while(iterator2.hasNext()) {
					Entity entity3 = (Entity)iterator2.next();
					EntityLiving entityLiving4 = (EntityLiving)entity3;
					if(this.func_48376_a(entityLiving4, false)) {
						this.targetEntity = entityLiving4;
						return true;
					}
				}
			}

			return false;
		}
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.targetEntity);
		super.startExecuting();
	}
}
