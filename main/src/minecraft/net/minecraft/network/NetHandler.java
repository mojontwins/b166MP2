package net.minecraft.network;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet0KeepAlive;
import net.minecraft.network.packet.Packet100OpenWindow;
import net.minecraft.network.packet.Packet101CloseWindow;
import net.minecraft.network.packet.Packet102WindowClick;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.network.packet.Packet104WindowItems;
import net.minecraft.network.packet.Packet105UpdateProgressbar;
import net.minecraft.network.packet.Packet106Transaction;
import net.minecraft.network.packet.Packet107CreativeSetSlot;
import net.minecraft.network.packet.Packet108EnchantItem;
import net.minecraft.network.packet.Packet10Flying;
import net.minecraft.network.packet.Packet130UpdateSign;
import net.minecraft.network.packet.Packet131MapData;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.network.packet.Packet14BlockDig;
import net.minecraft.network.packet.Packet15Place;
import net.minecraft.network.packet.Packet16BlockItemSwitch;
import net.minecraft.network.packet.Packet17Sleep;
import net.minecraft.network.packet.Packet18Animation;
import net.minecraft.network.packet.Packet19EntityAction;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet200Statistic;
import net.minecraft.network.packet.Packet201PlayerInfo;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet20NamedEntitySpawn;
import net.minecraft.network.packet.Packet21PickupSpawn;
import net.minecraft.network.packet.Packet22Collect;
import net.minecraft.network.packet.Packet23VehicleSpawn;
import net.minecraft.network.packet.Packet24MobSpawn;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.network.packet.Packet254ServerPing;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet25EntityPainting;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.network.packet.Packet29DestroyEntity;
import net.minecraft.network.packet.Packet2Handshake;
import net.minecraft.network.packet.Packet30Entity;
import net.minecraft.network.packet.Packet34EntityTeleport;
import net.minecraft.network.packet.Packet35EntityHeadRotation;
import net.minecraft.network.packet.Packet38EntityStatus;
import net.minecraft.network.packet.Packet39AttachEntity;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet40EntityMetadata;
import net.minecraft.network.packet.Packet43Experience;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet50PreChunk;
import net.minecraft.network.packet.Packet51MapChunk;
import net.minecraft.network.packet.Packet52MultiBlockChange;
import net.minecraft.network.packet.Packet53BlockChange;
import net.minecraft.network.packet.Packet54PlayNoteBlock;
import net.minecraft.network.packet.Packet5PlayerInventory;
import net.minecraft.network.packet.Packet60Explosion;
import net.minecraft.network.packet.Packet61DoorChange;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet71Weather;
import net.minecraft.network.packet.Packet7UseEntity;
import net.minecraft.network.packet.Packet88MovingPiston;
import net.minecraft.network.packet.Packet89SetArmor;
import net.minecraft.network.packet.Packet8UpdateHealth;
import net.minecraft.network.packet.Packet90ArmoredMobSpawn;
import net.minecraft.network.packet.Packet91UpdateCommandBlock;
import net.minecraft.network.packet.Packet95UpdateDayOfTheYear;
import net.minecraft.network.packet.Packet9Respawn;

public abstract class NetHandler {
	public abstract boolean isServerHandler();

	public void handleMapChunk(Packet51MapChunk packet51MapChunk1) {
	}

	public void registerPacket(Packet packet1) {
	}

	public void handleErrorMessage(String string1, Object[] object2) {
	}

	public void handleKickDisconnect(Packet255KickDisconnect packet255KickDisconnect1) {
		this.registerPacket(packet255KickDisconnect1);
	}

	public void handleLogin(Packet1Login packet1Login1) {
		this.registerPacket(packet1Login1);
	}

	public void handleFlying(Packet10Flying packet10Flying1) {
		this.registerPacket(packet10Flying1);
	}

	public void handleMultiBlockChange(Packet52MultiBlockChange packet52MultiBlockChange1) {
		this.registerPacket(packet52MultiBlockChange1);
	}

	public void handleBlockDig(Packet14BlockDig packet14BlockDig1) {
		this.registerPacket(packet14BlockDig1);
	}

