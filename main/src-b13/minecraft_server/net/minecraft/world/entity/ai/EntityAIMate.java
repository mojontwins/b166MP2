package net.minecraft.world.entity.ai;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.EntityAnimal;
import net.minecraft.world.level.World;

public class EntityAIMate extends EntityAIBase {
	private EntityAnimal theAnimal;
	World theWorld;
	private EntityAnimal targetMate;
	int field_48261_b = 0;
	float field_48262_c;

	public EntityAIMate(EntityAnimal entityAnimal1, float f2) {
		this.theAnimal = entityAnimal1;
		this.theWorld = entityAnimal1.worldObj;
		this.field_48262_c = f2;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		if(!this.theAnimal.isInLove()) {
			return false;
		} else {
			this.targetMate = this.func_48258_h();
			return this.targetMate != null;
		}
	}

	public boolean continueExecuting() {
		return this.targetMate.isEntityAlive() && this.targetMate.isInLove() && this.field_48261_b < 60;
	}

	public void resetTask() {
		this.targetMate = null;
		this.field_48261_b = 0;
	}

	public void updateTask() {
		this.theAnimal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float)this.theAnimal.getVerticalFaceSpeed());
		this.theAnimal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.field_48262_c);
		++this.field_48261_b;
		if(this.field_48261_b == 60) {
			this.func_48257_i();
		}

	}

	private EntityAnimal func_48258_h() {
		float f1 = 8.0F;
		List<Entity> list2 = this.theWorld.getEntitiesWithinAABB(this.theAnimal.getClass(), this.theAnimal.boundingBox.expand((double)f1, (double)f1, (double)f1));
		Iterator<Entity> iterator3 = list2.iterator();

		EntityAnimal entityAnimal5;
		do {
			if(!iterator3.hasNext()) {
				return null;
			}

			Entity entity4 = (Entity)iterator3.next();
			entityAnimal5 = (EntityAnimal)entity4;
		} while(!this.theAnimal.func_48135_b(entityAnimal5));

		return entityAnimal5;
	}

	private void func_48257_i() {
		EntityAnimal entityAnimal1 = this.theAnimal.spawnBabyAnimal(this.targetMate);
		if(entityAnimal1 != null) {
			this.theAnimal.setGrowingAge(6000);
			this.targetMate.setGrowingAge(6000);
			this.theAnimal.resetInLove();
			this.targetMate.resetInLove();
			entityAnimal1.setGrowingAge(-24000);
			entityAnimal1.setLocationAndAngles(this.theAnimal.posX, this.theAnimal.posY, this.theAnimal.posZ, 0.0F, 0.0F);
			this.theWorld.spawnEntityInWorld(entityAnimal1);
			Random random2 = this.theAnimal.getRNG();

			for(int i3 = 0; i3 < 7; ++i3) {
				double d4 = random2.nextGaussian() * 0.02D;
				double d6 = random2.nextGaussian() * 0.02D;
				double d8 = random2.nextGaussian() * 0.02D;
				this.theWorld.spawnParticle("heart", this.theAnimal.posX + (double)(random2.nextFloat() * this.theAnimal.width * 2.0F) - (double)this.theAnimal.width, this.theAnimal.posY + 0.5D + (double)(random2.nextFloat() * this.theAnimal.height), this.theAnimal.posZ + (double)(random2.nextFloat() * this.theAnimal.width * 2.0F) - (double)this.theAnimal.width, d4, d6, d8);
			}

		}
	}
}
