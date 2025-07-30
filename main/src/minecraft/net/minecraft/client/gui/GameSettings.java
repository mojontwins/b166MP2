package net.minecraft.client.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import net.minecraft.client.GameSettingsKeys;
import net.minecraft.client.GameSettingsValues;
import net.minecraft.client.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GraphicsModeSorter;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.Translator;

public class GameSettings {
	public static final String DEFAULT_DISPLAY_STRING = "DEFAULT";
	protected static final String[] RENDER_DISTANCES = new String[]{"options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};
	protected static final String[] DIFFICULTIES = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};
	protected static final String[] GUISCALES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
	protected static final String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
	protected static final String[] LIMIT_FRAMERATES = new String[]{"performance.max", "performance.balanced", "performance.powersaver"};

	public Minecraft mc;
	protected File optionsFile;
		
    public static final ArrayList<String> MODES = new ArrayList<String>();

	public GameSettings(Minecraft mc, File file2) {
		this.mc = mc;
		this.optionsFile = new File(file2, "options.txt");
		System.out.println ("Options file " + this.optionsFile);
		this.loadOptions();
	}

	public GameSettings() {
	}

	public String getKeyBindingDescription(int i1) {
		StringTranslate stringTranslate2 = StringTranslate.getInstance();
		return stringTranslate2.translateKey(GameSettingsKeys.keyBindings[i1].keyDescription);
	}

	public String getOptionDisplayString(int i1) {
		int i2 = GameSettingsKeys.keyBindings[i1].keyCode;
		return getKeyDisplayString(i2);
	}

	public static String getKeyDisplayString(int i0) {
		return i0 < 0 ? Translator.translateToLocalFormatted("key.mouseButton", new Object[]{i0 + 101}) : Keyboard.getKeyName(i0);
	}

	public void setKeyBinding(int i1, int i2) {
		GameSettingsKeys.keyBindings[i1].keyCode = i2;
		this.saveOptions();
	}

