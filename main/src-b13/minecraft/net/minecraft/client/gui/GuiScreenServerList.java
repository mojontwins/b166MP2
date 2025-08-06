package net.minecraft.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.StringTranslate;

public class GuiScreenServerList extends GuiScreen {
	private static String field_52009_d = "";
	private final GuiScreen guiScreen;
	private final ServerNBTStorage serverListStorage;
	private GuiTextField serverTextField;

	public GuiScreenServerList(GuiScreen guiScreen1, ServerNBTStorage serverNBTStorage2) {
		this.guiScreen = guiScreen1;
		this.serverListStorage = serverNBTStorage2;
	}

	public void updateScreen() {
		this.serverTextField.updateCursorCounter();
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, stringTranslate1.translateKey("selectServer.select")));
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, stringTranslate1.translateKey("gui.cancel")));
		this.serverTextField = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
		this.serverTextField.setMaxStringLength(128);
		this.serverTextField.setFocused(true);
		this.serverTextField.setText(field_52009_d);
		((GuiButton)this.controlList.get(0)).enabled = this.serverTextField.getText().length() > 0 && this.serverTextField.getText().split(":").length > 0;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		field_52009_d = this.serverTextField.getText();
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			if(guiButton1.id == 1) {
				this.guiScreen.confirmClicked(false, 0);
			} else if(guiButton1.id == 0) {
				this.serverListStorage.host = this.serverTextField.getText();
				this.guiScreen.confirmClicked(true, 0);
			}

		}
	}

	protected void keyTyped(char c1, int i2) {
		this.serverTextField.textboxKeyTyped(c1, i2);
		if(c1 == 28) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}

		((GuiButton)this.controlList.get(0)).enabled = this.serverTextField.getText().length() > 0 && this.serverTextField.getText().split(":").length > 0;
	}

	protected void mouseClicked(int i1, int i2, int i3) {
		super.mouseClicked(i1, i2, i3);
		this.serverTextField.mouseClicked(i1, i2, i3);
	}

	public void drawScreen(int i1, int i2, float f3) {
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("selectServer.direct"), this.width / 2, this.height / 4 - 60 + 20, 0xFFFFFF);
		this.drawString(this.fontRenderer, stringTranslate4.translateKey("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
		this.serverTextField.drawTextBox();
		super.drawScreen(i1, i2, f3);
	}
}
