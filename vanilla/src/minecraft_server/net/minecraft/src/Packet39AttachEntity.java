package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet39AttachEntity extends Packet {
	public int entityId;
	public int vehicleEntityId;

	public Packet39AttachEntity() {
	}

	public Packet39AttachEntity(Entity entity1, Entity entity2) {
		this.entityId = entity1.entityId;
		this.vehicleEntityId = entity2 != null ? entity2.entityId : -1;
	}

	public int getPacketSize() {
		return 8;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.vehicleEntityId = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeInt(this.vehicleEntityId);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_6003_a(this);
	}
}
