package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet53BlockChange extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int type;
	public int metadata;

	public Packet53BlockChange() {
		this.isChunkDataPacket = true;
	}

	public Packet53BlockChange(int i1, int i2, int i3, World world4) {
		this.isChunkDataPacket = true;
		this.xPosition = i1;
		this.yPosition = i2;
		this.zPosition = i3;
		this.type = world4.getBlockId(i1, i2, i3);
		this.metadata = world4.getBlockMetadata(i1, i2, i3);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.read();
		this.zPosition = dataInputStream1.readInt();
		this.type = dataInputStream1.read();
		this.metadata = dataInputStream1.read();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.write(this.yPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.write(this.type);
		dataOutputStream1.write(this.metadata);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleBlockChange(this);
	}

	public int getPacketSize() {
		return 11;
	}
}
