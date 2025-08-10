package net.minecraft.src;

public class ScreenScaleProxy extends ScaledResolution {
	public ScreenScaleProxy(int c, int d) {
		super(ModLoader.getMinecraftInstance().gameSettings, c, d);
	}
}
