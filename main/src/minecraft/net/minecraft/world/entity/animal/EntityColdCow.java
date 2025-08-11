package net.minecraft.world.entity.animal;

import java.util.List;

import com.mojang.nbt.NBTTagCompound;

import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.World;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.phys.Vec3D;

public class EntityColdCow extends EntityClassicCow {
	private int angerLevel = 0;
	protected int attackStrength = 4;
	
	public EntityColdCow(World world1) {
		super(world1);
		this.texture = "/mob/cow2.png";
		this.setSize(0.9F, 1.3F);
		this.health = this.getFullHealth();
	}
	
	// Make angerable!

	@Override
	public void writeEntityToNBT(NBTTagCompound nBTTagCompound1) {
		super.writeEntityToNBT(nBTTagCompound1);
		nBTTagCompound1.setShort("Anger", (short)this.angerLevel);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nBTTagCompound1) {
		super.readEntityFromNBT(nBTTagCompound1);
		this.angerLevel = nBTTagCompound1.getShort("Anger");
	}

	@Override
	protected Entity findPlayerToAttack() {
		if (this.angerLevel == 0) return null;

		EntityPlayer entityPlayer1 = this.worldObj.getClosestPlayerToEntity(this, 16.0D);
		return entityPlayer1 != null && !entityPlayer1.capabilities.isCreativeMode && this.canEntityBeSeen(entityPlayer1) ? entityPlayer1 : null;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource ds, int amount) {
		Entity entity = ds.getEntity();
		if(entity instanceof EntityPlayer && ((EntityPlayer)entity).capabilities.isCreativeMode) {
			return false;
		}
		
		List<Entity> list3 = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(32.0D, 32.0D, 32.0D));

		for(int i4 = 0; i4 < list3.size(); ++i4) {
			Entity entity5 = (Entity)list3.get(i4);
			if(entity5 instanceof EntityColdCow) {
				EntityColdCow entityToAnger = (EntityColdCow)entity5;
				entityToAnger.becomeAngryAt(entity);
			}
		}

		this.becomeAngryAt(entity);
	
