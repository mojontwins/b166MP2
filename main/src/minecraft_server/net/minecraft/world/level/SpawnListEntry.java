package net.minecraft.world.level;

public class SpawnListEntry extends WeightedRandomChoice {
	public Class<?> entityClass;
	public int minGroupCount;
	public int maxGroupCount;

	public SpawnListEntry(Class<?> class1, int weight, int minGroup, int maxGroup) {
		super(weight);
		this.entityClass = class1;
		this.minGroupCount = minGroup;
		this.maxGroupCount = maxGroup;
	}
	
	public String toString() {
		return "SpawnListEntry [" + this.entityClass + ", " + this.minGroupCount + ", " + this.maxGroupCount + "]";
	}
}
