package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

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
		netHandler1.func_20004_a(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
		this.inventoryType = dataInputStream1.readByte();
		this.windowTitle = dataInputStream1.readUTF();
		this.slotsCount = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
		dataOutputStream1.writeByte(this.inventoryType);
		dataOutputStream1.writeUTF(this.windowTitle);
		dataOutputStream1.writeByte(this.slotsCount);
	}

	public int getPacketSize() {
		return 3 + this.windowTitle.length();
	}
}
