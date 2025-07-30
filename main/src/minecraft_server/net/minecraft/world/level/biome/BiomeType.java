package net.minecraft.world.level.biome;

public class BiomeType {
	public static BiomeType forest = new BiomeType(0);
	public static BiomeType desert = new BiomeType(1);
	public static BiomeType hills = new BiomeType(2);
	public static BiomeType swamp = new BiomeType(3);
	public static BiomeType plains = new BiomeType(4);
	public static BiomeType taiga = new BiomeType(5);
	public static BiomeType jungle = new BiomeType(6);
	public static BiomeType veryCold = new BiomeType(7);
	public static BiomeType stitch = new BiomeType(8);
	public static BiomeType ocean = new BiomeType(9);
	public static BiomeType river = new BiomeType(10);
	public static BiomeType beach = new BiomeType(11);
	public static BiomeType mushroom = new BiomeType(12);
	public static BiomeType hell = new BiomeType(13);
	public static BiomeType end = new BiomeType(14);
	
	private int id;
	
	public BiomeType(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
