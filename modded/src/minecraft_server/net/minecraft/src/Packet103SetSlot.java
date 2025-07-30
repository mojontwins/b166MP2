package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet103SetSlot extends Packet {
	public int windowId;
	public int itemSlot;
	public ItemStack myItemStack;

	public Packet103SetSlot() {
	}

	public Packet103SetSlot(int i1, int i2, ItemStack itemStack3) {
		this.windowId = i1;
		this.itemSlot = i2;
		this.myItemStack = itemStack3 == null ? itemStack3 : itemStack3.copy();
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_20003_a(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
		this.itemSlot = dataInputStream1.readShort();
		short s2 = dataInputStream1.readShort();
		if(s2 >= 0) {
			byte b3 = dataInputStream1.readByte();
			short s4 = dataInputStream1.readShort();
			this.myItemStack = new ItemStack(s2, b3, s4);
		} else {
			this.myItemStack = null;
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
		dataOutputStream1.writeShort(this.itemSlot);
		if(this.myItemStack == null) {
			dataOutputStream1.writeShort(-1);
		} else {
			dataOutputStream1.writeShort(this.myItemStack.itemID);
			dataOutputStream1.writeByte(this.myItemStack.stackSize);
			dataOutputStream1.writeShort(this.myItemStack.getItemDamage());
		}

	}

	public int getPacketSize() {
		return 8;
	}
}
