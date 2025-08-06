package net.minecraft.client.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.multiplayer.ThreadPollServers;
import net.minecraft.client.renderer.Tessellator;

public class GuiSlotServer extends GuiSlot {
	public final GuiMultiplayer parentGui;

	public GuiSlotServer(GuiMultiplayer guiMultiplayer1) {
		super(guiMultiplayer1.mc, guiMultiplayer1.width, guiMultiplayer1.height, 32, guiMultiplayer1.height - 64, 36);
		this.parentGui = guiMultiplayer1;
	}

	protected int getSize() {
		return GuiMultiplayer.getServerList(this.parentGui).size();
	}

	protected void elementClicked(int i1, boolean z2) {
		GuiMultiplayer.setSelectedServer(this.parentGui, i1);
		boolean z3 = GuiMultiplayer.getSelectedServer(this.parentGui) >= 0 && GuiMultiplayer.getSelectedServer(this.parentGui) < this.getSize();
		GuiMultiplayer.getButtonSelect(this.parentGui).enabled = z3;
		GuiMultiplayer.getButtonEdit(this.parentGui).enabled = z3;
		GuiMultiplayer.getButtonDelete(this.parentGui).enabled = z3;
		if(z2 && z3) {
			GuiMultiplayer.joinServer(this.parentGui, i1);
		}

	}

	protected boolean isSelected(int i1) {
		return i1 == GuiMultiplayer.getSelectedServer(this.parentGui);
	}

	protected int getContentHeight() {
		return GuiMultiplayer.getServerList(this.parentGui).size() * 36;
	}

	protected void drawBackground() {
		this.parentGui.drawDefaultBackground();
	}

	protected void drawSlot(int i1, int i2, int i3, int i4, Tessellator tessellator5) {
		ServerNBTStorage serverNBTStorage6 = (ServerNBTStorage)GuiMultiplayer.getServerList(this.parentGui).get(i1);
		synchronized(GuiMultiplayer.getLock()) {
			if(GuiMultiplayer.getThreadsPending() < 5 && !serverNBTStorage6.polled) {
				serverNBTStorage6.polled = true;
				serverNBTStorage6.lag = -2L;
				serverNBTStorage6.motd = "";
				serverNBTStorage6.playerCount = "";
				GuiMultiplayer.incrementThreadsPending();
				(new ThreadPollServers(this, serverNBTStorage6)).start();
			}
		}

		this.parentGui.drawString(this.parentGui.fontRenderer, serverNBTStorage6.name, i2 + 2, i3 + 1, 0xFFFFFF);
		this.parentGui.drawString(this.parentGui.fontRenderer, serverNBTStorage6.motd, i2 + 2, i3 + 12, 8421504);
		this.parentGui.drawString(this.parentGui.fontRenderer, serverNBTStorage6.playerCount, i2 + 215 - this.parentGui.fontRenderer.getStringWidth(serverNBTStorage6.playerCount), i3 + 12, 8421504);
		this.parentGui.drawString(this.parentGui.fontRenderer, serverNBTStorage6.host, i2 + 2, i3 + 12 + 11, 3158064);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.parentGui.mc.renderEngine.bindTexture(this.parentGui.mc.renderEngine.getTexture("/gui/icons.png"));
		String string9 = "";
		byte b7;
		int i8;
		if(serverNBTStorage6.polled && serverNBTStorage6.lag != -2L) {
			b7 = 0;
			if(serverNBTStorage6.lag < 0L) {
				i8 = 5;
			} else if(serverNBTStorage6.lag < 150L) {
				i8 = 0;
			} else if(serverNBTStorage6.lag < 300L) {
				i8 = 1;
			} else if(serverNBTStorage6.lag < 600L) {
				i8 = 2;
			} else if(serverNBTStorage6.lag < 1000L) {
				i8 = 3;
			} else {
				i8 = 4;
			}

			if(serverNBTStorage6.lag < 0L) {
				string9 = "(no connection)";
			} else {
				string9 = serverNBTStorage6.lag + "ms";
			}
		} else {
			b7 = 1;
			i8 = (int)(System.currentTimeMillis() / 100L + (long)(i1 * 2) & 7L);
			if(i8 > 4) {
				i8 = 8 - i8;
			}

			string9 = "Polling..";
		}

		this.parentGui.drawTexturedModalRect(i2 + 205, i3, 0 + b7 * 10, 176 + i8 * 8, 10, 8);
		byte b10 = 4;
		if(this.mouseX >= i2 + 205 - b10 && this.mouseY >= i3 - b10 && this.mouseX <= i2 + 205 + 10 + b10 && this.mouseY <= i3 + 8 + b10) {
			GuiMultiplayer.setTooltipText(this.parentGui, string9);
		}

	}
}
