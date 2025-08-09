package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.entity.item.EntityMovingPiston;
import net.minecraft.world.level.tile.Block;

public class Packet88MovingPiston extends Packet {
	public int entityId;
	public int x, y, z;
	public byte data;
	public byte xmove, ymove, zmove;
	public boolean sticky;

	public Packet88MovingPiston() {
	}
	
	public Packet88MovingPiston(EntityMovingPiston movingPiston) {
		this.entityId = movingPiston.entityId;
		this.x = movingPiston.xstart;
		this.y = movingPiston.ystart;
		this.z = movingPiston.zstart;
		this.data = (byte) movingPiston.data;
		this.xmove = (byte) movingPiston.xmove;
		this.ymove = (byte) movingPiston.ymove;
		this.zmove = (byte) movingPiston.zmove;
		this.sticky = movingPiston.blockID == Block.classicStickyPiston.blockID;

	}
	
	@Override
	public void readPacketData(DataInputStream dis) throws IOException {
		this.entityId = dis.readInt();
		this.x = dis.readInt();
		this.y = dis.readInt();
		this.z = dis.readInt();
		this.data = dis.readByte();
		this.xmove = dis.readByte();
		this.ymove = dis.readByte();
		this.zmove = dis.readByte();
		this.sticky = dis.readBoolean();
	}

	@Override
	public void writePacketData(DataOutputStream dos) throws IOException {
		dos.writeInt(this.entityId);
		dos.writeInt(this.x);
		dos.writeInt(this.y);
		dos.writeInt(this.z);
		dos.writeByte(this.data);;
		dos.writeByte(this.xmove);
		dos.writeByte(this.ymove);
		dos.writeByte(this.zmove);
		dos.writeBoolean(this.sticky);
	}

	@Override
	public void processPacket(NetHandler netHandler) {
		netHandler.handleMovingPiston(this);
	}

	@Override
	public int getPacketSize() {
		return 21;
	}

}
