package net.minecraft.world.entity.projectile;

public class EntityEggInfo {
	public int spawnedID;
	public int primaryColor;
	public int secondaryColor;
	public String mobName;

	public EntityEggInfo(int id, int color1, int color2, String name) {
		this.spawnedID = id;
		this.primaryColor = color1;
		this.secondaryColor = color2;
		this.mobName = name;
	}
}
