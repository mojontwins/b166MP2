package net.minecraft.world.level;

public class LootItem {
	public int id;
	public int damage;
	public int maxQuantity;
	public boolean isRare;
	public int chance;
	
	public LootItem (int id, int damage, int maxQuantity, boolean isRare, int chance) {
		this.id = id;
		this.damage = damage;
		this.maxQuantity = maxQuantity;
		this.isRare = isRare;
		this.chance = chance;
	}
}
