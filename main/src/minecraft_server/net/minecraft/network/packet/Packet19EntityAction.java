package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.entity.Entity;

public class Packet19EntityAction extends Packet {
	public int entityId;
	public int state;

	public Packet19EntityAction() {
	}

	public Packet19EntityAction(Entity entity1, int i2) {
		this.entityId = entity1.entityId;
		this.state = i2;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.state = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeByte(this.state);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleEntityAction(this);
	}

	public int getPacketSize() {
		return 5;
	}
}
