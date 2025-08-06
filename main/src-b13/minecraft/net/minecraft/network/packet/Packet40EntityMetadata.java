package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.network.DataWatcher;
import net.minecraft.network.NetHandler;
import net.minecraft.network.WatchableObject;

public class Packet40EntityMetadata extends Packet {
	public int entityId;
	private List<WatchableObject> metadata;

	public Packet40EntityMetadata() {
	}

	public Packet40EntityMetadata(int i1, DataWatcher dataWatcher2) {
		this.entityId = i1;
		this.metadata = dataWatcher2.getChangedObjects();
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.metadata = DataWatcher.readWatchableObjects(dataInputStream1);
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		DataWatcher.writeObjectsInListToStream(this.metadata, dataOutputStream1);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleEntityMetadata(this);
	}

	public List<WatchableObject> getMetadata() {
		return this.metadata;
	}

	public int getPacketSize() {
		return 5;
	}
}
