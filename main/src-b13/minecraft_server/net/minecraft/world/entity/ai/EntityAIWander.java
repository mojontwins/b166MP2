package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class EntityAIWander extends EntityAIBase {
	private EntityCreature entity;
	private double field_46098_b;
	private double field_46099_c;
	private double field_46097_d;
	private float field_48317_e;

	public EntityAIWander(EntityCreature entityCreature1, float f2) {
		this.entity = entityCreature1;
		this.field_48317_e = f2;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(this.entity.getAge() >= 100) {
			return false;
		} else if(this.entity.getRNG().nextInt(120) != 0) {
			return false;
		} else {
			Vec3D vec3D1 = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
			if(vec3D1 == null) {
				return false;
			} else {
				this.field_46098_b = vec3D1.xCoord;
				this.field_46099_c = vec3D1.yCoord;
				this.field_46097_d = vec3D1.zCoord;
				return true;
			}
		}
	}

	public boolean continueExecuting() {
		return !this.entity.getNavigator().noPath();
	}

	public void startExecuting() {
		this.entity.getNavigator().tryMoveToXYZ(this.field_46098_b, this.field_46099_c, this.field_46097_d, this.field_48317_e);
	}
}