	public void handleBlockChange(Packet53BlockChange packet53BlockChange1) {
		this.registerPacket(packet53BlockChange1);
	}

	public void handlePreChunk(Packet50PreChunk packet50PreChunk1) {
		this.registerPacket(packet50PreChunk1);
	}

	public void handleNamedEntitySpawn(Packet20NamedEntitySpawn packet20NamedEntitySpawn1) {
		this.registerPacket(packet20NamedEntitySpawn1);
	}

	public void handleEntity(Packet30Entity packet30Entity1) {
		this.registerPacket(packet30Entity1);
	}

	public void handleEntityTeleport(Packet34EntityTeleport packet34EntityTeleport1) {
		this.registerPacket(packet34EntityTeleport1);
	}

	public void handlePlace(Packet15Place packet15Place1) {
		this.registerPacket(packet15Place1);
	}

	public void handleBlockItemSwitch(Packet16BlockItemSwitch packet16BlockItemSwitch1) {
		this.registerPacket(packet16BlockItemSwitch1);
	}

	public void handleDestroyEntity(Packet29DestroyEntity packet29DestroyEntity1) {
		this.registerPacket(packet29DestroyEntity1);
	}

	public void handlePickupSpawn(Packet21PickupSpawn packet21PickupSpawn1) {
		this.registerPacket(packet21PickupSpawn1);
	}

	public void handleCollect(Packet22Collect packet22Collect1) {
		this.registerPacket(packet22Collect1);
	}

	public void handleChat(Packet3Chat packet3Chat1) {
		this.registerPacket(packet3Chat1);
	}

	public void handleVehicleSpawn(Packet23VehicleSpawn packet23VehicleSpawn1) {
		this.registerPacket(packet23VehicleSpawn1);
	}

	public void handleAnimation(Packet18Animation packet18Animation1) {
		this.registerPacket(packet18Animation1);
	}

	public void handleEntityAction(Packet19EntityAction packet19EntityAction1) {
		this.registerPacket(packet19EntityAction1);
	}

	public void handleHandshake(Packet2Handshake packet2Handshake1) {
		this.registerPacket(packet2Handshake1);
	}

	public void handleMobSpawn(Packet24MobSpawn packet24MobSpawn1) {
		this.registerPacket(packet24MobSpawn1);
	}

	public void handleArmoredMobSpawn(Packet90ArmoredMobSpawn packet90ArmoredMobSpawn) {
		this.registerPacket(packet90ArmoredMobSpawn);
	}

	public void handleUpdateTime(Packet4UpdateTime packet4UpdateTime1) {
		this.registerPacket(packet4UpdateTime1);
	}

	public void handleSpawnPosition(Packet6SpawnPosition packet6SpawnPosition1) {
		this.registerPacket(packet6SpawnPosition1);
	}

	public void handleEntityVelocity(Packet28EntityVelocity packet28EntityVelocity1) {
		this.registerPacket(packet28EntityVelocity1);
	}

	public void handleEntityMetadata(Packet40EntityMetadata packet40EntityMetadata1) {
		this.registerPacket(packet40EntityMetadata1);
	}

	public void handleAttachEntity(Packet39AttachEntity packet39AttachEntity1) {
		this.registerPacket(packet39AttachEntity1);
	}

	public void handleUseEntity(Packet7UseEntity packet7UseEntity1) {
		this.registerPacket(packet7UseEntity1);
	}

	public void handleEntityStatus(Packet38EntityStatus packet38EntityStatus1) {
		this.registerPacket(packet38EntityStatus1);
	}

	public void handleUpdateHealth(Packet8UpdateHealth packet8UpdateHealth1) {
		this.registerPacket(packet8UpdateHealth1);
	}

	public void handleRespawn(Packet9Respawn packet9Respawn1) {
		this.registerPacket(packet9Respawn1);
	}

	public void handleExplosion(Packet60Explosion packet60Explosion1) {
		this.registerPacket(packet60Explosion1);
	}

	public void handleOpenWindow(Packet100OpenWindow packet100OpenWindow1) {
		this.registerPacket(packet100OpenWindow1);
	}