	public void setOptionFloatValue(EnumOptions enumOptions1, float f2) {
		if(enumOptions1 == EnumOptions.MUSIC) {
			GameSettingsValues.musicVolume = f2;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(enumOptions1 == EnumOptions.SOUND) {
			GameSettingsValues.soundVolume = f2;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(enumOptions1 == EnumOptions.SENSITIVITY) {
			GameSettingsValues.mouseSensitivity = f2;
		}

		if(enumOptions1 == EnumOptions.FOV) {
			GameSettingsValues.fovSetting = f2;
		}

		if(enumOptions1 == EnumOptions.GAMMA) {
			GameSettingsValues.gammaSetting = f2;
		}

	}

	public void setOptionValue(EnumOptions enumOptions1, int i2) {
		if(enumOptions1 == EnumOptions.INVERT_MOUSE) {
			GameSettingsValues.invertMouse = !GameSettingsValues.invertMouse;
		}

		if(enumOptions1 == EnumOptions.RENDER_DISTANCE) {
			GameSettingsValues.renderDistance = GameSettingsValues.renderDistance + i2 & 3;
		}

		if(enumOptions1 == EnumOptions.GUI_SCALE) {
			GameSettingsValues.guiScale = GameSettingsValues.guiScale + i2 & 3;
		}

		if(enumOptions1 == EnumOptions.PARTICLES) {
			GameSettingsValues.particleSetting = (GameSettingsValues.particleSetting + i2) % 3;
		}

		if(enumOptions1 == EnumOptions.VIEW_BOBBING) {
			GameSettingsValues.viewBobbing = !GameSettingsValues.viewBobbing;
		}

		if(enumOptions1 == EnumOptions.RENDER_CLOUDS) {
			GameSettingsValues.clouds = !GameSettingsValues.clouds;
		}

		if(enumOptions1 == EnumOptions.COLORED_FOG) {
			GameSettingsValues.coloredFog = !GameSettingsValues.coloredFog;
		}

		if(enumOptions1 == EnumOptions.ADVANCED_OPENGL) {
			GameSettingsValues.advancedOpengl = !GameSettingsValues.advancedOpengl;
			this.mc.renderGlobal.loadRenderers();
		}

		if(enumOptions1 == EnumOptions.FRAMERATE_LIMIT) {
			GameSettingsValues.limitFramerate = (GameSettingsValues.limitFramerate + i2 + 3) % 3;
		}

		if(enumOptions1 == EnumOptions.DIFFICULTY) {
			GameSettingsValues.difficulty = GameSettingsValues.difficulty + i2 & 3;
		}

		if(enumOptions1 == EnumOptions.GRAPHICS) {
			GameSettingsValues.fancyGraphics = !GameSettingsValues.fancyGraphics;
			this.mc.renderGlobal.loadRenderers();
		}

		if(enumOptions1 == EnumOptions.AMBIENT_OCCLUSION) {
			GameSettingsValues.ambientOcclusion = !GameSettingsValues.ambientOcclusion;
			this.mc.renderGlobal.loadRenderers();
		}
		
		if(enumOptions1 == EnumOptions.DISPLAY_MODES) {
			int idx = MODES.indexOf(GameSettingsValues.displayMode);
			idx ++; if(idx >= MODES.size()) idx = 0;
			GameSettingsValues.displayMode = MODES.get(idx);
		}

		if(enumOptions1 == EnumOptions.MIPMAP_LEVEL) {
			++GameSettingsValues.ofMipmapLevel;
			if(GameSettingsValues.ofMipmapLevel > 4) {
				GameSettingsValues.ofMipmapLevel = 0;
			}

			this.mc.renderEngine.refreshTextures();
		}

		if(enumOptions1 == EnumOptions.MIPMAP_TYPE) {
			GameSettingsValues.ofMipmapLinear = !GameSettingsValues.ofMipmapLinear;
			this.mc.renderEngine.refreshTextures();
		}

		this.saveOptions();
	}

	public float getOptionFloatValue(EnumOptions enumOptions1) {
		return enumOptions1 == EnumOptions.FOV ? GameSettingsValues.fovSetting : (enumOptions1 == EnumOptions.GAMMA ? GameSettingsValues.gammaSetting : (enumOptions1 == EnumOptions.MUSIC ? GameSettingsValues.musicVolume : (enumOptions1 == EnumOptions.SOUND ? GameSettingsValues.soundVolume : (enumOptions1 == EnumOptions.SENSITIVITY ? GameSettingsValues.mouseSensitivity : 0.0F))));
	}

	public boolean getOptionOrdinalValue(EnumOptions enumOptions1) {
		switch(EnumOptionsMappingHelper.enumOptionsMappingHelperArray[enumOptions1.ordinal()]) {
		case 1:
			return GameSettingsValues.invertMouse;
		case 2:
			return GameSettingsValues.viewBobbing;
		case 4:
			return GameSettingsValues.advancedOpengl;
		case 5:
			return GameSettingsValues.ambientOcclusion;
		case 6:
			return GameSettingsValues.clouds;
		case 7:
			return GameSettingsValues.coloredFog;
		default:
			return false;
		}
	}

	private static String showLocalizedOptionText(String[] string0, int i1) {
		if(i1 < 0 || i1 >= string0.length) {
			i1 = 0;
		}

		StringTranslate stringTranslate2 = StringTranslate.getInstance();
		return stringTranslate2.translateKey(string0[i1]);
	}

	public String getKeyBinding(EnumOptions enumOptions1) {
		StringTranslate stringTranslate2 = StringTranslate.getInstance();
		String string3 = stringTranslate2.translateKey(enumOptions1.getEnumString()) + ": ";

		if(enumOptions1 == EnumOptions.MIPMAP_LEVEL) {
			return GameSettingsValues.ofMipmapLevel == 0 ? string3 + "OFF" : (GameSettingsValues.ofMipmapLevel == 4 ? string3 + "Max" : string3 + GameSettingsValues.ofMipmapLevel);
		} else if(enumOptions1 == EnumOptions.MIPMAP_TYPE) {
			return GameSettingsValues.ofMipmapLinear ? string3 + "Linear" : string3 + "Nearest";
		} else if(enumOptions1.getEnumFloat()) {
			float f5 = this.getOptionFloatValue(enumOptions1);
			return enumOptions1 == EnumOptions.SENSITIVITY ? (f5 == 0.0F ? string3 + stringTranslate2.translateKey("options.sensitivity.min") : (f5 == 1.0F ? string3 + stringTranslate2.translateKey("options.sensitivity.max") : string3 + (int)(f5 * 200.0F) + "%")) : (enumOptions1 == EnumOptions.FOV ? (f5 == 0.0F ? string3 + stringTranslate2.translateKey("options.fov.min") : (f5 == 1.0F ? string3 + stringTranslate2.translateKey("options.fov.max") : string3 + (int)(70.0F + f5 * 40.0F))) : (enumOptions1 == EnumOptions.GAMMA ? (f5 == 0.0F ? string3 + stringTranslate2.translateKey("options.gamma.min") : (f5 == 1.0F ? string3 + stringTranslate2.translateKey("options.gamma.max") : string3 + "+" + (int)(f5 * 100.0F) + "%")) : (f5 == 0.0F ? string3 + stringTranslate2.translateKey("options.off") : string3 + (int)(f5 * 100.0F) + "%")));
		} else if(enumOptions1.getEnumBoolean()) {
			boolean z4 = this.getOptionOrdinalValue(enumOptions1);
			return z4 ? string3 + stringTranslate2.translateKey("options.on") : string3 + stringTranslate2.translateKey("options.off");
		} else {
			return 
					enumOptions1 == EnumOptions.RENDER_DISTANCE ? 
							string3 + showLocalizedOptionText(RENDER_DISTANCES, GameSettingsValues.renderDistance) 
						: 
							(enumOptions1 == EnumOptions.DIFFICULTY ? 
									string3 + showLocalizedOptionText(DIFFICULTIES, GameSettingsValues.difficulty) 
								: 
									(enumOptions1 == EnumOptions.GUI_SCALE ? 
											string3 + showLocalizedOptionText(GUISCALES, GameSettingsValues.guiScale) 
										: 
											(enumOptions1 == EnumOptions.PARTICLES ? 
													string3 + showLocalizedOptionText(PARTICLES, GameSettingsValues.particleSetting) 
												: 
													(enumOptions1 == EnumOptions.FRAMERATE_LIMIT ? 
															string3 + showLocalizedOptionText(LIMIT_FRAMERATES, GameSettingsValues.limitFramerate) 
														: 
															(enumOptions1 == EnumOptions.GRAPHICS ? 
																	(GameSettingsValues.fancyGraphics ?
																			string3 + stringTranslate2.translateKey("options.graphics.fancy") 
																		: 
																			string3 + stringTranslate2.translateKey("options.graphics.fast")
																	) 
																: 
																	(enumOptions1 == EnumOptions.DISPLAY_MODES ?
																			string3 + GameSettingsValues.displayMode
																		:
																			string3))))));
		}
	}

	public void loadOptions() {
		try {
			if(!this.optionsFile.exists()) {
				return;
			}

			BufferedReader bufferedReader1 = new BufferedReader(new FileReader(this.optionsFile));
			String string2 = "";

			while((string2 = bufferedReader1.readLine()) != null) {
				try {
					String[] string3 = string2.split(":");
					if(string3[0].equals("music")) {
						GameSettingsValues.musicVolume = this.parseFloat(string3[1]);
					}

					if(string3[0].equals("sound")) {
						GameSettingsValues.soundVolume = this.parseFloat(string3[1]);
					}

					if(string3[0].equals("mouseSensitivity")) {
						GameSettingsValues.mouseSensitivity = this.parseFloat(string3[1]);
					}

					if(string3[0].equals("fov")) {
						GameSettingsValues.fovSetting = this.parseFloat(string3[1]);
					}

					if(string3[0].equals("gamma")) {
						GameSettingsValues.gammaSetting = this.parseFloat(string3[1]);
					}

					if(string3[0].equals("invertYMouse")) {
						GameSettingsValues.invertMouse = string3[1].equals("true");
					}

					if(string3[0].equals("viewDistance")) {
						GameSettingsValues.renderDistance = Integer.parseInt(string3[1]);
					}

					if(string3[0].equals("guiScale")) {
						GameSettingsValues.guiScale = Integer.parseInt(string3[1]);
					}

					if(string3[0].equals("particles")) {
						GameSettingsValues.particleSetting = Integer.parseInt(string3[1]);
					}

					if(string3[0].equals("bobView")) {
						GameSettingsValues.viewBobbing = string3[1].equals("true");
					}

					if(string3[0].equals("advancedOpengl")) {
						GameSettingsValues.advancedOpengl = string3[1].equals("true");
					}

					if(string3[0].equals("fpsLimit")) {
						GameSettingsValues.limitFramerate = Integer.parseInt(string3[1]);
					}

					if(string3[0].equals("difficulty")) {
						GameSettingsValues.difficulty = Integer.parseInt(string3[1]);
					}

					if(string3[0].equals("fancyGraphics")) {
						GameSettingsValues.fancyGraphics = string3[1].equals("true");
					}

					if(string3[0].equals("ao")) {
						GameSettingsValues.ambientOcclusion = string3[1].equals("true");
					}

					if(string3[0].equals("clouds")) {
						GameSettingsValues.clouds = string3[1].equals("true");
					}

					if(string3[0].equals("coloredFog")) {
						GameSettingsValues.coloredFog = string3[1].equals("true");
					}

					if(string3[0].equals("skin")) {
						GameSettingsValues.skin = string3[1];
					}

					if(string3[0].equals("lastServer") && string3.length >= 2) {
						GameSettingsValues.lastServer = string3[1];
					}

					if(string3[0].equals("lang") && string3.length >= 2) {
						GameSettingsValues.language = string3[1];
					}
					
					if(string3[0].equals("displayMode") && string3.length >= 2) {
						GameSettingsValues.displayMode = string3[1];
					}

					for(int i4 = 0; i4 < GameSettingsKeys.keyBindings.length; ++i4) {
						if(string3[0].equals("key_" + GameSettingsKeys.keyBindings[i4].keyDescription)) {
							GameSettingsKeys.keyBindings[i4].keyCode = Integer.parseInt(string3[1]);
						}
					}
					
					if(string3[0].equals("ofMipmapLevel") && string3.length >= 2) {
						GameSettingsValues.ofMipmapLevel = Integer.valueOf(string3[1]).intValue();
						if(GameSettingsValues.ofMipmapLevel < 0) {
							GameSettingsValues.ofMipmapLevel = 0;
						}

						if(GameSettingsValues.ofMipmapLevel > 4) {
							GameSettingsValues.ofMipmapLevel = 4;
						}
					}

					if(string3[0].equals("ofMipmapLinear") && string3.length >= 2) {
						GameSettingsValues.ofMipmapLinear = Boolean.valueOf(string3[1]).booleanValue();
					}
				} catch (Exception exception5) {
					System.out.println("Skipping bad option: " + string2);
				}
			}

			KeyBinding.resetKeyBindingArrayAndHash();
			bufferedReader1.close();
		} catch (Exception exception6) {
			System.out.println("Failed to load options");
			exception6.printStackTrace();
		}

	}

	private float parseFloat(String string1) {
		return string1.equals("true") ? 1.0F : (string1.equals("false") ? 0.0F : Float.parseFloat(string1));
	}

	public void saveOptions() {
		try {
			PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.optionsFile));
			printWriter1.println("music:" + GameSettingsValues.musicVolume);
			printWriter1.println("sound:" + GameSettingsValues.soundVolume);
			printWriter1.println("invertYMouse:" + GameSettingsValues.invertMouse);
			printWriter1.println("mouseSensitivity:" + GameSettingsValues.mouseSensitivity);
			printWriter1.println("fov:" + GameSettingsValues.fovSetting);
			printWriter1.println("gamma:" + GameSettingsValues.gammaSetting);
			printWriter1.println("viewDistance:" + GameSettingsValues.renderDistance);
			printWriter1.println("guiScale:" + GameSettingsValues.guiScale);
			printWriter1.println("particles:" + GameSettingsValues.particleSetting);
			printWriter1.println("bobView:" + GameSettingsValues.viewBobbing);
			printWriter1.println("advancedOpengl:" + GameSettingsValues.advancedOpengl);
			printWriter1.println("fpsLimit:" + GameSettingsValues.limitFramerate);
			printWriter1.println("difficulty:" + GameSettingsValues.difficulty);
			printWriter1.println("fancyGraphics:" + GameSettingsValues.fancyGraphics);
			printWriter1.println("ao:" + GameSettingsValues.ambientOcclusion);
			printWriter1.println("clouds:" + GameSettingsValues.clouds);
			printWriter1.println("skin:" + GameSettingsValues.skin);
			printWriter1.println("lastServer:" + GameSettingsValues.lastServer);
			printWriter1.println("lang:" + GameSettingsValues.language);
			printWriter1.println("displayMode:" + GameSettingsValues.displayMode);
			printWriter1.println("ofMipmapLevel:" + GameSettingsValues.ofMipmapLevel);
			printWriter1.println("ofMipmapLinear:" + GameSettingsValues.ofMipmapLinear);
			printWriter1.println("coloredFog:" + GameSettingsValues.coloredFog);

			for(int i2 = 0; i2 < GameSettingsKeys.keyBindings.length; ++i2) {
				printWriter1.println("key_" + GameSettingsKeys.keyBindings[i2].keyDescription + ":" + GameSettingsKeys.keyBindings[i2].keyCode);
			}

			printWriter1.close();
		} catch (Exception exception3) {
			System.out.println("Failed to save options");
			exception3.printStackTrace();
		}

	}

	
	
	static {
		DisplayMode current = Display.getDisplayMode();
				
        final ArrayList<DisplayMode> Resolutions = new ArrayList<DisplayMode>();
        MODES.add(DEFAULT_DISPLAY_STRING);
        try {
            final DisplayMode[] MODES = Display.getAvailableDisplayModes();
            for (int i = 0; i < MODES.length; ++i) {
                final DisplayMode mode = MODES[i];
                Resolutions.add(mode);
            }
        }
        catch (LWJGLException e) {
            e.printStackTrace();
        }

        for (final DisplayMode mode : Resolutions) {
        	if(mode.getBitsPerPixel() == current.getBitsPerPixel() && mode.getFrequency() == current.getFrequency());
            MODES.add(mode.getWidth() + "x" + mode.getHeight() + "x" + mode.getBitsPerPixel() + " " + mode.getFrequency() + "Hz");
        }
        
        Collections.sort(MODES, new GraphicsModeSorter());
    }

}
