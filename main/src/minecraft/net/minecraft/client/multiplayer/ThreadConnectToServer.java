package net.minecraft.client.multiplayer;

import java.net.ConnectException;
import java.net.UnknownHostException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.network.packet.Packet2Handshake;

public class ThreadConnectToServer extends Thread {
	final Minecraft mc;
	final String ip;
	final int port;
	final GuiConnecting connectingGui;

	public ThreadConnectToServer(GuiConnecting guiConnecting1, Minecraft minecraft2, String string3, int i4) {
		this.connectingGui = guiConnecting1;
		this.mc = minecraft2;
		this.ip = string3;
		this.port = i4;
	}

	public void run() {
		try {
			GuiConnecting.setNetClientHandler(this.connectingGui, new NetClientHandler(this.mc, this.ip, this.port));
			if(GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			GuiConnecting.getNetClientHandler(this.connectingGui).addToSendQueue(new Packet2Handshake(this.mc.session.username, this.ip, this.port));
		} catch (UnknownHostException unknownHostException2) {
			if(GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			this.mc.displayGuiScreen(new GuiDisconnected("connect.failed", "disconnect.genericReason", new Object[]{"Unknown host \'" + this.ip + "\'"}));
		} catch (ConnectException connectException3) {
			if(GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			this.mc.displayGuiScreen(new GuiDisconnected("connect.failed", "disconnect.genericReason", new Object[]{connectException3.getMessage()}));
		} catch (Exception exception4) {
			if(GuiConnecting.isCancelled(this.connectingGui)) {
				return;
			}

			exception4.printStackTrace();
			this.mc.displayGuiScreen(new GuiDisconnected("connect.failed", "disconnect.genericReason", new Object[]{exception4.toString()}));
		}

	}
}
