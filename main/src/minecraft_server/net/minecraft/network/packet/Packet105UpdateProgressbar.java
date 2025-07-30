package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet105UpdateProgressbar extends Packet {
	public int windowId;
	public int progressBar;
	public int progressBarValue;

	public Packet105UpdateProgressbar() {
	}

	public Packet105UpdateProgressbar(int i1, int i2, int i3) {
		this.windowId = i1;
		this.progressBar = i2;
		this.progressBarValue = i3;
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleUpdateProgressbar(this);
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.windowId = dataInputStream1.readByte();
		this.progressBar = dataInputStream1.readShort();
		this.progressBarValue = dataInputStream1.readShort();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.windowId);
		dataOutputStream1.writeShort(this.progressBar);
		dataOutputStream1.writeShort(this.progressBarValue);
	}

	public int getPacketSize() {
		return 5;
	}
}
