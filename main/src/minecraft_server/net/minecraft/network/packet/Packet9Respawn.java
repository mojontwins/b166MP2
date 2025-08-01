package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.level.WorldType;

public class Packet9Respawn extends Packet {
	public int respawnDimension;
	public int difficulty;
	public int worldHeight;
	public int creativeMode;
	public WorldType terrainType;

	public Packet9Respawn() {
	}

	public Packet9Respawn(int i1, byte b2, WorldType worldType3, int i4, int i5) {
		this.respawnDimension = i1;
		this.difficulty = b2;
		this.worldHeight = i4;
		this.creativeMode = i5;
		this.terrainType = worldType3;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleRespawn(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.respawnDimension = dataInputStream1.readInt();
		this.difficulty = dataInputStream1.readByte();
		this.creativeMode = dataInputStream1.readByte();
		this.worldHeight = dataInputStream1.readShort();
		String string2 = readString(dataInputStream1, 16);
		this.terrainType = WorldType.parseWorldType(string2);
		if(this.terrainType == null) {
			this.terrainType = WorldType.DEFAULT;
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.respawnDimension);
		dataOutputStream1.writeByte(this.difficulty);
		dataOutputStream1.writeByte(this.creativeMode);
		dataOutputStream1.writeShort(this.worldHeight);
		System.out.println ("Writing terraintype [" + this.terrainType.toString() + "] L=" + this.terrainType.toString().length());
		writeString(this.terrainType.toString(), dataOutputStream1);
	}

	public int getPacketSize() {
		return 8 + this.terrainType.toString().length();
	}
}
