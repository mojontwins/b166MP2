package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet3Chat extends Packet {
	public static int maxStringLength = 256;
	public String message;
	
	public boolean mouseValid;
	public int mouseX, mouseY, mouseZ;

	public Packet3Chat() {
	}

	public Packet3Chat(String string1) {
		if(string1.length() > maxStringLength) {
			string1 = string1.substring(0, maxStringLength);
		}

		this.message = string1;
		this.mouseValid = false;
	}

	public Packet3Chat(String command, boolean mouseValid, int mouseX, int mouseY, int mouseZ) {
		this(command);
		this.mouseValid = mouseValid;
		this.mouseX = mouseX;
		this.mouseY = mouseY;
		this.mouseZ = mouseZ;
	}
	
	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.message = readString(dataInputStream1, maxStringLength);
		
		this.mouseValid = dataInputStream1.readBoolean();
		this.mouseX = dataInputStream1.readInt();
		this.mouseY = dataInputStream1.readInt();
		this.mouseZ = dataInputStream1.readInt();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		writeString(this.message, dataOutputStream1);
		
		dataOutputStream1.writeBoolean(this.mouseValid);
		dataOutputStream1.writeInt(this.mouseX);
		dataOutputStream1.writeInt(this.mouseY);
		dataOutputStream1.writeInt(this.mouseZ);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleChat(this);
	}

	public int getPacketSize() {
		return 2 + this.message.length() * 2 + 1 + 4 + 4 + 4;
	}
}
