package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.NetHandler;
import net.minecraft.world.item.ItemStack;

public class Packet15Place extends Packet {
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public int direction;
	public ItemStack itemStack;
	public float xWithinFace;
	public float yWithinFace;
	public float zWithinFace;
	public boolean keyPressed;

	public Packet15Place() {
	}

	public Packet15Place(int i1, int i2, int i3, int i4, ItemStack itemStack5, float xWithinFace, float yWithinFace, float zWithinFace, boolean keyPressed) {
		this.xPosition = i1;
		this.yPosition = i2;
		this.zPosition = i3;
		this.direction = i4;
		this.itemStack = itemStack5;
		this.xWithinFace = xWithinFace;
		this.yWithinFace = yWithinFace;
		this.zWithinFace = zWithinFace;
		this.keyPressed = keyPressed;
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.read();
		this.zPosition = dataInputStream1.readInt();
		this.direction = dataInputStream1.read();
		this.xWithinFace = dataInputStream1.readFloat();
		this.yWithinFace = dataInputStream1.readFloat();
		this.zWithinFace = dataInputStream1.readFloat();
		this.keyPressed = dataInputStream1.readBoolean();
		
		this.itemStack = this.readItemStack(dataInputStream1);
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.write(this.yPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.write(this.direction);
		dataOutputStream1.writeFloat(this.xWithinFace);
		dataOutputStream1.writeFloat(this.yWithinFace);
		dataOutputStream1.writeFloat(this.zWithinFace);
		dataOutputStream1.writeBoolean(this.keyPressed);
		
		this.writeItemStack(this.itemStack, dataOutputStream1);
	}

	public void processPacket(NetHandler netHandler1) {
		netHandler1.handlePlace(this);
	}

	public int getPacketSize() {
		// 4 + 1 + 4 + 1 + 4 + 4 + 4 + 1 = 23
		return 23;
	}
}
