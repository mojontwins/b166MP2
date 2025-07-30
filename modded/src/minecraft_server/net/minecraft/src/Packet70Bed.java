package net.minecraft.src;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Packet70Bed extends Packet {
	public static final String[] field_25016_a = new String[]{"tile.bed.notValid", null, null};
	public int field_25015_b;

	public Packet70Bed() {
	}

	public Packet70Bed(int i1) {
		this.field_25015_b = i1;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.field_25015_b = dataInputStream1.readByte();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.field_25015_b);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_25001_a(this);
	}

	public int getPacketSize() {
		return 1;
	}
}
