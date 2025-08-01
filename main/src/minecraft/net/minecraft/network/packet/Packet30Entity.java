package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet30Entity extends Packet {
	public int entityId;
	public byte xPosition;
	public byte yPosition;
	public byte zPosition;
	public byte yaw;
	public byte pitch;
	public boolean rotating = false;

	public Packet30Entity() {
	}

	public Packet30Entity(int i1) {
		this.entityId = i1;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleEntity(this);
	}

	public int getPacketSize() {
		return 4;
	}
}
