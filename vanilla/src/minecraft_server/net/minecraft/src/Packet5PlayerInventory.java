package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet5PlayerInventory extends Packet {
	public int entityID;
	public int slot;
	public int itemID;
	public int itemDamage;

	public Packet5PlayerInventory() {
	}

	public Packet5PlayerInventory(int i1, int i2, ItemStack itemStack3) {
		this.entityID = i1;
		this.slot = i2;
		if(itemStack3 == null) {
			this.itemID = -1;
			this.itemDamage = 0;
		} else {
			this.itemID = itemStack3.itemID;
			this.itemDamage = itemStack3.getItemDamage();
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityID = dataInputStream1.readInt();
		this.slot = dataInputStream1.readShort();
		this.itemID = dataInputStream1.readShort();
		this.itemDamage = dataInputStream1.readShort();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityID);
		dataOutputStream1.writeShort(this.slot);
		dataOutputStream1.writeShort(this.itemID);
		dataOutputStream1.writeShort(this.itemDamage);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handlePlayerInventory(this);
	}

	public int getPacketSize() {
		return 8;
	}
}
