package net.minecraft.world.entity.projectile;

import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.level.World;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.phys.MovingObjectPosition;

public class EntitySmallFireball extends EntityFireball {
	public EntitySmallFireball(World world1) {
		super(world1);
		this.setSize(0.3125F, 0.3125F);
	}

	public EntitySmallFireball(World world1, EntityLiving entityLiving2, double d3, double d5, double d7) {
		super(world1, entityLiving2, d3, d5, d7);
		this.setSize(0.3125F, 0.3125F);
	}

	public EntitySmallFireball(World world1, double d2, double d4, double d6, double d8, double d10, double d12) {
		super(world1, d2, d4, d6, d8, d10, d12);
		this.setSize(0.3125F, 0.3125F);
	}

	protected void throwableHitEntity(MovingObjectPosition movingObjectPosition1) {
		if(!this.worldObj.isRemote) {
			if(movingObjectPosition1.entityHit != null) {
				if(!movingObjectPosition1.entityHit.isImmuneToFire() && movingObjectPosition1.entityHit.attackEntityFrom(DamageSource.causeFireballDamage(this, this.shootingEntity), 5)) {
					movingObjectPosition1.entityHit.setFire(5);
				}
			} else {
				int i2 = movingObjectPosition1.blockX;
				int i3 = movingObjectPosition1.blockY;
				int i4 = movingObjectPosition1.blockZ;
				switch(movingObjectPosition1.sideHit) {
				case 0:
					--i3;
					break;
				case 1:
					++i3;
					break;
				case 2:
					--i4;
					break;
				case 3:
					++i4;
					break;
				case 4:
					--i2;
					break;
				case 5:
					++i2;
				}

				if(this.worldObj.isAirBlock(i2, i3, i4)) {
					this.worldObj.setBlockWithNotify(i2, i3, i4, Block.fire.blockID);
				}
			}

			this.setDead();
		}

	}

	public boolean canBeCollidedWith() {
		return false;
	}

	public boolean attackEntityFrom(DamageSource damageSource1, int i2) {
		return false;
	}
}
