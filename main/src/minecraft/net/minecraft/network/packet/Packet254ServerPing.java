package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet254ServerPing extends Packet {
	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleServerPing(this);
	}

	public int getPacketSize() {
		return 0;
	}
}
