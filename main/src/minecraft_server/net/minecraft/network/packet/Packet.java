package net.minecraft.network.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.mojang.nbt.CompressedStreamTools;
import com.mojang.nbt.NBTTagCompound;

import net.minecraft.network.NetHandler;
import net.minecraft.world.IntHashMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class Packet {
	public static IntHashMap packetIdToClassMap = new IntHashMap();
	private static Map<Class<?>, Integer> packetClassToIdMap = new HashMap<Class<?>, Integer>();
	private static Set<Integer> clientPacketIdList = new HashSet<Integer>();
	private static Set<Integer> serverPacketIdList = new HashSet<Integer>();
	public final long creationTimeMillis = System.currentTimeMillis();
	public static long packetsRead;
	public static long bytesRead;
	public static long packetsWritten;
	public static long bytesWritten;
	public boolean isChunkDataPacket = false;

	static void addIdClassMapping(int i0, boolean z1, boolean z2, Class<?> class3) {
		if(packetIdToClassMap.containsItem(i0)) {
			throw new IllegalArgumentException("Duplicate packet id:" + i0);
		} else if(packetClassToIdMap.containsKey(class3)) {
			throw new IllegalArgumentException("Duplicate packet class:" + class3);
		} else {
			packetIdToClassMap.addKey(i0, class3);
			packetClassToIdMap.put(class3, i0);
			if(z1) {
				clientPacketIdList.add(i0);
			}

			if(z2) {
				serverPacketIdList.add(i0);
			}

		}
	}

	public static Packet getNewPacket(int packetID) {
		try {
			Class<?> clazz = (Class<?>)packetIdToClassMap.lookup(packetID);
			return clazz == null ? null : (Packet)clazz.getConstructor().newInstance();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Skipping packet with id " + packetID);
			return null;
		}
	}

	public final int getPacketId() {
		return ((Integer)packetClassToIdMap.get(this.getClass())).intValue();
	}

	public static Packet readPacket(DataInputStream dis, boolean isServer) throws IOException {
		Packet packet = null;

		int packetID;
		try {
			packetID = dis.read();
			if(packetID == -1) {
				return null;
			}

			if(isServer && !serverPacketIdList.contains(packetID) || !isServer && !clientPacketIdList.contains(packetID)) {
				throw new IOException("Bad packet id " + packetID);
			}

			packet = getNewPacket(packetID);
			if(packet == null) {
				throw new IOException("Bad packet id " + packetID);
			}

			packet.readPacketData(dis);
			++packetsRead;
			bytesRead += (long)packet.getPacketSize();
		} catch (EOFException eOFException5) {
			System.out.println("Reached end of stream");
			return null;
		}

		PacketCount.countPacket(packetID, (long)packet.getPacketSize());
		++packetsRead;
		bytesRead += (long)packet.getPacketSize();
		return packet;
	}

	public static void writePacket(Packet packet0, DataOutputStream dataOutputStream1) throws IOException {
		dataOutputStream1.write(packet0.getPacketId());
		packet0.writePacketData(dataOutputStream1);
		++packetsWritten;
		bytesWritten += (long)packet0.getPacketSize();
	}

	public static void writeString(String string0, DataOutputStream dataOutputStream1) throws IOException {
		if(string0.length() > 32767) {
			throw new IOException("String too big");
		} else {
			dataOutputStream1.writeShort(string0.length());
			dataOutputStream1.writeChars(string0);
		}
	}

	public static String readString(DataInputStream dataInputStream0, int i1) throws IOException {
		short s2 = dataInputStream0.readShort();
		if(s2 > i1) {
			throw new IOException("Received string length longer than maximum allowed (" + s2 + " > " + i1 + ")");
		} else if(s2 < 0) {
			throw new IOException("Received string length is less than zero! Weird string!");
		} else {
			StringBuilder stringBuilder3 = new StringBuilder();

			for(int i4 = 0; i4 < s2; ++i4) {
				stringBuilder3.append(dataInputStream0.readChar());
			}

			return stringBuilder3.toString();
		}
	}

	public abstract void readPacketData(DataInputStream dis) throws IOException;

	public abstract void writePacketData(DataOutputStream dataOutputStream1) throws IOException;

	public abstract void processPacket(NetHandler netHandler1);

	public abstract int getPacketSize();

	protected ItemStack readItemStack(DataInputStream dis) throws IOException {
		ItemStack theStack = null;
		short itemID = dis.readShort();
		if(itemID >= 0) {
			byte size = dis.readByte();
			short damage = dis.readShort();
			theStack = new ItemStack(itemID, size, damage);
			if(Item.itemsList[itemID].isDamageable() || Item.itemsList[itemID].hasMetadata()) {
				theStack.stackTagCompound = this.readNBTTagCompound(dis);
			}
		}

		return theStack;
	}

	protected void writeItemStack(ItemStack theStack, DataOutputStream dos) throws IOException {
		if(theStack == null) {
			dos.writeShort(-1);
		} else {
			dos.writeShort(theStack.itemID);
			dos.writeByte(theStack.stackSize);
			dos.writeShort(theStack.getItemDamage());
			if(theStack.getItem().isDamageable() || theStack.getItem().hasMetadata()) {
				this.writeNBTTagCompound(theStack.stackTagCompound, dos);
			}
		}

	}

	protected NBTTagCompound readNBTTagCompound(DataInputStream dis) throws IOException {
		short s2 = dis.readShort();
		if(s2 < 0) {
			return null;
		} else {
			byte[] b3 = new byte[s2];
			dis.readFully(b3);
			return CompressedStreamTools.decompress(b3);
		}
	}

	protected void writeNBTTagCompound(NBTTagCompound compoundTag, DataOutputStream dataOutputStream2) throws IOException {
		if(compoundTag == null) {
			dataOutputStream2.writeShort(-1);
		} else {
			byte[] b3 = CompressedStreamTools.compress(compoundTag);
			dataOutputStream2.writeShort((short)b3.length);
			dataOutputStream2.write(b3);
		}

	}

	static {
		addIdClassMapping(0, true, true, Packet0KeepAlive.class);
		addIdClassMapping(1, true, true, Packet1Login.class);
		addIdClassMapping(2, true, true, Packet2Handshake.class);
		addIdClassMapping(3, true, true, Packet3Chat.class);
		addIdClassMapping(4, true, false, Packet4UpdateTime.class);
		addIdClassMapping(5, true, false, Packet5PlayerInventory.class);
		addIdClassMapping(6, true, false, Packet6SpawnPosition.class);
		addIdClassMapping(7, false, true, Packet7UseEntity.class);
		addIdClassMapping(8, true, false, Packet8UpdateHealth.class);
		addIdClassMapping(9, true, true, Packet9Respawn.class);
		addIdClassMapping(10, true, true, Packet10Flying.class);
		addIdClassMapping(11, true, true, Packet11PlayerPosition.class);
		addIdClassMapping(12, true, true, Packet12PlayerLook.class);
		addIdClassMapping(13, true, true, Packet13PlayerLookMove.class);
		addIdClassMapping(14, false, true, Packet14BlockDig.class);
		addIdClassMapping(15, false, true, Packet15Place.class);
		addIdClassMapping(16, false, true, Packet16BlockItemSwitch.class);
		addIdClassMapping(17, true, false, Packet17Sleep.class);
		addIdClassMapping(18, true, true, Packet18Animation.class);
		addIdClassMapping(19, false, true, Packet19EntityAction.class);
		addIdClassMapping(20, true, false, Packet20NamedEntitySpawn.class);
		addIdClassMapping(21, true, false, Packet21PickupSpawn.class);
		addIdClassMapping(22, true, false, Packet22Collect.class);
		addIdClassMapping(23, true, false, Packet23VehicleSpawn.class);
		addIdClassMapping(24, true, false, Packet24MobSpawn.class);
		addIdClassMapping(25, true, false, Packet25EntityPainting.class);
		addIdClassMapping(28, true, false, Packet28EntityVelocity.class);
		addIdClassMapping(29, true, false, Packet29DestroyEntity.class);
		addIdClassMapping(30, true, false, Packet30Entity.class);
		addIdClassMapping(31, true, false, Packet31RelEntityMove.class);
		addIdClassMapping(32, true, false, Packet32EntityLook.class);
		addIdClassMapping(33, true, false, Packet33RelEntityMoveLook.class);
		addIdClassMapping(34, true, false, Packet34EntityTeleport.class);
		addIdClassMapping(35, true, false, Packet35EntityHeadRotation.class);
		addIdClassMapping(38, true, false, Packet38EntityStatus.class);
		addIdClassMapping(39, true, false, Packet39AttachEntity.class);
		addIdClassMapping(40, true, false, Packet40EntityMetadata.class);
		addIdClassMapping(43, true, false, Packet43Experience.class);
		addIdClassMapping(50, true, false, Packet50PreChunk.class);
		addIdClassMapping(51, true, false, Packet51MapChunk.class);
		addIdClassMapping(52, true, false, Packet52MultiBlockChange.class);
		addIdClassMapping(53, true, false, Packet53BlockChange.class);
		addIdClassMapping(54, true, false, Packet54PlayNoteBlock.class);
		addIdClassMapping(60, true, false, Packet60Explosion.class);
		addIdClassMapping(61, true, false, Packet61DoorChange.class);
		addIdClassMapping(70, true, false, Packet70GameEvent.class);
		addIdClassMapping(71, true, false, Packet71Weather.class);
		addIdClassMapping(88, true, true, Packet88MovingPiston.class);
		addIdClassMapping(89, true, false, Packet89SetArmor.class);
		addIdClassMapping(90, true, false, Packet90ArmoredMobSpawn.class);
		addIdClassMapping(91, true, true, Packet91UpdateCommandBlock.class);
		addIdClassMapping(95, true, false, Packet95UpdateDayOfTheYear.class);
		addIdClassMapping(100, true, false, Packet100OpenWindow.class);
		addIdClassMapping(101, true, true, Packet101CloseWindow.class);
		addIdClassMapping(102, false, true, Packet102WindowClick.class);
		addIdClassMapping(103, true, false, Packet103SetSlot.class);
		addIdClassMapping(104, true, false, Packet104WindowItems.class);
		addIdClassMapping(105, true, false, Packet105UpdateProgressbar.class);
		addIdClassMapping(106, true, true, Packet106Transaction.class);
		addIdClassMapping(107, true, true, Packet107CreativeSetSlot.class);
		addIdClassMapping(108, false, true, Packet108EnchantItem.class);
		addIdClassMapping(130, true, true, Packet130UpdateSign.class);
		addIdClassMapping(131, true, false, Packet131MapData.class);
		addIdClassMapping(132, true, false, Packet132TileEntityData.class);
		addIdClassMapping(200, true, false, Packet200Statistic.class);
		addIdClassMapping(201, true, false, Packet201PlayerInfo.class);
		addIdClassMapping(202, true, true, Packet202PlayerAbilities.class);
		addIdClassMapping(250, true, true, Packet250CustomPayload.class);
		addIdClassMapping(254, false, true, Packet254ServerPing.class);
		addIdClassMapping(255, true, true, Packet255KickDisconnect.class);
	}
}
