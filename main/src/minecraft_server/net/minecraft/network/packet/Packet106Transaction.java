package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet106Transaction extends Packet {
	public int windowId;
	public short shortWindowId;
	public boolean accepted;

	public Packet106Transaction() {
	}

	public Packet106Transaction(int i1, short s2, boolean z3) {
		this.windowId = i1;
		this.shortWindowId = s2;
		this.accepted = z3;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleTransaction(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
		this.shortWindowId = dataInputStream1.readShort();
		this.accepted = dataInputStream1.readByte() != 0;
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
		dataOutputStream1.writeShort(this.shortWindowId);
		dataOutputStream1.writeByte(this.accepted ? 1 : 0);
	}

	public int getPacketSize() {
		return 4;
	}
}
