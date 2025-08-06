package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.level.World;

public class Packet53BlockChange extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int type;
	public int metadata;

	public Packet53BlockChange() {
		this.isChunkDataPacket = true;
	}

	public Packet53BlockChange(int x, int y, int z, World world) {
		this.isChunkDataPacket = true;
		this.xPosition = x;
		this.yPosition = y;
		this.zPosition = z;
		this.type = world.getBlockId(x, y, z);
		this.metadata = world.getBlockMetadata(x, y, z);
	}

	public void readPacketData(DataInputStream dis) throws IOException {
		this.xPosition = dis.readInt();
		this.yPosition = dis.read();
		this.zPosition = dis.readInt();
		this.type = dis.readShort();
		this.metadata = dis.read();
	}

	public void writePacketData(DataOutputStream dos) throws IOException {
		dos.writeInt(this.xPosition);
		dos.write(this.yPosition);
		dos.writeInt(this.zPosition);
		dos.writeShort(this.type);
		dos.write(this.metadata);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleBlockChange(this);
	}

	public int getPacketSize() {
		return 12;
	}
}
