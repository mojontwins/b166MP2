package net.minecraft.world.entity.ai;

import net.minecraft.world.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract {
	boolean field_48328_i;
	int field_48327_j;

	public EntityAIOpenDoor(EntityLiving entityLiving1, boolean z2) {
		super(entityLiving1);
		this.theEntity = entityLiving1;
		this.field_48328_i = z2;
	}

	public boolean continueExecuting() {
		return this.field_48328_i && this.field_48327_j > 0 && super.continueExecuting();
	}

	public void startExecuting() {
		this.field_48327_j = 20;
		this.targetDoor.onPoweredBlockChange(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, true);
	}

	public void resetTask() {
		if(this.field_48328_i) {
			this.targetDoor.onPoweredBlockChange(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, false);
		}

	}

	public void updateTask() {
		--this.field_48327_j;
		super.updateTask();
	}
}
