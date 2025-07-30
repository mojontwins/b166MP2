package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet38EntityStatus extends Packet {
	public int entityId;
	public byte entityStatus;

	public Packet38EntityStatus() {
	}

	public Packet38EntityStatus(int i1, byte b2) {
		this.entityId = i1;
		this.entityStatus = b2;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.entityStatus = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeByte(this.entityStatus);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleEntityStatus(this);
	}

	public int getPacketSize() {
		return 5;
	}
}
