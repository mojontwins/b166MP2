package net.minecraft.world.level;

public enum EnumSkyBlock {
	Sky(15),
	Block(0);

	public final int defaultLightValue;

	private EnumSkyBlock(int i3) {
		this.defaultLightValue = i3;
	}
}
