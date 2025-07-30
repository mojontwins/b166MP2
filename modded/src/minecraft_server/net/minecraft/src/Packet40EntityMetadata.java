package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

public class Packet40EntityMetadata extends Packet {
	public int entityId;
	private List field_21018_b;

	public Packet40EntityMetadata() {
	}

	public Packet40EntityMetadata(int i1, DataWatcher dataWatcher2) {
		this.entityId = i1;
		this.field_21018_b = dataWatcher2.getChangedObjects();
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.field_21018_b = DataWatcher.readWatchableObjects(dataInputStream1);
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		DataWatcher.writeObjectsInListToStream(this.field_21018_b, dataOutputStream1);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_21002_a(this);
	}

	public int getPacketSize() {
		return 5;
	}
}
