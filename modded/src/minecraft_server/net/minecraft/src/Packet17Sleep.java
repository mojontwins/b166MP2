package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet17Sleep extends Packet {
	public int field_22041_a;
	public int field_22040_b;
	public int field_22044_c;
	public int field_22043_d;
	public int field_22042_e;

	public Packet17Sleep() {
	}

	public Packet17Sleep(Entity entity1, int i2, int i3, int i4, int i5) {
		this.field_22042_e = i2;
		this.field_22040_b = i3;
		this.field_22044_c = i4;
		this.field_22043_d = i5;
		this.field_22041_a = entity1.entityId;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.field_22041_a = dataInputStream1.readInt();
		this.field_22042_e = dataInputStream1.readByte();
		this.field_22040_b = dataInputStream1.readInt();
		this.field_22044_c = dataInputStream1.readByte();
		this.field_22043_d = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.field_22041_a);
		dataOutputStream1.writeByte(this.field_22042_e);
		dataOutputStream1.writeInt(this.field_22040_b);
		dataOutputStream1.writeByte(this.field_22044_c);
		dataOutputStream1.writeInt(this.field_22043_d);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_22002_a(this);
	}

	public int getPacketSize() {
		return 14;
	}
}
