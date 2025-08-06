package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet101CloseWindow extends Packet {
	public int windowId;

	public Packet101CloseWindow() {
	}

	public Packet101CloseWindow(int i1) {
		this.windowId = i1;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleCloseWindow(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
	}

	public int getPacketSize() {
		return 1;
	}
}
