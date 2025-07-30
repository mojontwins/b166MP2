package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.animal.EntityChicken;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;

public class EntityEgg extends EntityThrowable {
	public EntityEgg(World world1) {
		super(world1);
	}

	public EntityEgg(World world1, EntityLiving entityLiving2) {
		super(world1, entityLiving2);
	}

	public EntityEgg(World world1, double d2, double d4, double d6) {
		super(world1, d2, d4, d6);
	}

	protected void onImpact(MovingObjectPosition movingObjectPosition1) {
		if(movingObjectPosition1.entityHit != null && movingObjectPosition1.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.thrower), 0)) {
			;
		}

		if(!this.worldObj.isRemote && this.rand.nextInt(8) == 0) {
			byte b2 = 1;
			if(this.rand.nextInt(32) == 0) {
				b2 = 4;
			}

			for(int i3 = 0; i3 < b2; ++i3) {
				EntityChicken entityChicken4 = new EntityChicken(this.worldObj);
				entityChicken4.setGrowingAge(-24000);
				entityChicken4.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, 0.0F);
				this.worldObj.spawnEntityInWorld(entityChicken4);
			}
		}

		for(int i5 = 0; i5 < 8; ++i5) {
			this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if(!this.worldObj.isRemote) {
			this.setDead();
		}

	}
}
