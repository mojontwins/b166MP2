package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.item.ItemStack;

public class Packet107CreativeSetSlot extends Packet {
	public int slot;
	public ItemStack itemStack;

	public Packet107CreativeSetSlot() {
	}

	public Packet107CreativeSetSlot(int i1, ItemStack itemStack2) {
		this.slot = i1;
		this.itemStack = itemStack2;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleCreativeSetSlot(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.slot = dataInputStream1.readShort();
		this.itemStack = this.readItemStack(dataInputStream1);
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeShort(this.slot);
		this.writeItemStack(this.itemStack, dataOutputStream1);
	}

	public int getPacketSize() {
		return 8;
	}
}
