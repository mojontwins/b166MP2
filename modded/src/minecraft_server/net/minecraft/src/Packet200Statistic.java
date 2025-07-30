package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet200Statistic extends Packet {
	public int field_27041_a;
	public int field_27040_b;

	public Packet200Statistic() {
	}

	public Packet200Statistic(int i1, int i2) {
		this.field_27041_a = i1;
		this.field_27040_b = i2;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_27001_a(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.field_27041_a = dataInputStream1.readInt();
		this.field_27040_b = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.field_27041_a);
		dataOutputStream1.writeByte(this.field_27040_b);
	}

	public int getPacketSize() {
		return 6;
	}
}
