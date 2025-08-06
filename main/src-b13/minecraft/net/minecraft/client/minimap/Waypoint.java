package net.minecraft.client.minimap;

public class Waypoint {
	public String name;
	public int x;
	public int z;
	public boolean enabled;
	public float red = 0.0F;
	public float green = 1.0F;
	public float blue = 0.0F;
	public int dimension;

	public Waypoint(String name, int x, int z, boolean enabled, int dimension) {
		this.name = name;
		this.x = x;
		this.z = z;
		this.enabled = enabled;
		this.dimension = dimension;
	}

	public Waypoint(String name, int x, int z, boolean enabled, int dimension, float red, float green, float blue) {
		this(name, x, z, enabled, dimension);
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
}
