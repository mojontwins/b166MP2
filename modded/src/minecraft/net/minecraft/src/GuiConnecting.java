package net.minecraft.src;

import net.minecraft.client.Minecraft;

public class GuiConnecting extends GuiScreen {
	private NetClientHandler clientHandler;
	private boolean cancelled = false;

	public GuiConnecting(Minecraft minecraft, String s, int j) {
		System.out.println("Connecting to " + s + ", " + j);
		minecraft.changeWorld1((World)null);
		(new ThreadConnectToServer(this, minecraft, s, j)).start();
		ModSettings.setContext("MP" + ModLoader.getMinecraftInstance().gameSettings.lastServer.toLowerCase(), "mods/per_server/MP" + ModLoader.getMinecraftInstance().gameSettings.lastServer.toLowerCase() + "/");
	}

	public void updateScreen() {
		if(this.clientHandler != null) {
			this.clientHandler.processReadPackets();
		}

	}

	protected void keyTyped(char c, int j) {
	}

	public void initGui() {
		StringTranslate nd1 = StringTranslate.getInstance();
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, nd1.translateKey("gui.cancel")));
	}

	protected void actionPerformed(GuiButton ka1) {
		if(ka1.id == 0) {
			this.cancelled = true;
			if(this.clientHandler != null) {
				this.clientHandler.disconnect();
			}

			this.mc.displayGuiScreen(new GuiMainMenu());
		}

	}

	public void drawScreen(int j, int k, float f) {
		this.drawDefaultBackground();
		StringTranslate nd1 = StringTranslate.getInstance();
		if(this.clientHandler == null) {
			this.drawCenteredString(this.fontRenderer, nd1.translateKey("connect.connecting"), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
			this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 0xFFFFFF);
		} else {
			this.drawCenteredString(this.fontRenderer, nd1.translateKey("connect.authorizing"), this.width / 2, this.height / 2 - 50, 0xFFFFFF);
			this.drawCenteredString(this.fontRenderer, this.clientHandler.field_1209_a, this.width / 2, this.height / 2 - 10, 0xFFFFFF);
		}

		super.drawScreen(j, k, f);
	}

	static NetClientHandler setNetClientHandler(GuiConnecting vp1, NetClientHandler mx1) {
		return vp1.clientHandler = mx1;
	}

	static boolean isCancelled(GuiConnecting vp1) {
		return vp1.cancelled;
	}

	static NetClientHandler getNetClientHandler(GuiConnecting vp1) {
		return vp1.clientHandler;
	}
}
