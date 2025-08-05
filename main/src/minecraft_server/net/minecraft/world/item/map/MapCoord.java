package net.minecraft.world.item.map;

public class MapCoord {
	public byte field_28217_a;
	public byte centerX;
	public byte centerZ;
	public byte iconRotation;
	final MapData data;

	public MapCoord(MapData mapData1, byte b2, byte b3, byte b4, byte b5) {
		this.data = mapData1;
		this.field_28217_a = b2;
		this.centerX = b3;
		this.centerZ = b4;
		this.iconRotation = b5;
	}
}
