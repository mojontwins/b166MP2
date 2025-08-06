package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;

public class Packet95UpdateDayOfTheYear extends Packet {
	public int dayOfTheYear = 0;
	
	public Packet95UpdateDayOfTheYear() {
	}
	
	public Packet95UpdateDayOfTheYear(int dayOfTheYear) {
		this.dayOfTheYear = dayOfTheYear;
	}

	@Override
	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.dayOfTheYear = dataInputStream1.readByte();
	}

	@Override
	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeByte(this.dayOfTheYear);
	}

	@Override
	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleUpdateDayOfTheYear(this);
	}

	@Override
	public int getPacketSize() {
		return 1;
	}

}
