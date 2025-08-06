package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.network.NetHandler;
import net.minecraft.world.item.ItemStack;

public class Packet104WindowItems extends Packet {
	public int windowId;
	public ItemStack[] itemStack;

	public Packet104WindowItems() {
	}

	public Packet104WindowItems(int i1, List<ItemStack> list2) {
		this.windowId = i1;
		this.itemStack = new ItemStack[list2.size()];

		for(int i3 = 0; i3 < this.itemStack.length; ++i3) {
			ItemStack itemStack4 = (ItemStack)list2.get(i3);
			this.itemStack[i3] = itemStack4 == null ? null : itemStack4.copy();
		}

	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
		short s2 = dataInputStream1.readShort();
		this.itemStack = new ItemStack[s2];

		for(int i3 = 0; i3 < s2; ++i3) {
			this.itemStack[i3] = this.readItemStack(dataInputStream1);
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
		dataOutputStream1.writeShort(this.itemStack.length);

		for(int i2 = 0; i2 < this.itemStack.length; ++i2) {
			this.writeItemStack(this.itemStack[i2], dataOutputStream1);
		}

	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleWindowItems(this);
	}

	public int getPacketSize() {
		return 3 + this.itemStack.length * 5;
	}
}
