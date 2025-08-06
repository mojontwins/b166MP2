package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.IFireEntity;
import net.minecraft.world.level.World;
import net.minecraft.world.phys.MovingObjectPosition;

public class EntitySnowball extends EntityThrowable {
	public EntitySnowball(World world1) {
		super(world1);
	}

	public EntitySnowball(World world1, EntityLiving entityLiving2) {
		super(world1, entityLiving2);
	}

	public EntitySnowball(World world1, double d2, double d4, double d6) {
		super(world1, d2, d4, d6);
	}

	protected void onImpact(MovingObjectPosition movingObjectPosition1) {
		if(movingObjectPosition1.entityHit != null) {
			byte b2 = 0;
			if(movingObjectPosition1.entityHit instanceof IFireEntity) {
				b2 = 4;
			}

			if(movingObjectPosition1.entityHit.attackEntityFrom(DamageSource.causeThrownDamage(this, this.thrower), b2)) {
				;
			}
		}

		for(int i3 = 0; i3 < 8; ++i3) {
			this.worldObj.spawnParticle("snowballpoof", this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
		}

		if(!this.worldObj.isRemote) {
			this.setDead();
		}

	}
}
