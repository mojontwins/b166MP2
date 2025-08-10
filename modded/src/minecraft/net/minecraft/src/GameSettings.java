package net.minecraft.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

import net.minecraft.client.Minecraft;

import org.lwjgl.input.Keyboard;

public class GameSettings {
	private static final String[] RENDER_DISTANCES = new String[]{"options.renderDistance.far", "options.renderDistance.normal", "options.renderDistance.short", "options.renderDistance.tiny"};
	private static final String[] DIFFICULTIES = new String[]{"options.difficulty.peaceful", "options.difficulty.easy", "options.difficulty.normal", "options.difficulty.hard"};
	private static final String[] GUISCALES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
	private static final String[] LIMIT_FRAMERATES = new String[]{"performance.max", "performance.balanced", "performance.powersaver"};
	public float musicVolume = 1.0F;
	public float soundVolume = 1.0F;
	public float mouseSensitivity = 0.5F;
	public boolean invertMouse = false;
	public int renderDistance = 0;
	public boolean viewBobbing = true;
	public boolean anaglyph = false;
	public boolean advancedOpengl = false;
	public int limitFramerate = 1;
	public boolean fancyGraphics = true;
	public boolean ambientOcclusion = true;
	public String skin = "Default";
	public KeyBinding keyBindForward = new KeyBinding("key.forward", 17);
	public KeyBinding keyBindLeft = new KeyBinding("key.left", 30);
	public KeyBinding keyBindBack = new KeyBinding("key.back", 31);
	public KeyBinding keyBindRight = new KeyBinding("key.right", 32);
	public KeyBinding keyBindJump = new KeyBinding("key.jump", 57);
	public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18);
	public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16);
	public KeyBinding keyBindChat = new KeyBinding("key.chat", 20);
	public KeyBinding keyBindToggleFog = new KeyBinding("key.fog", 33);
	public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42);
	public KeyBinding[] keyBindings = new KeyBinding[]{this.keyBindForward, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindDrop, this.keyBindInventory, this.keyBindChat, this.keyBindToggleFog};
	protected Minecraft mc;
	private File optionsFile;
	public int difficulty = 2;
	public boolean hideGUI = false;
	public boolean thirdPersonView = false;
	public boolean showDebugInfo = false;
	public String lastServer = "";
	public boolean field_22275_C = false;
	public boolean smoothCamera = false;
	public boolean field_22273_E = false;
	public float field_22272_F = 1.0F;
	public float field_22271_G = 1.0F;
	public int guiScale = 0;

	public GameSettings(Minecraft paramMinecraft, File paramFile) {
		this.mc = paramMinecraft;
		this.optionsFile = new File(paramFile, "options.txt");
		this.keyBindings = ModLoader.RegisterAllKeys(this.keyBindings);
		this.loadOptions();
	}

	public GameSettings() {
	}

	public String getKeyBindingDescription(int paramInt) {
		StringTranslate localnd = StringTranslate.getInstance();
		return localnd.translateKey(this.keyBindings[paramInt].keyDescription);
	}

	public String getOptionDisplayString(int paramInt) {
		return Keyboard.getKeyName(this.keyBindings[paramInt].keyCode);
	}

	public void setKeyBinding(int paramInt1, int paramInt2) {
		this.keyBindings[paramInt1].keyCode = paramInt2;
		this.saveOptions();
	}

	public void setOptionFloatValue(EnumOptions paramhr, float paramFloat) {
		if(paramhr == EnumOptions.MUSIC) {
			this.musicVolume = paramFloat;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(paramhr == EnumOptions.SOUND) {
			this.soundVolume = paramFloat;
			this.mc.sndManager.onSoundOptionsChanged();
		}

		if(paramhr == EnumOptions.SENSITIVITY) {
			this.mouseSensitivity = paramFloat;
		}

	}

	public void setOptionValue(EnumOptions paramhr, int paramInt) {
		if(paramhr == EnumOptions.INVERT_MOUSE) {
			this.invertMouse = !this.invertMouse;
		}

		if(paramhr == EnumOptions.RENDER_DISTANCE) {
			this.renderDistance = this.renderDistance + paramInt & 3;
		}

		if(paramhr == EnumOptions.GUI_SCALE) {
			this.guiScale = this.guiScale + paramInt & 3;
		}

		if(paramhr == EnumOptions.VIEW_BOBBING) {
			this.viewBobbing = !this.viewBobbing;
		}

		if(paramhr == EnumOptions.ADVANCED_OPENGL) {
			this.advancedOpengl = !this.advancedOpengl;
			this.mc.renderGlobal.loadRenderers();
		}

		if(paramhr == EnumOptions.ANAGLYPH) {
			this.anaglyph = !this.anaglyph;
			this.mc.renderEngine.refreshTextures();
		}

		if(paramhr == EnumOptions.LIMIT_FRAMERATE) {
			this.limitFramerate = (this.limitFramerate + paramInt + 3) % 3;
		}

		if(paramhr == EnumOptions.DIFFICULTY) {
			this.difficulty = this.difficulty + paramInt & 3;
		}

		if(paramhr == EnumOptions.GRAPHICS) {
			this.fancyGraphics = !this.fancyGraphics;
			this.mc.renderGlobal.loadRenderers();
		}

		if(paramhr == EnumOptions.AMBIENT_OCCLUSION) {
			this.ambientOcclusion = !this.ambientOcclusion;
			this.mc.renderGlobal.loadRenderers();
		}

		this.saveOptions();
	}

	public float getOptionFloatValue(EnumOptions paramhr) {
		return paramhr == EnumOptions.MUSIC ? this.musicVolume : (paramhr == EnumOptions.SOUND ? this.soundVolume : (paramhr == EnumOptions.SENSITIVITY ? this.mouseSensitivity : 0.0F));
	}

	public boolean getOptionOrdinalValue(EnumOptions paramhr) {
		switch(EnumOptionsMappingHelper.enumOptionsMappingHelperArray[paramhr.ordinal()]) {
		case 1:
			return this.invertMouse;
		case 2:
			return this.viewBobbing;
		case 3:
			return this.anaglyph;
		case 4:
			return this.advancedOpengl;
		case 5:
			return this.ambientOcclusion;
		default:
			return false;
		}
	}

	public String getKeyBinding(EnumOptions paramhr) {
		StringTranslate localnd = StringTranslate.getInstance();
		String str = localnd.translateKey(paramhr.getEnumString()) + ": ";
		if(paramhr.getEnumFloat()) {
			float bool1 = this.getOptionFloatValue(paramhr);
			return paramhr == EnumOptions.SENSITIVITY ? (bool1 == 0.0F ? str + localnd.translateKey("options.sensitivity.min") : (bool1 == 1.0F ? str + localnd.translateKey("options.sensitivity.max") : str + (int)(bool1 * 200.0F) + "%")) : (bool1 == 0.0F ? str + localnd.translateKey("options.off") : str + (int)(bool1 * 100.0F) + "%");
		} else if(paramhr.getEnumBoolean()) {
			boolean bool = this.getOptionOrdinalValue(paramhr);
			return bool ? str + localnd.translateKey("options.on") : str + localnd.translateKey("options.off");
		} else {
			return paramhr == EnumOptions.RENDER_DISTANCE ? str + localnd.translateKey(RENDER_DISTANCES[this.renderDistance]) : (paramhr == EnumOptions.DIFFICULTY ? str + localnd.translateKey(DIFFICULTIES[this.difficulty]) : (paramhr == EnumOptions.GUI_SCALE ? str + localnd.translateKey(GUISCALES[this.guiScale]) : (paramhr == EnumOptions.LIMIT_FRAMERATE ? str + StatCollector.translateToLocal(LIMIT_FRAMERATES[this.limitFramerate]) : (paramhr == EnumOptions.GRAPHICS ? (this.fancyGraphics ? str + localnd.translateKey("options.graphics.fancy") : str + localnd.translateKey("options.graphics.fast")) : str))));
		}
	}

	public void loadOptions() {
		try {
			if(!this.optionsFile.exists()) {
				return;
			}

			BufferedReader localException1 = new BufferedReader(new FileReader(this.optionsFile));
			String str = "";

			while((str = localException1.readLine()) != null) {
				try {
					String[] localException2 = str.split(":");
					if(localException2[0].equals("music")) {
						this.musicVolume = this.parseFloat(localException2[1]);
					}

					if(localException2[0].equals("sound")) {
						this.soundVolume = this.parseFloat(localException2[1]);
					}

					if(localException2[0].equals("mouseSensitivity")) {
						this.mouseSensitivity = this.parseFloat(localException2[1]);
					}

					if(localException2[0].equals("invertYMouse")) {
						this.invertMouse = localException2[1].equals("true");
					}

					if(localException2[0].equals("viewDistance")) {
						this.renderDistance = Integer.parseInt(localException2[1]);
					}

					if(localException2[0].equals("guiScale")) {
						this.guiScale = Integer.parseInt(localException2[1]);
					}

					if(localException2[0].equals("bobView")) {
						this.viewBobbing = localException2[1].equals("true");
					}

					if(localException2[0].equals("anaglyph3d")) {
						this.anaglyph = localException2[1].equals("true");
					}

					if(localException2[0].equals("advancedOpengl")) {
						this.advancedOpengl = localException2[1].equals("true");
					}

					if(localException2[0].equals("fpsLimit")) {
						this.limitFramerate = Integer.parseInt(localException2[1]);
					}

					if(localException2[0].equals("difficulty")) {
						this.difficulty = Integer.parseInt(localException2[1]);
					}

					if(localException2[0].equals("fancyGraphics")) {
						this.fancyGraphics = localException2[1].equals("true");
					}

					if(localException2[0].equals("ao")) {
						this.ambientOcclusion = localException2[1].equals("true");
					}

					if(localException2[0].equals("skin")) {
						this.skin = localException2[1];
					}

					if(localException2[0].equals("lastServer") && localException2.length >= 2) {
						this.lastServer = localException2[1];
					}

					for(int i1 = 0; i1 < this.keyBindings.length; ++i1) {
						if(localException2[0].equals("key_" + this.keyBindings[i1].keyDescription)) {
							this.keyBindings[i1].keyCode = Integer.parseInt(localException2[1]);
						}
					}
				} catch (Exception exception5) {
					System.out.println("Skipping bad option: " + str);
				}
			}

			localException1.close();
		} catch (Exception exception6) {
			System.out.println("Failed to load options");
			exception6.printStackTrace();
		}

	}

	private float parseFloat(String paramString) {
		return paramString.equals("true") ? 1.0F : (paramString.equals("false") ? 0.0F : Float.parseFloat(paramString));
	}

	public void saveOptions() {
		try {
			PrintWriter localException = new PrintWriter(new FileWriter(this.optionsFile));
			localException.println("music:" + this.musicVolume);
			localException.println("sound:" + this.soundVolume);
			localException.println("invertYMouse:" + this.invertMouse);
			localException.println("mouseSensitivity:" + this.mouseSensitivity);
			localException.println("viewDistance:" + this.renderDistance);
			localException.println("guiScale:" + this.guiScale);
			localException.println("bobView:" + this.viewBobbing);
			localException.println("anaglyph3d:" + this.anaglyph);
			localException.println("advancedOpengl:" + this.advancedOpengl);
			localException.println("fpsLimit:" + this.limitFramerate);
			localException.println("difficulty:" + this.difficulty);
			localException.println("fancyGraphics:" + this.fancyGraphics);
			localException.println("ao:" + this.ambientOcclusion);
			localException.println("skin:" + this.skin);
			localException.println("lastServer:" + this.lastServer);

			for(int i1 = 0; i1 < this.keyBindings.length; ++i1) {
				localException.println("key_" + this.keyBindings[i1].keyDescription + ":" + this.keyBindings[i1].keyCode);
			}

			localException.close();
		} catch (Exception exception3) {
			System.out.println("Failed to save options");
			exception3.printStackTrace();
		}

	}
}
