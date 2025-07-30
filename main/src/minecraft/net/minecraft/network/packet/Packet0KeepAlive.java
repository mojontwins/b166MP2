package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet0KeepAlive extends Packet {
	public int randomId;

	public Packet0KeepAlive() {
	}

	public Packet0KeepAlive(int i1) {
		this.randomId = i1;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleKeepAlive(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.randomId = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.randomId);
	}

	public int getPacketSize() {
		return 4;
	}
}
