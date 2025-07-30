package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet7UseEntity extends Packet {
	public int playerEntityId;
	public int targetEntity;
	public int isLeftClick;

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.playerEntityId = dataInputStream1.readInt();
		this.targetEntity = dataInputStream1.readInt();
		this.isLeftClick = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.playerEntityId);
		dataOutputStream1.writeInt(this.targetEntity);
		dataOutputStream1.writeByte(this.isLeftClick);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_6006_a(this);
	}

	public int getPacketSize() {
		return 9;
	}
}
