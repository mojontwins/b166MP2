package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.ThreadConnectToServer;
import net.minecraft.client.title.GuiMainMenu;
import net.minecraft.util.StringTranslate;
import net.minecraft.world.level.World;

public class GuiConnecting extends GuiScreen {
	private NetClientHandler clientHandler;
	private boolean cancelled = false;

	public GuiConnecting(Minecraft mc, String string2, int i3) {
		System.out.println("Connecting to " + string2 + ", " + i3);
		mc.changeWorld1((World)null);
		(new ThreadConnectToServer(this, mc, string2, i3)).start();
	}

	public void updateScreen() {
		if(this.clientHandler != null) {
			this.clientHandler.processReadPackets();
		}

	}

	protected void keyTyped(char c1, int i2) {
	}

	public void initGui() {
		StringTranslate stringTranslate1 = StringTranslate.getInstance();
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, stringTranslate1.translateKey("gui.cancel")));
	}

	protected void actionPerformed(GuiButton guiButton1) {
		if(guiButton1.id == 0) {
			this.cancelled = true;
			if(this.clientHandler != null) {
				this.clientHandler.disconnect();
			}

			this.mc.displayGuiScreen(new GuiMainMenu());
		}

	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawDefaultBackground();
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		if(this.clientHandler == null) {
			this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("connect.connecting"), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
			this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 0xFFFFFF);
		} else {
			this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("connect.authorizing"), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
			this.drawCenteredString(this.fontRenderer, this.clientHandler.field_1209_a, this.width / 2, this.height / 2 - 10, 0xFFFFFF);
		}

		super.drawScreen(i1, i2, f3);
	}

	public static NetClientHandler setNetClientHandler(GuiConnecting guiConnecting0, NetClientHandler netClientHandler1) {
		return guiConnecting0.clientHandler = netClientHandler1;
	}

	public static boolean isCancelled(GuiConnecting guiConnecting0) {
		return guiConnecting0.cancelled;
	}

	public static NetClientHandler getNetClientHandler(GuiConnecting guiConnecting0) {
		return guiConnecting0.clientHandler;
	}
}
