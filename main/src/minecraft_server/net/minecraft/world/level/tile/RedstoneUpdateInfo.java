package net.minecraft.world.level.tile;

public class RedstoneUpdateInfo {
	public int x;
	public int y;
	public int z;
	public long updateTime;

	public RedstoneUpdateInfo(int i1, int i2, int i3, long j4) {
		this.x = i1;
		this.y = i2;
		this.z = i3;
		this.updateTime = j4;
	}
}
