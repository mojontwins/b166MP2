package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet70GameEvent extends Packet {
	public static final String[] bedChat = new String[]{"tile.bed.notValid", null, null, "gameMode.changed"};
	public int bedState;
	public int gameMode;

	public boolean raining;
	public boolean snowing;
	public boolean thundering;

	public Packet70GameEvent() {
	}

	public Packet70GameEvent(int state, int gameMode) {
		this.bedState = state;
		this.gameMode = gameMode;
	}
	
	public Packet70GameEvent(boolean raining, boolean snowing, boolean thundering) {
		this.raining = raining;
		this.snowing = snowing;
		this.thundering = thundering;
		this.bedState = 1;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.bedState = dataInputStream1.readByte();
		this.gameMode = dataInputStream1.readByte();
		
		this.raining = dataInputStream1.readBoolean();
		this.snowing = dataInputStream1.readBoolean();
		this.thundering = dataInputStream1.readBoolean();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.bedState);
		dataOutputStream1.writeByte(this.gameMode);
		
		dataOutputStream1.writeBoolean(this.raining);
		dataOutputStream1.writeBoolean(this.snowing);
		dataOutputStream1.writeBoolean(this.thundering);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleBed(this);
	}

	public int getPacketSize() {
		return 2 + 3;
	}
}
