package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.entity.Entity;

public class Packet17Sleep extends Packet {
	public int entityID;
	public int bedX;
	public int bedY;
	public int bedZ;
	public int field_22046_e;

	public Packet17Sleep() {
	}

	public Packet17Sleep(Entity entity1, int i2, int i3, int i4, int i5) {
		this.field_22046_e = i2;
		this.bedX = i3;
		this.bedY = i4;
		this.bedZ = i5;
		this.entityID = entity1.entityId;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityID = dataInputStream1.readInt();
		this.field_22046_e = dataInputStream1.readByte();
		this.bedX = dataInputStream1.readInt();
		this.bedY = dataInputStream1.readByte();
		this.bedZ = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityID);
		dataOutputStream1.writeByte(this.field_22046_e);
		dataOutputStream1.writeInt(this.bedX);
		dataOutputStream1.writeByte(this.bedY);
		dataOutputStream1.writeInt(this.bedZ);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleSleep(this);
	}

	public int getPacketSize() {
		return 14;
	}
}
