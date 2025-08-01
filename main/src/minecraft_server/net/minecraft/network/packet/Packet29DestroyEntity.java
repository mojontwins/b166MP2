package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet29DestroyEntity extends Packet {
	public int entityId;

	public Packet29DestroyEntity() {
	}

	public Packet29DestroyEntity(int i1) {
		this.entityId = i1;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleDestroyEntity(this);
	}

	public int getPacketSize() {
		return 4;
	}
}
