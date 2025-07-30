package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet43Experience extends Packet {
	public float experience;
	public int experienceTotal;
	public int experienceLevel;

	public Packet43Experience() {
	}

	public Packet43Experience(float f1, int i2, int i3) {
		this.experience = f1;
		this.experienceTotal = i2;
		this.experienceLevel = i3;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.experience = dataInputStream1.readFloat();
		this.experienceLevel = dataInputStream1.readShort();
		this.experienceTotal = dataInputStream1.readShort();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeFloat(this.experience);
		dataOutputStream1.writeShort(this.experienceLevel);
		dataOutputStream1.writeShort(this.experienceTotal);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleExperience(this);
	}

	public int getPacketSize() {
		return 4;
	}
}
