package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet131MapData extends Packet {
	public short itemID;
	public short uniqueID;
	public byte[] itemData;

	public Packet131MapData() {
		this.isChunkDataPacket = true;
	}

	public Packet131MapData(short s1, short s2, byte[] b3) {
		this.isChunkDataPacket = true;
		this.itemID = s1;
		this.uniqueID = s2;
		this.itemData = b3;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.itemID = dataInputStream1.readShort();
		this.uniqueID = dataInputStream1.readShort();
		this.itemData = new byte[dataInputStream1.readByte() & 255];
		dataInputStream1.readFully(this.itemData);
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeShort(this.itemID);
		dataOutputStream1.writeShort(this.uniqueID);
		dataOutputStream1.writeByte(this.itemData.length);
		dataOutputStream1.write(this.itemData);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleMapData(this);
	}

	public int getPacketSize() {
		return 4 + this.itemData.length;
	}

}
