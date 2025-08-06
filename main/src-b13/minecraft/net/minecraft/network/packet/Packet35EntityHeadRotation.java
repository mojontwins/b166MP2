package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet35EntityHeadRotation extends Packet {
	public int entityId;
	public byte headRotationYaw;

	public Packet35EntityHeadRotation() {
	}

	public Packet35EntityHeadRotation(int i1, byte b2) {
		this.entityId = i1;
		this.headRotationYaw = b2;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.headRotationYaw = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeByte(this.headRotationYaw);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleEntityHeadRotation(this);
	}

	public int getPacketSize() {
		return 5;
	}
}