	public void handleCloseWindow(Packet101CloseWindow packet101CloseWindow1) {
		this.registerPacket(packet101CloseWindow1);
	}

	public void handleWindowClick(Packet102WindowClick packet102WindowClick1) {
		this.registerPacket(packet102WindowClick1);
	}

	public void handleSetSlot(Packet103SetSlot packet103SetSlot1) {
		this.registerPacket(packet103SetSlot1);
	}

	public void handleWindowItems(Packet104WindowItems packet104WindowItems1) {
		this.registerPacket(packet104WindowItems1);
	}

	public void handleUpdateSign(Packet130UpdateSign packet130UpdateSign1) {
		this.registerPacket(packet130UpdateSign1);
	}

	public void handleUpdateProgressbar(Packet105UpdateProgressbar packet105UpdateProgressbar1) {
		this.registerPacket(packet105UpdateProgressbar1);
	}

	public void handlePlayerInventory(Packet5PlayerInventory packet5PlayerInventory1) {
		this.registerPacket(packet5PlayerInventory1);
	}

	public void handleTransaction(Packet106Transaction packet106Transaction1) {
		this.registerPacket(packet106Transaction1);
	}

	public void handleEntityPainting(Packet25EntityPainting packet25EntityPainting1) {
		this.registerPacket(packet25EntityPainting1);
	}

	public void handlePlayNoteBlock(Packet54PlayNoteBlock packet54PlayNoteBlock1) {
		this.registerPacket(packet54PlayNoteBlock1);
	}

	public void handleStatistic(Packet200Statistic packet200Statistic1) {
		this.registerPacket(packet200Statistic1);
	}

	public void handleSleep(Packet17Sleep packet17Sleep1) {
		this.registerPacket(packet17Sleep1);
	}

	public void handleBed(Packet70GameEvent Packet70GameEvent1) {
		this.registerPacket(Packet70GameEvent1);
	}

	public void handleWeather(Packet71Weather packet71Weather1) {
		this.registerPacket(packet71Weather1);
	}

	public void handleDoorChange(Packet61DoorChange packet61DoorChange1) {
		this.registerPacket(packet61DoorChange1);
	}

	public void handleServerPing(Packet254ServerPing packet254ServerPing1) {
		this.registerPacket(packet254ServerPing1);
	}

	public void handlePlayerInfo(Packet201PlayerInfo packet201PlayerInfo1) {
		this.registerPacket(packet201PlayerInfo1);
	}

	public void handleKeepAlive(Packet0KeepAlive packet0KeepAlive1) {
		this.registerPacket(packet0KeepAlive1);
	}

	public void handleExperience(Packet43Experience packet43Experience1) {
		this.registerPacket(packet43Experience1);
	}

	public void handleCreativeSetSlot(Packet107CreativeSetSlot packet107CreativeSetSlot1) {
		this.registerPacket(packet107CreativeSetSlot1);
	}

	public void handleEnchantItem(Packet108EnchantItem packet108EnchantItem1) {
	}

	public void handleCustomPayload(Packet250CustomPayload packet250CustomPayload1) {
	}

	public void handleEntityHeadRotation(Packet35EntityHeadRotation packet35EntityHeadRotation1) {
		this.registerPacket(packet35EntityHeadRotation1);
	}

	public void handleTileEntityData(Packet132TileEntityData packet132TileEntityData1) {
		this.registerPacket(packet132TileEntityData1);
	}

	public void func_50100_a(Packet202PlayerAbilities packet202PlayerAbilities1) {
		this.registerPacket(packet202PlayerAbilities1);
	}
	
	public void handleUpdateDayOfTheYear(Packet95UpdateDayOfTheYear packet) {
		this.registerPacket(packet);
	}
	
	public void handleUpdateCommandBlock(Packet91UpdateCommandBlock packet91UpdateCommandBlock) {
		this.registerPacket(packet91UpdateCommandBlock);
	}

	public void handleSetArmor(Packet89SetArmor packet) {
		this.registerPacket(packet);
	}

	public void handleMapData(Packet131MapData packet) {
		this.registerPacket(packet);
	}

	public void handleMovingPiston(Packet88MovingPiston packet) {
		this.registerPacket(packet);
	}

}
