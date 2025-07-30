package net.minecraft.src;

public class MapCoord {
	public byte field_28202_a;
	public byte field_28201_b;
	public byte field_28205_c;
	public byte field_28204_d;
	final MapData field_28203_e;

	public MapCoord(MapData mapData1, byte b2, byte b3, byte b4, byte b5) {
		this.field_28203_e = mapData1;
		this.field_28202_a = b2;
		this.field_28201_b = b3;
		this.field_28205_c = b4;
		this.field_28204_d = b5;
	}
}
