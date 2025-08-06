package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityLightningBolt;

public class Packet71Weather extends Packet {
	public int entityID;
	public int posX;
	public int posY;
	public int posZ;
	public int isLightningBolt;

	public Packet71Weather() {
	}

	public Packet71Weather(Entity entity1) {
		this.entityID = entity1.entityId;
		this.posX = MathHelper.floor_double(entity1.posX * 32.0D);
		this.posY = MathHelper.floor_double(entity1.posY * 32.0D);
		this.posZ = MathHelper.floor_double(entity1.posZ * 32.0D);
		if(entity1 instanceof EntityLightningBolt) {
			this.isLightningBolt = 1;
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityID = dataInputStream1.readInt();
		this.isLightningBolt = dataInputStream1.readByte();
		this.posX = dataInputStream1.readInt();
		this.posY = dataInputStream1.readInt();
		this.posZ = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityID);
		dataOutputStream1.writeByte(this.isLightningBolt);
		dataOutputStream1.writeInt(this.posX);
		dataOutputStream1.writeInt(this.posY);
		dataOutputStream1.writeInt(this.posZ);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleWeather(this);
	}

	public int getPacketSize() {
		return 17;
	}
}
