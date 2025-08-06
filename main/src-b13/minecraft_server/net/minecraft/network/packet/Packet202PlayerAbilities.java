package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.entity.player.PlayerCapabilities;

public class Packet202PlayerAbilities extends Packet {
	public boolean disableDamage = false;
	public boolean isFlying = false;
	public boolean allowFlying = false;
	public boolean isCreativeMode = false;

	public Packet202PlayerAbilities() {
	}

	public Packet202PlayerAbilities(PlayerCapabilities playerCapabilities1) {
		this.disableDamage = playerCapabilities1.disableDamage;
		this.isFlying = playerCapabilities1.isFlying;
		this.allowFlying = playerCapabilities1.allowFlying;
		this.isCreativeMode = playerCapabilities1.isCreativeMode;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.disableDamage = dataInputStream1.readBoolean();
		this.isFlying = dataInputStream1.readBoolean();
		this.allowFlying = dataInputStream1.readBoolean();
		this.isCreativeMode = dataInputStream1.readBoolean();
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeBoolean(this.disableDamage);
		dataOutputStream1.writeBoolean(this.isFlying);
		dataOutputStream1.writeBoolean(this.allowFlying);
		dataOutputStream1.writeBoolean(this.isCreativeMode);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.func_50100_a(this);
	}

	public int getPacketSize() {
		return 1;
	}
}
