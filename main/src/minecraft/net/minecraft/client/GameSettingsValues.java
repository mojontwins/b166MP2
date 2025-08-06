package net.minecraft.client;

import net.minecraft.client.gui.GameSettings;

public class GameSettingsValues {
	
	public static float musicVolume = 1.0F;
	public static float soundVolume = 1.0F;
	public static float mouseSensitivity = 0.5F;
	public static boolean invertMouse = false;
	public static int renderDistance = 0;
	public static boolean viewBobbing = true;

	public static boolean advancedOpengl = false;
	public static int limitFramerate = 1;
	public static boolean fancyGraphics = true;
	public static boolean ambientOcclusion = true;
	public static boolean clouds = true;
	public static String skin = "Default";
	
	public static int difficulty = 2;
	public static boolean hideGUI = false;
	public static int thirdPersonView = 0;
	public static boolean showDebugInfo = false;
	public static String lastServer = "";
	public static boolean noclip = false;
	public static boolean smoothCamera = false;
	public static boolean debugCamEnable = false;
	public static float noclipRate = 1.0F;
	public static float debugCamRate = 1.0F;
	public static float fovSetting = 0.0F;
	public static float gammaSetting = 0.0F;
	public static int guiScale = 0;
	public static int particleSetting = 0;
	public static String language = "en_US";
	public static String displayMode = GameSettings.DEFAULT_DISPLAY_STRING;
	public static boolean coloredFog = true;
	
	public static int ofMipmapLevel = 4;
	public static boolean ofMipmapLinear = false;
	public static boolean ofCustomFonts = false;
	public static boolean ofAnimatedTextures = true;
	
	public static boolean shouldRenderClouds() {
		return GameSettingsValues.renderDistance < 2 && GameSettingsValues.clouds;
	}
}
