package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.level.WorldType;

public class Packet1Login extends Packet {
	public int protocolVersion;
	public String username;
	public WorldType terrainType;
	public int serverMode;
	public int dimension;
	public byte difficultySetting;
	public byte worldHeight;
	public byte maxPlayers;
	public boolean enableSeasons;

	public Packet1Login() {
	}

	public Packet1Login(String string1, int i2) {
		this.username = string1;
		this.protocolVersion = i2;
	}

	public Packet1Login(String username, int protocolVersion, WorldType terrainType, int gameMode, int dimension, byte difficulty, byte worldHeight, byte maxPlayers, boolean enableSeasons) {
		this.username = username;
		this.protocolVersion = protocolVersion;
		this.terrainType = terrainType;
		this.dimension = dimension;
		this.difficultySetting = difficulty;
		this.serverMode = gameMode;
		this.worldHeight = worldHeight;
		this.maxPlayers = maxPlayers;
		this.enableSeasons = enableSeasons;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.protocolVersion = dataInputStream1.readInt();
		this.username = readString(dataInputStream1, 16);
		String string2 = readString(dataInputStream1, 16);
		this.terrainType = WorldType.parseWorldType(string2);
		if(this.terrainType == null) {
			this.terrainType = WorldType.DEFAULT;
		}

		this.serverMode = dataInputStream1.readInt();
		this.dimension = dataInputStream1.readInt();
		this.difficultySetting = dataInputStream1.readByte();
		this.worldHeight = dataInputStream1.readByte();
		this.maxPlayers = dataInputStream1.readByte();
		this.enableSeasons = dataInputStream1.readBoolean();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.protocolVersion);
		writeString(this.username, dataOutputStream1);
		if(this.terrainType == null) {
			writeString("", dataOutputStream1);
		} else {
			writeString(this.terrainType.getWorldTypeName(), dataOutputStream1);
		}

		dataOutputStream1.writeInt(this.serverMode);
		dataOutputStream1.writeInt(this.dimension);
		dataOutputStream1.writeByte(this.difficultySetting);
		dataOutputStream1.writeByte(this.worldHeight);
		dataOutputStream1.writeByte(this.maxPlayers);
		dataOutputStream1.writeBoolean(this.enableSeasons);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handleLogin(this);
	}

	public int getPacketSize() {
		int i1 = 0;
		if(this.terrainType != null) {
			i1 = this.terrainType.getWorldTypeName().length();
		}

		return 4 + this.username.length() + 4 + 7 + 7 + i1 + 1;
	}
}
