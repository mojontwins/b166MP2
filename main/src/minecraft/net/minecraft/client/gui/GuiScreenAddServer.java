package net.minecraft.client.gui;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.StringTranslate;

public class GuiScreenAddServer extends GuiScreen {
	private GuiScreen parentGui;
	private GuiTextField serverAddress;
	private GuiTextField serverName;
	private ServerNBTStorage serverNBTStorage;

	public GuiScreenAddServer(GuiScreen guiScreen1, ServerNBTStorage serverNBTStorage2) {
		this.parentGui = guiScreen1;
		this.serverNBTStorage = serverNBTStorage2;
	}

	public void updateScreen() {
		this.serverName.updateCursorCounter();
		this.serverAddress.updateCursorCounter();
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		Keyboard.enableRepeatEvents(true);
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, stringTranslate1.translateKey("addServer.add")));
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, stringTranslate1.translateKey("gui.cancel")));
		this.serverName = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 76, 200, 20);
		this.serverName.setFocused(true);
		this.serverName.setText(this.serverNBTStorage.name);
		this.serverAddress = new GuiTextField(this, this.fontRenderer, this.width / 2 - 100, 116, 200, 20);
		this.serverAddress.setMaxStringLength(128);
		this.serverAddress.setText(this.serverNBTStorage.host);
		((GuiButton)this.controlList.get(0)).enabled = this.serverAddress.getText().length() > 0 && this.serverAddress.getText().split(":").length > 0 && this.serverName.getText().length() > 0;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.enabled) {
			if(guiButton1.id == 1) {
				this.parentGui.confirmClicked(false, 0);
			} else if(guiButton1.id == 0) {
				this.serverNBTStorage.name = this.serverName.getText();
				this.serverNBTStorage.host = this.serverAddress.getText();
				this.parentGui.confirmClicked(true, 0);
			}

		}
	}

	protected void keyTyped(char c1, int i2) {
		this.serverName.textboxKeyTyped(c1, i2);
		this.serverAddress.textboxKeyTyped(c1, i2);
		if(c1 == 9) {
			if(this.serverName.getIsFocused()) {
				this.serverName.setFocused(false);
				this.serverAddress.setFocused(true);
			} else {
				this.serverName.setFocused(true);
				this.serverAddress.setFocused(false);
			}
		}

		if(c1 == 13) {
			this.actionPerformed((GuiButton)this.controlList.get(0));
		}

		((GuiButton)this.controlList.get(0)).enabled = this.serverAddress.getText().length() > 0 && this.serverAddress.getText().split(":").length > 0 && this.serverName.getText().length() > 0;
		if(((GuiButton)this.controlList.get(0)).enabled) {
			String string3 = this.serverAddress.getText().trim();
			String[] string4 = string3.split(":");
			if(string4.length > 2) {
				((GuiButton)this.controlList.get(0)).enabled = false;
			}
		}

	}

	protected void mouseClicked(int i1, int i2, int i3) {
		super.mouseClicked(i1, i2, i3);
		this.serverAddress.mouseClicked(i1, i2, i3);
		this.serverName.mouseClicked(i1, i2, i3);
	}

	public void drawScreen(int i1, int i2, float f3) {
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("addServer.title"), this.width / 2, this.height / 4 - 60 + 20, 0xFFFFFF);
		this.drawString(this.fontRenderer, stringTranslate4.translateKey("addServer.enterName"), this.width / 2 - 100, 63, 10526880);
		this.drawString(this.fontRenderer, stringTranslate4.translateKey("addServer.enterIp"), this.width / 2 - 100, 104, 10526880);
		this.serverName.drawTextBox();
		this.serverAddress.drawTextBox();
		super.drawScreen(i1, i2, f3);
	}
}
