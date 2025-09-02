package net.minecraft.world.entity.monster;

public interface IMoCreature {
	void selectType();

	String getName();

	void setName(String string1);

	boolean getIsTamed();

	void setTamed(boolean z1);

	boolean getIsAdult();

	void setAdult(boolean z1);

	boolean checkSpawningBiome();

	boolean getCanSpawnHere();
}
