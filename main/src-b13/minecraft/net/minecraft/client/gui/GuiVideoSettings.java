package net.minecraft.client.gui;

import net.minecraft.client.GameSettingsValues;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.Translator;

public class GuiVideoSettings extends GuiScreen {
	private GuiScreen parentGuiScreen;
	protected String screenTitle = "Video Settings";
	private GameSettings guiGameSettings;
	private boolean is64bit = false;
	private static EnumOptions[] videoOptions = new EnumOptions[]{
			EnumOptions.DISPLAY_MODES, EnumOptions.GRAPHICS, 
			EnumOptions.RENDER_DISTANCE, EnumOptions.AMBIENT_OCCLUSION, 
			EnumOptions.COLORED_FOG, EnumOptions.RENDER_CLOUDS,
			EnumOptions.MIPMAP_LEVEL, EnumOptions.MIPMAP_TYPE,
			EnumOptions.FRAMERATE_LIMIT, EnumOptions.PARTICLES, 
			EnumOptions.VIEW_BOBBING, EnumOptions.GUI_SCALE, 
			EnumOptions.ADVANCED_OPENGL, EnumOptions.GAMMA, 
	};

	public GuiVideoSettings(GuiScreen guiScreen1, GameSettings gameSettings2) {
		this.parentGuiScreen = guiScreen1;
		this.guiGameSettings = gameSettings2;
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		this.screenTitle = stringTranslate1.translateKey("options.videoTitle");
		int i2 = 0;
		EnumOptions[] enumOptions3 = videoOptions;
		int i4 = enumOptions3.length;

		int i5;
		for(i5 = 0; i5 < i4; ++i5) {
			EnumOptions enumOptions6 = enumOptions3[i5];
			if(!enumOptions6.getEnumFloat()) {
				this.controlList.add(new GuiSmallButton(enumOptions6.returnEnumOrdinal(), this.width / 2 - 155 + i2 % 2 * 160, this.height / 6 + 24 * (i2 >> 1), enumOptions6, this.guiGameSettings.getKeyBinding(enumOptions6)));
			} else {
				this.controlList.add(new GuiSlider(enumOptions6.returnEnumOrdinal(), this.width / 2 - 155 + i2 % 2 * 160, this.height / 6 + 24 * (i2 >> 1), enumOptions6, this.guiGameSettings.getKeyBinding(enumOptions6), this.guiGameSettings.getOptionFloatValue(enumOptions6)));
			}

			++i2;
		}

		this.controlList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, stringTranslate1.translateKey("gui.done")));
		this.is64bit = false;
		String[] string9 = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
		String[] string10 = string9;
		i5 = string9.length;

		for(int i11 = 0; i11 < i5; ++i11) {
			String string7 = string10[i11];
			String string8 = System.getProperty(string7);
			if(string8 != null && string8.indexOf("64") >= 0) {
				this.is64bit = true;
				break;
			}
		}

	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			int i2 = GameSettingsValues.guiScale;
			if(guiButton1.id < 100 && guiButton1 instanceof GuiSmallButton) {
				this.guiGameSettings.setOptionValue(((GuiSmallButton)guiButton1).returnEnumOptions(), 1);
				guiButton1.displayString = this.guiGameSettings.getKeyBinding(EnumOptions.getEnumOptions(guiButton1.id));
			}

			if(guiButton1.id == 200) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}

			if(GameSettingsValues.guiScale != i2) {
				ScaledResolution scaledResolution3 = new ScaledResolution(this.mc.displayWidth, this.mc.displayHeight);
				int i4 = scaledResolution3.getScaledWidth();
				int i5 = scaledResolution3.getScaledHeight();
				this.setWorldAndResolution(this.mc, i4, i5);
			}

		}
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 0xFFFFFF);
		if(!this.is64bit && GameSettingsValues.renderDistance == 0) {
			this.drawCenteredString(this.fontRenderer, Translator.translateToLocal("options.farWarning1"), this.width / 2, this.height / 6 + 144, 11468800);
			this.drawCenteredString(this.fontRenderer, Translator.translateToLocal("options.farWarning2"), this.width / 2, this.height / 6 + 144 + 12, 11468800);
		}

		super.drawScreen(i1, i2, f3);
	}
}
