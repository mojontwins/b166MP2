package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet54PlayNoteBlock extends Packet {
	public int xLocation;
	public int yLocation;
	public int zLocation;
	public int instrumentType;
	public int pitch;

	public Packet54PlayNoteBlock() {
	}

	public Packet54PlayNoteBlock(int i1, int i2, int i3, int i4, int i5) {
		this.xLocation = i1;
		this.yLocation = i2;
		this.zLocation = i3;
		this.instrumentType = i4;
		this.pitch = i5;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xLocation = dataInputStream1.readInt();
		this.yLocation = dataInputStream1.readShort();
		this.zLocation = dataInputStream1.readInt();
		this.instrumentType = dataInputStream1.read();
		this.pitch = dataInputStream1.read();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xLocation);
		dataOutputStream1.writeShort(this.yLocation);
		dataOutputStream1.writeInt(this.zLocation);
		dataOutputStream1.write(this.instrumentType);
		dataOutputStream1.write(this.pitch);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_21004_a(this);
	}

	public int getPacketSize() {
		return 12;
	}
}
