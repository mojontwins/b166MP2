package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityCreature;
import net.minecraft.world.phys.Vec3D;

public class EntityAIPanic extends EntityAIBase {
	private EntityCreature theCreature;
	private float field_48314_b;
	private double field_48315_c;
	private double field_48312_d;
	private double field_48313_e;

	public EntityAIPanic(EntityCreature entityCreature1, float f2) {
		this.theCreature = entityCreature1;
		this.field_48314_b = f2;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if(this.theCreature.getAITarget() == null) {
			return false;
		} else {
			Vec3D vec3D1 = RandomPositionGenerator.findRandomTarget(this.theCreature, 5, 4);
			if(vec3D1 == null) {
				return false;
			} else {
				this.field_48315_c = vec3D1.xCoord;
				this.field_48312_d = vec3D1.yCoord;
				this.field_48313_e = vec3D1.zCoord;
				return true;
			}
		}
	}

	public void startExecuting() {
		this.theCreature.getNavigator().tryMoveToXYZ(this.field_48315_c, this.field_48312_d, this.field_48313_e, this.field_48314_b);
	}

	public boolean continueExecuting() {
		return !this.theCreature.getNavigator().noPath();
	}
}
