package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet50PreChunk extends Packet {
	public int xPosition;
	public int yPosition;
	public boolean mode;

	public Packet50PreChunk() {
		this.isChunkDataPacket = false;
	}

	public Packet50PreChunk(int i1, int i2, boolean z3) {
		this.isChunkDataPacket = false;
		this.xPosition = i1;
		this.yPosition = i2;
		this.mode = z3;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.readInt();
		this.mode = dataInputStream1.read() != 0;
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.writeInt(this.yPosition);
		dataOutputStream1.write(this.mode ? 1 : 0);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handlePreChunk(this);
	}

	public int getPacketSize() {
		return 9;
	}
}