		return super.attackEntityFrom(ds, amount);
	}
	
	private void becomeAngryAt(Entity entity) {
		this.entityToAttack = entity;
		this.angerLevel = 400 + this.rand.nextInt(400);
	}

	//
	
	@Override
	protected void updateEntityActionState() {
		this.hasAttacked = this.isMovementCeased();
		float f1 = 16.0F;
		if(this.entityToAttack == null) {
			this.entityToAttack = this.findPlayerToAttack();
			if(this.entityToAttack != null) {
				this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f1, true, false, false, true);
			}
		} else if(!this.entityToAttack.isEntityAlive()) {
			this.entityToAttack = null;
		} else {
			float f2 = this.entityToAttack.getDistanceToEntity(this);
			if(this.canEntityBeSeen(this.entityToAttack)) {
				this.attackEntity(this.entityToAttack, f2);
			} else {
				this.attackBlockedEntity(this.entityToAttack, f2);
			}
		}

		if(this.hasAttacked || this.entityToAttack == null || this.pathToEntity != null && this.rand.nextInt(20) != 0) {
			if(!this.hasAttacked && (this.pathToEntity == null && this.rand.nextInt(80) == 0 || this.rand.nextInt(80) == 0)) {
				this.getPathToThis();
			}
		} else {
			this.pathToEntity = this.worldObj.getPathEntityToEntity(this, this.entityToAttack, f1, true, false, false, true);
		}

		int i21 = MathHelper.floor_double(this.boundingBox.minY + 0.5D);
		boolean z3 = this.isInWater();
		boolean z4 = this.handleLavaMovement();
		this.rotationPitch = 0.0F;
		if(this.pathToEntity != null && this.rand.nextInt(100) != 0) {
			Vec3D vec3D5 = this.pathToEntity.getCurrentNodeVec3d(this);
			double d6 = (double)(this.width * 2.0F);

			while(vec3D5 != null && vec3D5.squareDistanceTo(this.posX, vec3D5.yCoord, this.posZ) < d6 * d6) {
				this.pathToEntity.incrementPathIndex();
				if(this.pathToEntity.isFinished()) {
					vec3D5 = null;
					this.pathToEntity = null;
				} else {
					vec3D5 = this.pathToEntity.getCurrentNodeVec3d(this);
				}
			}

			this.isJumping = false;
			if(vec3D5 != null) {
				double d8 = vec3D5.xCoord - this.posX;
				double d10 = vec3D5.zCoord - this.posZ;
				double d12 = vec3D5.yCoord - (double)i21;
				float f14 = (float)(Math.atan2(d10, d8) * 180.0D / (double)(float)Math.PI) - 90.0F;
				float f15 = f14 - this.rotationYaw;

				for(this.moveForward = this.moveSpeed; f15 < -180.0F; f15 += 360.0F) {
				}

				while(f15 >= 180.0F) {
					f15 -= 360.0F;
				}

				if(f15 > 30.0F) {
					f15 = 30.0F;
				}

				if(f15 < -30.0F) {
					f15 = -30.0F;
				}

				this.rotationYaw += f15;
				if(this.hasAttacked && this.entityToAttack != null) {
					double d16 = this.entityToAttack.posX - this.posX;
					double d18 = this.entityToAttack.posZ - this.posZ;
					float f20 = this.rotationYaw;
					this.rotationYaw = (float)(Math.atan2(d18, d16) * 180.0D / (double)(float)Math.PI) - 90.0F;
					f15 = (f20 - this.rotationYaw + 90.0F) * (float)Math.PI / 180.0F;
					this.moveStrafing = -MathHelper.sin(f15) * this.moveForward * 1.0F;
					this.moveForward = MathHelper.cos(f15) * this.moveForward * 1.0F;
				}

				if(d12 > 0.0D) {
					this.isJumping = true;
				}
			}

			if(this.entityToAttack != null) {
				this.faceEntity(this.entityToAttack, 30.0F, 30.0F);
			}

			if(this.isCollidedHorizontally && !this.hasPath()) {
				this.isJumping = true;
			}

			if(this.rand.nextFloat() < 0.8F && (z3 || z4)) {
				this.isJumping = true;
			}

		} else {
			super.updateEntityActionState();
			this.pathToEntity = null;
		}
	}

	protected void getPathToThis() {
		boolean z1 = false;
		int i2 = -1;
		int i3 = -1;
		int i4 = -1;
		float f5 = -99999.0F;

		for(int i6 = 0; i6 < 10; ++i6) {
			int i7 = MathHelper.floor_double(this.posX + (double)this.rand.nextInt(13) - 6.0D);
			int i8 = MathHelper.floor_double(this.posY + (double)this.rand.nextInt(7) - 3.0D);
			int i9 = MathHelper.floor_double(this.posZ + (double)this.rand.nextInt(13) - 6.0D);
			float f10 = this.getBlockPathWeight(i7, i8, i9);
			if(f10 > f5) {
				f5 = f10;
				i2 = i7;
				i3 = i8;
				i4 = i9;
				z1 = true;
			}
		}

		if(z1) {
			this.pathToEntity = this.worldObj.getEntityPathToXYZ(this, i2, i3, i4, 10.0F, true, false, false, true);
		}

	}
	
	@Override
	protected void attackEntity(Entity entity, float f2) {
		if(this.attackTime <= 0 && f2 < 2.0F && entity.boundingBox.maxY > this.boundingBox.minY && entity.boundingBox.minY < this.boundingBox.maxY) {
			this.attackTime = 20;
			entity.attackEntityFrom(DamageSource.causeMobDamage(this), this.attackStrength);
		}

	}
	
	@Override 
	public boolean hasPath() {
		return this.pathToEntity != null;
	}

	@Override
	public void setPathToEntity(PathEntity pathentity) {
		this.pathToEntity = pathentity;
	}

	public Entity getTarget() {
		return this.entityToAttack;
	}

	public void setTarget(Entity entity) {
		this.entityToAttack = entity;
	}
	
	public int getFullHealth() {
		return 15;
	}
	
	public void setInSnow() {
	}
}
