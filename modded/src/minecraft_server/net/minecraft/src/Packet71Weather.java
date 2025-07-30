package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet71Weather extends Packet {
	public int field_27043_a;
	public int field_27042_b;
	public int field_27046_c;
	public int field_27045_d;
	public int field_27044_e;

	public Packet71Weather() {
	}

	public Packet71Weather(Entity entity1) {
		this.field_27043_a = entity1.entityId;
		this.field_27042_b = MathHelper.floor_double(entity1.posX * 32.0D);
		this.field_27046_c = MathHelper.floor_double(entity1.posY * 32.0D);
		this.field_27045_d = MathHelper.floor_double(entity1.posZ * 32.0D);
		if(entity1 instanceof EntityLightningBolt) {
			this.field_27044_e = 1;
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.field_27043_a = dataInputStream1.readInt();
		this.field_27044_e = dataInputStream1.readByte();
		this.field_27042_b = dataInputStream1.readInt();
		this.field_27046_c = dataInputStream1.readInt();
		this.field_27045_d = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.field_27043_a);
		dataOutputStream1.writeByte(this.field_27044_e);
		dataOutputStream1.writeInt(this.field_27042_b);
		dataOutputStream1.writeInt(this.field_27046_c);
		dataOutputStream1.writeInt(this.field_27045_d);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_27002_a(this);
	}

	public int getPacketSize() {
		return 17;
	}
}
