package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.network.DataWatcher;
import net.minecraft.network.NetHandler;
import net.minecraft.network.WatchableObject;
import net.minecraft.src.MathHelper;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.monster.EntityArmoredMob;
import net.minecraft.world.item.ItemStack;

public class Packet90ArmoredMobSpawn extends Packet {
	public int entityId;
	public int type;
	public int xPosition;
	public int yPosition;
	public int zPosition;
	public byte yaw;
	public byte pitch;
	public byte yawHead;
	private DataWatcher metaData;
	private List<WatchableObject> receivedMetadata;
	public ItemStack[] inventory = new ItemStack[5];

	public Packet90ArmoredMobSpawn() {
	}

	public Packet90ArmoredMobSpawn(EntityArmoredMob theMob) {
		this.entityId = theMob.entityId;
		this.type = (byte)EntityList.getEntityID(theMob);
		this.xPosition = MathHelper.floor_double(theMob.posX * 32.0D);
		this.yPosition = MathHelper.floor_double(theMob.posY * 32.0D);
		this.zPosition = MathHelper.floor_double(theMob.posZ * 32.0D);
		this.yaw = (byte)((int)(theMob.rotationYaw * 256.0F / 360.0F));
		this.pitch = (byte)((int)(theMob.rotationPitch * 256.0F / 360.0F));
		this.yawHead = (byte)((int)(theMob.rotationYawHead * 256.0F / 360.0F));
		this.metaData = theMob.getDataWatcher();
		for(int i = 0; i < 5; i ++) {
			ItemStack stack = theMob.inventory.getStackInSlot(i);
			this.inventory[i] = stack == null ? null : stack.copy();
		}
	}

	public void readPacketData(DataInputStream dataInputStream1) throws IOException {
		this.entityId = dataInputStream1.readInt();
		this.type = dataInputStream1.readByte() & 255;
		this.xPosition = dataInputStream1.readInt();
		this.yPosition = dataInputStream1.readInt();
		this.zPosition = dataInputStream1.readInt();
		this.yaw = dataInputStream1.readByte();
		this.pitch = dataInputStream1.readByte();
		this.yawHead = dataInputStream1.readByte();
		
		for(int i = 0; i < 5; i ++) {
			this.inventory[i] = this.readItemStack(dataInputStream1);
		}
		
		this.receivedMetadata = DataWatcher.readWatchableObjects(dataInputStream1);
		
	}

	public void writePacketData(DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.writeInt(this.entityId);
		dataOutputStream1.writeByte(this.type & 255);
		dataOutputStream1.writeInt(this.xPosition);
		dataOutputStream1.writeInt(this.yPosition);
		dataOutputStream1.writeInt(this.zPosition);
		dataOutputStream1.writeByte(this.yaw);
		dataOutputStream1.writeByte(this.pitch);
		dataOutputStream1.writeByte(this.yawHead);
		
		for(int i = 0; i < 5; i ++) {
			this.writeItemStack(this.inventory[i], dataOutputStream1);
		}
		
		this.metaData.writeWatchableObjects(dataOutputStream1);
	}

	public void processPacket(NetHandler netHandler) {
		netHandler.handleArmoredMobSpawn(this);
	}

	public int getPacketSize() {
		// Each itemstack is 5 bytes
		
		return 20 + 5 * 5;
	}

	public List<WatchableObject> getMetadata() {
		return this.receivedMetadata;
	}
}
