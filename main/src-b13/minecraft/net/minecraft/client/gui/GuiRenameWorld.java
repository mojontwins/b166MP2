package net.minecraft.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.StringTranslate;
import net.minecraft.world.level.WorldInfo;
import net.minecraft.world.level.chunk.storage.ISaveFormat;

public class GuiRenameWorld extends GuiScreen {
	private GuiScreen parentGuiScreen;
	private GuiTextField theGuiTextField;
	private final String worldName;

	public GuiRenameWorld(GuiScreen guiScreen1, String string2) {
		this.parentGuiScreen = guiScreen1;
		this.worldName = string2;
	}

	public void updateScreen() {
		this.theGuiTextField.updateCursorCounter();
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, stringTranslate1.translateKey("selectWorld.renameButton")));
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, stringTranslate1.translateKey("gui.cancel")));
		ISaveFormat iSaveFormat2 = this.mc.getSaveLoader();
		WorldInfo worldInfo3 = iSaveFormat2.getWorldInfo(this.worldName);
		String string4 = worldInfo3.getWorldName();
		this.theGuiTextField = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 60, 200, 20);
		this.theGuiTextField.setFocused(true);
		this.theGuiTextField.setText(string4);
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			if(guiButton1.id == 1) {
				this.mc.displayGuiScreen(this.parentGuiScreen);
			} else if(guiButton1.id == 0) {
				ISaveFormat iSaveFormat2 = this.mc.getSaveLoader();
				iSaveFormat2.renameWorld(this.worldName, this.theGuiTextField.getText().trim());
				this.mc.displayGuiScreen(this.parentGuiScreen);
			}

		}
	}

	protected void keyTyped(char c1, int i2) {
		this.theGuiTextField.textboxKeyTyped(c1, i2);
		((GuiButton)this.controlList.get(0)).enabled = this.theGuiTextField.getText().trim().length() > 0;
		if(c1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}

	}

	protected void mouseClicked(int i1, int i2, int i3) {
		super.mouseClicked(i1, i2, i3);
		this.theGuiTextField.mouseClicked(i1, i2, i3);
	}

	public void drawScreen(int i1, int i2, float f3) {
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.renameTitle"), this.width / 2, this.height / 4 - 60 + 20, 0xFFFFFF);
		this.drawString(this.fontRenderer, stringTranslate4.translateKey("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
		this.theGuiTextField.drawTextBox();
		super.drawScreen(i1, i2, f3);
	}
}
