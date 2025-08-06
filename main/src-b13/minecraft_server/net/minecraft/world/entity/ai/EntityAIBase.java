package net.minecraft.world.entity.ai;

public abstract class EntityAIBase {
	private int mutexBits = 0;

	public abstract boolean shouldExecute();

	public boolean continueExecuting() {
		return this.shouldExecute();
	}

	public boolean isContinuous() {
		return true;
	}

	public void startExecuting() {
	}

	public void resetTask() {
	}

	public void updateTask() {
	}

	public void setMutexBits(int i1) {
		this.mutexBits = i1;
	}

	public int getMutexBits() {
		return this.mutexBits;
	}
}
