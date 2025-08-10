package net.minecraft.src;

public class GuiOptions extends GuiScreen {
	private GuiScreen parentScreen;
	protected String screenTitle = "Options";
	private GameSettings options;
	private static EnumOptions[] field_22135_k = new EnumOptions[]{EnumOptions.MUSIC, EnumOptions.SOUND, EnumOptions.INVERT_MOUSE, EnumOptions.SENSITIVITY, EnumOptions.DIFFICULTY};

	public GuiOptions(GuiScreen cy1, GameSettings kr1) {
		this.parentScreen = cy1;
		this.options = kr1;
	}

	public void initGui() {
		StringTranslate nd1 = StringTranslate.getInstance();
		this.screenTitle = nd1.translateKey("options.title");
		int k = 0;
		EnumOptions[] ahr = field_22135_k;
		int i1 = ahr.length;

		for(int j1 = 0; j1 < i1; ++j1) {
			EnumOptions hr1 = ahr[j1];
			if(!hr1.getEnumFloat()) {
				this.controlList.add(new GuiSmallButton(hr1.returnEnumOrdinal(), this.width / 2 - 155 + k % 2 * 160, this.height / 6 + 24 * (k >> 1), hr1, this.options.getKeyBinding(hr1)));
			} else {
				this.controlList.add(new GuiSlider(hr1.returnEnumOrdinal(), this.width / 2 - 155 + k % 2 * 160, this.height / 6 + 24 * (k >> 1), hr1, this.options.getKeyBinding(hr1), this.options.getOptionFloatValue(hr1)));
			}

			++k;
		}

		this.controlList.add(new GuiButton(101, this.width / 2 - 100, this.height / 6 + 96, nd1.translateKey("options.video")));
		this.controlList.add(new GuiButton(100, this.width / 2 - 100, this.height / 6 + 120, nd1.translateKey("options.controls")));
		this.controlList.add(new GuiButton(300, this.width / 2 - 100, this.height / 6 + 144, "Global Mod Settings"));
		this.controlList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, nd1.translateKey("gui.done")));
	}

	protected void actionPerformed(GuiButton ka1) {
		if(ka1.enabled) {
			if(ka1.id < 100 && ka1 instanceof GuiSmallButton) {
				this.options.setOptionValue(((GuiSmallButton)ka1).returnEnumOptions(), 1);
				ka1.displayString = this.options.getKeyBinding(EnumOptions.getEnumOptions(ka1.id));
			}

			if(ka1.id == 101) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(new GuiVideoSettings(this, this.options));
			}

			if(ka1.id == 100) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(new GuiControls(this, this.options));
			}

			if(ka1.id == 200) {
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentScreen);
			}

			if(ka1.id == 300) {
				this.mc.gameSettings.saveOptions();
				ModSettingScreen.guicontext = "";
				WidgetSetting.updateAll();
				GuiModScreen.show((GuiModScreen)(new GuiModSelect(this)));
			}

		}
	}

	public void drawScreen(int k, int i1, float f) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, this.screenTitle, this.width / 2, 20, 0xFFFFFF);
		super.drawScreen(k, i1, f);
	}
}
