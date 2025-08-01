package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;

public class Packet34EntityTeleport extends Packet {
	public int entityId;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public byte yaw;
	public byte pitch;

	public Packet34EntityTeleport() {
	}

	public Packet34EntityTeleport(Entity entity1) {
		this.entityId = entity1.entityId;
		this.xPosition = MathHelper.floor_double(entity1.posX * 32.0D);
		this.yPosition = MathHelper.floor_double(entity1.posY * 32.0D);
		this.zPosition = MathHelper.floor_double(entity1.posZ * 32.0D);
		this.yaw = (byte)((int)(entity1.rotationYaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(entity1.rotationPitch * 256.0F / 360.0F));
	}

	public Packet34EntityTeleport(int i1, int i2, int i3, int i4, byte b5, byte b6) {
		this.entityId = i1;
		this.xPosition = i2;
		this.yPosition = i3;
		this.zPosition = i4;
		this.yaw = b5;
		this.pitch = b6;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.readInt();
		this.zPosition = dataInputStream1.readInt();
		this.yaw = (byte)dataInputStream1.read();
		this.pitch = (byte)dataInputStream1.read();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.writeInt(this.yPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.write(this.yaw);
		dataOutputStream1.write(this.pitch);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleEntityTeleport(this);
	}

	public int getPacketSize() {
		return 34;
	}
}
