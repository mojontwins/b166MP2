package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet250CustomPayload extends Packet {
	public String channel;
	public int length;
	public byte[] data;

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.channel = readString(dataInputStream1, 16);
		this.length = dataInputStream1.readShort();
		if(this.length > 0 && this.length < 32767) {
			this.data = new byte[this.length];
			dataInputStream1.readFully(this.data);
		}

	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		writeString(this.channel, dataOutputStream1);
		dataOutputStream1.writeShort((short)this.length);
		if(this.data != null) {
			dataOutputStream1.write(this.data);
		}

	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleCustomPayload(this);
	}

	public int getPacketSize() {
		return 2 + this.channel.length() * 2 + 2 + this.length;
	}
}
