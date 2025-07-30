package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet61DoorChange extends Packet {
	public int sfxID;
	public int auxData;
	public int posX;
	public int posY;
	public int posZ;

	public Packet61DoorChange() {
	}

	public Packet61DoorChange(int i1, int i2, int i3, int i4, int i5) {
		this.sfxID = i1;
		this.posX = i2;
		this.posY = i3;
		this.posZ = i4;
		this.auxData = i5;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.sfxID = dataInputStream1.readInt();
		this.posX = dataInputStream1.readInt();
		this.posY = dataInputStream1.readByte() & 255;
		this.posZ = dataInputStream1.readInt();
		this.auxData = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.sfxID);
		dataOutputStream1.writeInt(this.posX);
		dataOutputStream1.writeByte(this.posY & 255);
		dataOutputStream1.writeInt(this.posZ);
		dataOutputStream1.writeInt(this.auxData);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleDoorChange(this);
	}

	public int getPacketSize() {
		return 20;
	}
}
