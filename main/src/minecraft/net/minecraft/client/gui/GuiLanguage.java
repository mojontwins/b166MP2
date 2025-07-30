package net.minecraft.client.gui;

import net.minecraft.util.StringTranslate;

public class GuiLanguage extends GuiScreen {
	protected GuiScreen parentGui;
	private int updateTimer = -1;
	private GuiSlotLanguage languageList;
	private final GameSettings gameSettings;
	private GuiSmallButton doneButton;

	public GuiLanguage(GuiScreen guiScreen1, GameSettings gameSettings2) {
		this.parentGui = guiScreen1;
		this.gameSettings = gameSettings2;
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		this.controlList.add(this.doneButton = new GuiSmallButton(6, this.width / 2 - 75, this.height - 38, stringTranslate1.translateKey("gui.done")));
		this.languageList = new GuiSlotLanguage(this);
		this.languageList.registerScrollButtons(this.controlList, 7, 8);
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			switch(guiButton1.id) {
			case 5:
				break;
			case 6:
				this.gameSettings.saveOptions();
				this.mc.displayGuiScreen(this.parentGui);
				break;
			default:
				this.languageList.actionPerformed(guiButton1);
			}

		}
	}

	protected void mouseClicked(int i1, int i2, int i3) {
		super.mouseClicked(i1, i2, i3);
	}

	protected void mouseMovedOrUp(int i1, int i2, int i3) {
		super.mouseMovedOrUp(i1, i2, i3);
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.languageList.drawScreen(i1, i2, f3);
		if(this.updateTimer <= 0) {
			this.mc.texturePackList.updateAvaliableTexturePacks();
			this.updateTimer += 20;
		}

		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("options.language"), this.width / 2, 16, 0xFFFFFF);
		this.drawCenteredString(this.fontRenderer, "(" + stringTranslate4.translateKey("options.languageWarning") + ")", this.width / 2, this.height - 56, 8421504);
		super.drawScreen(i1, i2, f3);
	}

	public void updateScreen() {
		super.updateScreen();
		--this.updateTimer;
	}

	static GameSettings getGameSettings(GuiLanguage guiLanguage0) {
		return guiLanguage0.gameSettings;
	}

	static GuiSmallButton getDoneButton(GuiLanguage guiLanguage0) {
		return guiLanguage0.doneButton;
	}
}
