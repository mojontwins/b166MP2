package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.item.ItemStack;

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
		netHandler1.handleSetSlot(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
		this.itemSlot = dataInputStream1.readShort();
		this.myItemStack = this.readItemStack(dataInputStream1);
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
		dataOutputStream1.writeShort(this.itemSlot);
		this.writeItemStack(this.myItemStack, dataOutputStream1);
	}

	public int getPacketSize() {
		return 8;
	}
}
