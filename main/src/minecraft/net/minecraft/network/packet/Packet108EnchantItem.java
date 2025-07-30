package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet108EnchantItem extends Packet {
	public int windowId;
	public int enchantment;

	public Packet108EnchantItem() {
	}

	public Packet108EnchantItem(int i1, int i2) {
		this.windowId = i1;
		this.enchantment = i2;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleEnchantItem(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
		this.enchantment = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
		dataOutputStream1.writeByte(this.enchantment);
	}

	public int getPacketSize() {
		return 2;
	}
}
