package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet23VehicleSpawn extends Packet {
	public int entityId;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int field_28044_e;
	public int field_28043_f;
	public int field_28042_g;
	public int type;
	public int field_28041_i;

	public Packet23VehicleSpawn() {
	}

	public Packet23VehicleSpawn(Entity entity1, int i2) {
		this(entity1, i2, 0);
	}

	public Packet23VehicleSpawn(Entity entity1, int i2, int i3) {
		this.entityId = entity1.entityId;
		this.xPosition = MathHelper.floor_double(entity1.posX * 32.0D);
		this.yPosition = MathHelper.floor_double(entity1.posY * 32.0D);
		this.zPosition = MathHelper.floor_double(entity1.posZ * 32.0D);
		this.type = i2;
		this.field_28041_i = i3;
		if(i3 > 0) {
			double d4 = entity1.motionX;
			double d6 = entity1.motionY;
			double d8 = entity1.motionZ;
			double d10 = 3.9D;
			if(d4 < -d10) {
				d4 = -d10;
			}

			if(d6 < -d10) {
				d6 = -d10;
			}

			if(d8 < -d10) {
				d8 = -d10;
			}

			if(d4 > d10) {
				d4 = d10;
			}

			if(d6 > d10) {
				d6 = d10;
			}

			if(d8 > d10) {
				d8 = d10;
			}

			this.field_28044_e = (int)(d4 * 8000.0D);
			this.field_28043_f = (int)(d6 * 8000.0D);
			this.field_28042_g = (int)(d8 * 8000.0D);
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.type = dataInputStream1.readByte();
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.readInt();
		this.zPosition = dataInputStream1.readInt();
		this.field_28041_i = dataInputStream1.readInt();
		if(this.field_28041_i > 0) {
			this.field_28044_e = dataInputStream1.readShort();
			this.field_28043_f = dataInputStream1.readShort();
			this.field_28042_g = dataInputStream1.readShort();
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeByte(this.type);
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.writeInt(this.yPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.writeInt(this.field_28041_i);
		if(this.field_28041_i > 0) {
			dataOutputStream1.writeShort(this.field_28044_e);
			dataOutputStream1.writeShort(this.field_28043_f);
			dataOutputStream1.writeShort(this.field_28042_g);
		}

	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleVehicleSpawn(this);
	}

	public int getPacketSize() {
		return 21 + this.field_28041_i > 0 ? 6 : 0;
	}
}
