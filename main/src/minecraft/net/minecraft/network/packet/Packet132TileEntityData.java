package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet132TileEntityData extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int actionType;
	public int customParam1;
	public int customParam2;
	public int customParam3;

	public Packet132TileEntityData() {
		this.isChunkDataPacket = true;
	}

	public Packet132TileEntityData(int i1, int i2, int i3, int i4, int i5) {
		this.isChunkDataPacket = true;
		this.xPosition = i1;
		this.yPosition = i2;
		this.zPosition = i3;
		this.actionType = i4;
		this.customParam1 = i5;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.readShort();
		this.zPosition = dataInputStream1.readInt();
		this.actionType = dataInputStream1.readByte();
		this.customParam1 = dataInputStream1.readInt();
		this.customParam2 = dataInputStream1.readInt();
		this.customParam3 = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.writeShort(this.yPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.writeByte((byte)this.actionType);
		dataOutputStream1.writeInt(this.customParam1);
		dataOutputStream1.writeInt(this.customParam2);
		dataOutputStream1.writeInt(this.customParam3);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleTileEntityData(this);
	}

	public int getPacketSize() {
		return 25;
	}
}
