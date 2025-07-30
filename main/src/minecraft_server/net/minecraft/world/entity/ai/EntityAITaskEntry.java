package net.minecraft.world.entity.ai;

class EntityAITaskEntry {
	public EntityAIBase action;
	public int priority;
	final EntityAITasks tasks;

	public EntityAITaskEntry(EntityAITasks entityAITasks1, int i2, EntityAIBase entityAIBase3) {
		this.tasks = entityAITasks1;
		this.priority = i2;
		this.action = entityAIBase3;
	}
}
