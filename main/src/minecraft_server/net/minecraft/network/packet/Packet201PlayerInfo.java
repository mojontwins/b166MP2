package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet201PlayerInfo extends Packet {
	public String playerName;
	public boolean isConnected;
	public int ping;

	public Packet201PlayerInfo() {
	}

	public Packet201PlayerInfo(String string1, boolean z2, int i3) {
		this.playerName = string1;
		this.isConnected = z2;
		this.ping = i3;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.playerName = readString(dataInputStream1, 16);
		this.isConnected = dataInputStream1.readByte() != 0;
		this.ping = dataInputStream1.readShort();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		writeString(this.playerName, dataOutputStream1);
		dataOutputStream1.writeByte(this.isConnected ? 1 : 0);
		dataOutputStream1.writeShort(this.ping);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handlePlayerInfo(this);
	}

	public int getPacketSize() {
		return this.playerName.length() + 2 + 1 + 2;
	}
}
