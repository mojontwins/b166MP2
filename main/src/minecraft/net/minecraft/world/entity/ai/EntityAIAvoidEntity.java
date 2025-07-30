package net.minecraft.world.entity.ai;

import java.util.List;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.entity.animal.EntityTameable;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.pathfinder.PathEntity;
import net.minecraft.world.level.pathfinder.PathNavigate;
import net.minecraft.world.phys.Vec3D;

public class EntityAIAvoidEntity extends EntityAIBase {
	private EntityCreature theEntity;
	private float field_48242_b;
	private float field_48243_c;
	private Entity field_48240_d;
	private float field_48241_e;
	private PathEntity field_48238_f;
	private PathNavigate entityPathNavigate;
	private Class<?> targetEntityClass;

	public EntityAIAvoidEntity(EntityCreature entityCreature1, Class<?> class2, float f3, float f4, float f5) {
		this.theEntity = entityCreature1;
		this.targetEntityClass = class2;
		this.field_48241_e = f3;
		this.field_48242_b = f4;
		this.field_48243_c = f5;
		this.entityPathNavigate = entityCreature1.getNavigator();
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(this.targetEntityClass == EntityPlayer.class) {
			if(this.theEntity instanceof EntityTameable && ((EntityTameable)this.theEntity).isTamed()) {
				return false;
			}

			this.field_48240_d = this.theEntity.worldObj.getClosestPlayerToEntity(this.theEntity, (double)this.field_48241_e);
			if(this.field_48240_d == null) {
				return false;
			}
		} else {
			List<Entity> list1 = this.theEntity.worldObj.getEntitiesWithinAABB(this.targetEntityClass, this.theEntity.boundingBox.expand((double)this.field_48241_e, 3.0D, (double)this.field_48241_e));
			if(list1.size() == 0) {
				return false;
			}

			this.field_48240_d = (Entity)list1.get(0);
		}

		if(!this.theEntity.getEntitySenses().canSee(this.field_48240_d)) {
			return false;
		} else {
			Vec3D vec3D2 = RandomPositionGenerator.findRandomTargetBlockAwayFrom(this.theEntity, 16, 7, Vec3D.createVector(this.field_48240_d.posX, this.field_48240_d.posY, this.field_48240_d.posZ));
			if(vec3D2 == null) {
				return false;
			} else if(this.field_48240_d.getDistanceSq(vec3D2.xCoord, vec3D2.yCoord, vec3D2.zCoord) < this.field_48240_d.getDistanceSqToEntity(this.theEntity)) {
				return false;
			} else {
				this.field_48238_f = this.entityPathNavigate.getPathToXYZ(vec3D2.xCoord, vec3D2.yCoord, vec3D2.zCoord);
				return this.field_48238_f == null ? false : this.field_48238_f.func_48639_a(vec3D2);
			}
		}
	}

	public boolean continueExecuting() {
		return !this.entityPathNavigate.noPath();
	}

	public void startExecuting() {
		this.entityPathNavigate.setPath(this.field_48238_f, this.field_48242_b);
	}

	public void resetTask() {
		this.field_48240_d = null;
	}

	public void updateTask() {
		if(this.theEntity.getDistanceSqToEntity(this.field_48240_d) < 49.0D) {
			this.theEntity.getNavigator().setSpeed(this.field_48243_c);
		} else {
			this.theEntity.getNavigator().setSpeed(this.field_48242_b);
		}

	}
}
