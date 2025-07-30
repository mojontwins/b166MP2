package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet8UpdateHealth extends Packet {
	public int healthMP;
	public int food;
	public float foodSaturation;

	public Packet8UpdateHealth() {
	}

	public Packet8UpdateHealth(int i1, int i2, float f3) {
		this.healthMP = i1;
		this.food = i2;
		this.foodSaturation = f3;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.healthMP = dataInputStream1.readShort();
		this.food = dataInputStream1.readShort();
		this.foodSaturation = dataInputStream1.readFloat();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeShort(this.healthMP);
		dataOutputStream1.writeShort(this.food);
		dataOutputStream1.writeFloat(this.foodSaturation);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleUpdateHealth(this);
	}

	public int getPacketSize() {
		return 8;
	}
}
