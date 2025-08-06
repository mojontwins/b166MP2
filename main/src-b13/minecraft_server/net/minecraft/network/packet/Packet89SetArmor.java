package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet89SetArmor extends Packet {
	public int entityId;
	public byte armorType;
	public short itemId;
	
	public Packet89SetArmor() {
	}
	
	public Packet89SetArmor(int entityId, int armorType, int itemId) {
		this.entityId = entityId;
		this.armorType = (byte) armorType;
		this.itemId = (short) itemId;
	}
	
	@Override
	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.armorType = dataInputStream1.readByte();
		this.itemId = dataInputStream1.readShort();
	}

	@Override
	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeByte(this.armorType);
		dataOutputStream1.writeShort(this.itemId);
	}

	@Override
	public void processPacket(NetHandler netHandler) {
		netHandler.handleSetArmor(this);
	}

	@Override
	public int getPacketSize() {
		return 4+1+2;
	}

}
