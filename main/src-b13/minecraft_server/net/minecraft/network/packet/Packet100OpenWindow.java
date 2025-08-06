package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet100OpenWindow extends Packet {
	public int windowId;
	public int inventoryType;
	public String windowTitle;
	public int slotsCount;

	public Packet100OpenWindow() {
	}

	public Packet100OpenWindow(int i1, int i2, String string3, int i4) {
		this.windowId = i1;
		this.inventoryType = i2;
		this.windowTitle = string3;
		this.slotsCount = i4;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleOpenWindow(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte() & 255;
		this.inventoryType = dataInputStream1.readByte() & 255;
		this.windowTitle = readString(dataInputStream1, 32);
		this.slotsCount = dataInputStream1.readByte() & 255;
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId & 255);
		dataOutputStream1.writeByte(this.inventoryType & 255);
		writeString(this.windowTitle, dataOutputStream1);
		dataOutputStream1.writeByte(this.slotsCount & 255);
	}

	public int getPacketSize() {
		return 3 + this.windowTitle.length();
	}
}
