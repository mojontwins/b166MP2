package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.util.StringTranslate;

public class GuiDownloadTerrain extends GuiScreen {
	private NetClientHandler netHandler;

	public GuiDownloadTerrain(NetClientHandler netClientHandler1) {
		this.netHandler = netClientHandler1;
	}

	protected void keyTyped(char c1, int i2) {
	}

	public void initGui() {
		this.controlList.clear();
	}

	public void updateScreen() {
		++this.updateCounter;
		if(this.updateCounter % 20 == 0) {
			this.netHandler.addToSendQueue(new Packet0KeepAlive());
		}

		if(this.netHandler != null) {
			this.netHandler.processReadPackets();
		}

	}

	protected void actionPerformed(GuiButton guiButton1) {
	}

	public void drawScreen(int i1, int i2, float f3) {
		this.drawBackground(0);
		StringTranslate stringTranslate4 = StringTranslate.getInstance();
		this.drawCenteredString(this.fontRenderer, stringTranslate4.translateKey("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
		super.drawScreen(i1, i2, f3);
	}
}
