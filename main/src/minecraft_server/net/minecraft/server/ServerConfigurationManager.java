package net.minecraft.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet201PlayerInfo;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet70GameEvent;
import net.minecraft.network.packet.Packet9Respawn;
import net.minecraft.server.network.NetLoginHandler;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.server.player.PlayerManager;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.chunk.storage.ISaveHandler;
import net.minecraft.world.level.dimension.Teleporter;
import net.minecraft.world.level.tile.entity.TileEntity;

public class ServerConfigurationManager implements IServerConfigManager {
	public static Logger logger = Logger.getLogger("Minecraft");
	public List<EntityPlayerMP> playerEntities = new ArrayList<EntityPlayerMP>();
	private MinecraftServer mcServer;
	private PlayerManager[] playerManagerObj = new PlayerManager[5];
	private int maxPlayers;
	private Set<String> bannedPlayers = new HashSet<String>();
	private Set<String> bannedIPs = new HashSet<String>();
	private Set<String> ops = new HashSet<String>();
	private Set<String> whiteListedIPs = new HashSet<String>();
	private File bannedPlayersFile;
	private File ipBanFile;
	private File opFile;
	private File whitelistPlayersFile;
	private ISaveHandler playerNBTManagerObj;
	private boolean whiteListEnforced;
	private int playerIndexForTicker = 0;

	public ServerConfigurationManager(MinecraftServer minecraftServer1) {
		this.mcServer = minecraftServer1;
		this.bannedPlayersFile = minecraftServer1.getFile("banned-players.txt");
		this.ipBanFile = minecraftServer1.getFile("banned-ips.txt");
		this.opFile = minecraftServer1.getFile("ops.txt");
		this.whitelistPlayersFile = minecraftServer1.getFile("white-list.txt");
		int i2 = minecraftServer1.propertyManagerObj.getIntProperty("view-distance", 10);
		this.playerManagerObj[0] = new PlayerManager(minecraftServer1, 0, i2);
		this.playerManagerObj[1] = new PlayerManager(minecraftServer1, -1, i2);
		this.playerManagerObj[2] = new PlayerManager(minecraftServer1, 1, i2);
		this.playerManagerObj[3] = new PlayerManager(minecraftServer1, 7, i2);
		this.playerManagerObj[4] = new PlayerManager(minecraftServer1, 9, i2);
		this.maxPlayers = minecraftServer1.propertyManagerObj.getIntProperty("max-players", 20);
		this.whiteListEnforced = minecraftServer1.propertyManagerObj.getBooleanProperty("white-list", false);
		this.readBannedPlayers();
		this.loadBannedList();
		this.loadOps();
		this.loadWhiteList();
		this.writeBannedPlayers();
		this.saveBannedList();
		this.saveOps();
		this.saveWhiteList();
	}

	public MinecraftServer getServer() {
		return this.mcServer;
	}
	
	public void setPlayerManager(WorldServer[] worldServer1) {
		this.playerNBTManagerObj = worldServer1[0].getSaveHandler().getPlayerNBTManager();
	}

	public void joinNewPlayerManager(EntityPlayerMP entityPlayerMP1) {
		this.playerManagerObj[0].removePlayer(entityPlayerMP1);
		this.playerManagerObj[1].removePlayer(entityPlayerMP1);
		this.playerManagerObj[2].removePlayer(entityPlayerMP1);
		this.playerManagerObj[3].removePlayer(entityPlayerMP1);
		this.playerManagerObj[4].removePlayer(entityPlayerMP1);
		this.getPlayerManager(entityPlayerMP1.dimension).addPlayer(entityPlayerMP1);
		WorldServer worldServer2 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		worldServer2.chunkProviderServer.loadChunk((int)entityPlayerMP1.posX >> 4, (int)entityPlayerMP1.posZ >> 4);
	}

	public int getMaxTrackingDistance() {
		return this.playerManagerObj[0].getMaxTrackingDistance();
	}

	private PlayerManager getPlayerManager(int i1) {
		//return i1 == -1 ? this.playerManagerObj[1] : (i1 == 0 ? this.playerManagerObj[0] : (i1 == 1 ? this.playerManagerObj[2] : null));
		switch(i1) {
		case -1: return this.playerManagerObj[1];
		case 0: return this.playerManagerObj[0];
		case 1: return this.playerManagerObj[2];
		case 7: return this.playerManagerObj[3];
		case 9: return this.playerManagerObj[4];
		default: return null;
		}
	}

	public void readPlayerDataFromFile(EntityPlayerMP entityPlayerMP1) {
		this.playerNBTManagerObj.readPlayerData(entityPlayerMP1);
	}

	public void playerLoggedIn(EntityPlayerMP entityPlayerMP1) {
		this.sendPacketToAllPlayers(new Packet201PlayerInfo(entityPlayerMP1.username, true, 1000));
		this.playerEntities.add(entityPlayerMP1);
		WorldServer worldServer2 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		worldServer2.chunkProviderServer.loadChunk((int)entityPlayerMP1.posX >> 4, (int)entityPlayerMP1.posZ >> 4);

		while(worldServer2.getCollidingBoundingBoxes(entityPlayerMP1, entityPlayerMP1.boundingBox).size() != 0) {
			entityPlayerMP1.setPosition(entityPlayerMP1.posX, entityPlayerMP1.posY + 1.0D, entityPlayerMP1.posZ);
		}

		worldServer2.spawnEntityInWorld(entityPlayerMP1);
		this.getPlayerManager(entityPlayerMP1.dimension).addPlayer(entityPlayerMP1);

		for(int i3 = 0; i3 < this.playerEntities.size(); ++i3) {
			EntityPlayerMP entityPlayerMP4 = (EntityPlayerMP)this.playerEntities.get(i3);
			entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet201PlayerInfo(entityPlayerMP4.username, true, entityPlayerMP4.ping));
		}

	}

	public void serverUpdateMountedMovingPlayer(EntityPlayerMP entityPlayerMP1) {
		this.getPlayerManager(entityPlayerMP1.dimension).updateMountedMovingPlayer(entityPlayerMP1);
	}

	public void playerLoggedOut(EntityPlayerMP entityPlayerMP1) {
		this.playerNBTManagerObj.writePlayerData(entityPlayerMP1);
		this.mcServer.getWorldManager(entityPlayerMP1.dimension).setEntityDead(entityPlayerMP1);
		this.playerEntities.remove(entityPlayerMP1);
		this.getPlayerManager(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
		this.sendPacketToAllPlayers(new Packet201PlayerInfo(entityPlayerMP1.username, false, 9999));
	}

	public EntityPlayerMP login(NetLoginHandler netLoginHandler1, String string2) {
		if(this.bannedPlayers.contains(string2.trim().toLowerCase())) {
			netLoginHandler1.kickUser("You are banned from this server!");
			return null;
		} else if(!this.isAllowedToLogin(string2)) {
			netLoginHandler1.kickUser("You are not white-listed on this server!");
			return null;
		} else {
			String string3 = netLoginHandler1.netManager.getRemoteAddress().toString();
			string3 = string3.substring(string3.indexOf("/") + 1);
			string3 = string3.substring(0, string3.indexOf(":"));
			if(this.bannedIPs.contains(string3)) {
				netLoginHandler1.kickUser("Your IP address is banned from this server!");
				return null;
			} else if(this.playerEntities.size() >= this.maxPlayers) {
				netLoginHandler1.kickUser("The server is full!");
				return null;
			} else {
				for(int i4 = 0; i4 < this.playerEntities.size(); ++i4) {
					EntityPlayerMP entityPlayerMP5 = (EntityPlayerMP)this.playerEntities.get(i4);
					if(entityPlayerMP5.username.equalsIgnoreCase(string2)) {
						entityPlayerMP5.playerNetServerHandler.kickPlayer("You logged in from another location");
					}
				}

				return new EntityPlayerMP(this.mcServer, this.mcServer.getWorldManager(0), string2, new ItemInWorldManager(this.mcServer.getWorldManager(0)));
			}
		}
	}

	public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP entityPlayerMP1, int i2, boolean z3) {
		System.out.println ("Recreating player entity - " + entityPlayerMP1.username + " Spawn is " + entityPlayerMP1.getSpawnDimension() + " : " + entityPlayerMP1.getSpawnChunk());
		
		// Remove current instance of the player
		this.mcServer.getEntityTracker(entityPlayerMP1.dimension).removeTrackedPlayerSymmetric(entityPlayerMP1);
		this.mcServer.getEntityTracker(entityPlayerMP1.dimension).untrackEntity(entityPlayerMP1);
		this.getPlayerManager(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
		this.playerEntities.remove(entityPlayerMP1);
		this.mcServer.getWorldManager(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
		
		// Respawn player. Get where:
		ChunkCoordinates chunkCoordinates4 = entityPlayerMP1.getSpawnChunk();
		int spawnDimension = entityPlayerMP1.getSpawnDimension(); 					// This is new!
		
		// Originally, this set the dimension to i2, which was always 0.
		// I have to update this accordingly now that I'm storing where the player set the spawn!
		entityPlayerMP1.dimension = spawnDimension; // was: i2;
		EntityPlayerMP entityPlayerMP5 = new EntityPlayerMP(
				this.mcServer, 
				this.mcServer.getWorldManager(entityPlayerMP1.dimension), 
				entityPlayerMP1.username, 
				new ItemInWorldManager(this.mcServer.getWorldManager(entityPlayerMP1.dimension))
			);
		
		if(z3) {
			entityPlayerMP5.copyPlayer(entityPlayerMP1);
		}

		// Give the new player the same ID and server handler the old had
		entityPlayerMP5.entityId = entityPlayerMP1.entityId;
		entityPlayerMP5.playerNetServerHandler = entityPlayerMP1.playerNetServerHandler;
		
		// Also set the correct dimension!
		entityPlayerMP5.dimension = spawnDimension;
		
		// Same properties.
		WorldServer worldServer6 = this.mcServer.getWorldManager(entityPlayerMP5.dimension); // Was: entityPlayerMP1.dimension !!
		
		entityPlayerMP5.itemInWorldManager.toggleGameType(entityPlayerMP1.itemInWorldManager.getGameType());
		entityPlayerMP5.itemInWorldManager.setGameMode(worldServer6.getWorldInfo().getGameType());
		
		if(chunkCoordinates4 != null) {
			
			ChunkCoordinates chunkCoordinates7 = EntityPlayer.verifyRespawnCoordinates(this.mcServer.getWorldManager(entityPlayerMP1.dimension), chunkCoordinates4);
			if(chunkCoordinates7 != null) {
				entityPlayerMP5.setLocationAndAngles((double)((float)chunkCoordinates7.posX + 0.5F), (double)((float)chunkCoordinates7.posY + 0.1F), (double)((float)chunkCoordinates7.posZ + 0.5F), 0.0F, 0.0F);
				entityPlayerMP5.setSpawnChunk(chunkCoordinates4);
				
				// Also set spawn dimension!
				entityPlayerMP5.setSpawnDimension(spawnDimension);
			} else {
				entityPlayerMP5.playerNetServerHandler.sendPacket(new Packet70GameEvent(0, 0));
			}
		}

		worldServer6.chunkProviderServer.loadChunk((int)entityPlayerMP5.posX >> 4, (int)entityPlayerMP5.posZ >> 4);

		while(worldServer6.getCollidingBoundingBoxes(entityPlayerMP5, entityPlayerMP5.boundingBox).size() != 0) {
			entityPlayerMP5.setPosition(entityPlayerMP5.posX, entityPlayerMP5.posY + 1.0D, entityPlayerMP5.posZ);
		}

		entityPlayerMP5.playerNetServerHandler.sendPacket(
				new Packet9Respawn(
						entityPlayerMP5.dimension, 
						(byte)entityPlayerMP5.worldObj.difficultySetting, 
						entityPlayerMP5.worldObj.getWorldInfo().getTerrainType(), 
						entityPlayerMP5.worldObj.getHeight(), 
						entityPlayerMP5.itemInWorldManager.getGameType()
					)
			);
		entityPlayerMP5.playerNetServerHandler.teleportTo(entityPlayerMP5.posX, entityPlayerMP5.posY, entityPlayerMP5.posZ, entityPlayerMP5.rotationYaw, entityPlayerMP5.rotationPitch);
		
		this.updateTimeAndWeather(entityPlayerMP5, worldServer6);
		this.getPlayerManager(entityPlayerMP5.dimension).addPlayer(entityPlayerMP5);
		
		worldServer6.spawnEntityInWorld(entityPlayerMP5);
		this.playerEntities.add(entityPlayerMP5);
		
		entityPlayerMP5.s_func_20057_k();
		entityPlayerMP5.s_func_22068_s();
		
		return entityPlayerMP5;
	}

	public void sendPlayerToOtherDimension(EntityPlayerMP entityPlayerMP1, int dimensionTo) {
		// From
		int dimensionFrom = entityPlayerMP1.dimension;
		WorldServer worldFrom = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		
		// To
		entityPlayerMP1.dimension = dimensionTo;
		WorldServer worldTo = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		
		// Notify client
		entityPlayerMP1.playerNetServerHandler.sendPacket(
			new Packet9Respawn(
				entityPlayerMP1.dimension, 
				(byte)entityPlayerMP1.worldObj.difficultySetting, 
				worldTo.getWorldInfo().getTerrainType(), 
				worldTo.getHeight(), 
				entityPlayerMP1.itemInWorldManager.getGameType()
			)
		);
		
		// Remove player from old worldmanager
		worldFrom.removePlayer(entityPlayerMP1);
		entityPlayerMP1.isDead = false;

		// Get current position in old world

		double x = entityPlayerMP1.posX;
		double y = entityPlayerMP1.posY;
		double z = entityPlayerMP1.posZ;
		double netherMultiplier = 8.0D;

		// Creates a teleporter object
		
		Teleporter teleporter = new Teleporter();

		// Depending on the destination...
		if(entityPlayerMP1.dimension == -1) {
			x /= netherMultiplier;
			z /= netherMultiplier;
			entityPlayerMP1.setLocationAndAngles(x, y, z, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
			if(entityPlayerMP1.isEntityAlive()) {
				worldFrom.updateEntityWithOptionalForce(entityPlayerMP1, false);
			}
		} else if(entityPlayerMP1.dimension == 0) {
			if(dimensionFrom == -1) {
				// If it comes from the nether
				x *= netherMultiplier;
				z *= netherMultiplier;
			} 
			
			entityPlayerMP1.setLocationAndAngles(x, y, z, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
			if(entityPlayerMP1.isEntityAlive()) {
				worldFrom.updateEntityWithOptionalForce(entityPlayerMP1, false);
			}
		} else if(entityPlayerMP1.dimension == 1) {
			ChunkCoordinates chunkCoordinates12 = worldTo.getEntrancePortalLocation();
			x = (double)chunkCoordinates12.posX;
			entityPlayerMP1.posY = (double)chunkCoordinates12.posY;
			z = (double)chunkCoordinates12.posZ;
			entityPlayerMP1.setLocationAndAngles(x, y, z, 90.0F, 0.0F);
			if(entityPlayerMP1.isEntityAlive()) {
				worldFrom.updateEntityWithOptionalForce(entityPlayerMP1, false);
			}
		} 

		if(dimensionFrom != 1 && entityPlayerMP1.isEntityAlive()) {
			worldTo.spawnEntityInWorld(entityPlayerMP1);
			entityPlayerMP1.setLocationAndAngles(x, entityPlayerMP1.posY, z, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
			worldTo.updateEntityWithOptionalForce(entityPlayerMP1, false);
			worldTo.chunkProviderServer.chunkLoadOverride = true;
			teleporter.placeInPortal(worldTo, entityPlayerMP1);
			worldTo.chunkProviderServer.chunkLoadOverride = false;
		}

		this.joinNewPlayerManager(entityPlayerMP1);
		entityPlayerMP1.playerNetServerHandler.teleportTo(entityPlayerMP1.posX, entityPlayerMP1.posY, entityPlayerMP1.posZ, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
		entityPlayerMP1.setWorld(worldTo);
		entityPlayerMP1.itemInWorldManager.setWorld(worldTo);
		this.updateTimeAndWeather(entityPlayerMP1, worldTo);
		this.s_func_30008_g(entityPlayerMP1);
		
	}

	public void onTick() {
		if(++this.playerIndexForTicker > 200) {
			this.playerIndexForTicker = 0;
		}

		if(this.playerIndexForTicker < this.playerEntities.size()) {			
			EntityPlayerMP entityPlayerMP1 = (EntityPlayerMP)this.playerEntities.get(this.playerIndexForTicker);
			this.sendPacketToAllPlayers(new Packet201PlayerInfo(entityPlayerMP1.username, true, entityPlayerMP1.ping));
		}

		for(int i2 = 0; i2 < this.playerManagerObj.length; ++i2) {
			this.playerManagerObj[i2].updatePlayerInstances();
		}

	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3, int i4) {
		this.getPlayerManager(i4).markBlockNeedsUpdate(i1, i2, i3);
	}

	public void sendPacketToAllPlayers(Packet packet1) {
		for(int i2 = 0; i2 < this.playerEntities.size(); ++i2) {
			EntityPlayerMP entityPlayerMP3 = (EntityPlayerMP)this.playerEntities.get(i2);
			entityPlayerMP3.playerNetServerHandler.sendPacket(packet1);
		}

	}

	public void sendPacketToAllPlayersInDimension(Packet packet1, int i2) {
		for(int i3 = 0; i3 < this.playerEntities.size(); ++i3) {
			EntityPlayerMP entityPlayerMP4 = (EntityPlayerMP)this.playerEntities.get(i3);
			if(entityPlayerMP4.dimension == i2) {
				entityPlayerMP4.playerNetServerHandler.sendPacket(packet1);
			}
		}

	}

	public String getPlayerList() {
		String string1 = "";

		for(int i2 = 0; i2 < this.playerEntities.size(); ++i2) {
			if(i2 > 0) {
				string1 = string1 + ", ";
			}

			string1 = string1 + ((EntityPlayerMP)this.playerEntities.get(i2)).username;
		}

		return string1;
	}

	public String[] getPlayerNamesAsList() {
		String[] string1 = new String[this.playerEntities.size()];

		for(int i2 = 0; i2 < this.playerEntities.size(); ++i2) {
			string1[i2] = ((EntityPlayerMP)this.playerEntities.get(i2)).username;
		}

		return string1;
	}

	public void banPlayer(String string1) {
		this.bannedPlayers.add(string1.toLowerCase());
		this.writeBannedPlayers();
	}

	public void pardonPlayer(String string1) {
		this.bannedPlayers.remove(string1.toLowerCase());
		this.writeBannedPlayers();
	}

	private void readBannedPlayers() {
		try {
			this.bannedPlayers.clear();
			BufferedReader bufferedReader1 = new BufferedReader(new FileReader(this.bannedPlayersFile));
			String string2 = "";

			while((string2 = bufferedReader1.readLine()) != null) {
				this.bannedPlayers.add(string2.trim().toLowerCase());
			}

			bufferedReader1.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load ban list: " + exception3);
		}

	}

	private void writeBannedPlayers() {
		try {
			PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.bannedPlayersFile, false));
			Iterator<String> iterator2 = this.bannedPlayers.iterator();

			while(iterator2.hasNext()) {
				String string3 = (String)iterator2.next();
				printWriter1.println(string3);
			}

			printWriter1.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ban list: " + exception4);
		}

	}

	public Set<String> getBannedPlayersList() {
		return this.bannedPlayers;
	}

	public Set<String> getBannedIPsList() {
		return this.bannedIPs;
	}

	public void banIP(String string1) {
		this.bannedIPs.add(string1.toLowerCase());
		this.saveBannedList();
	}

	public void pardonIP(String string1) {
		this.bannedIPs.remove(string1.toLowerCase());
		this.saveBannedList();
	}

	private void loadBannedList() {
		try {
			this.bannedIPs.clear();
			BufferedReader bufferedReader1 = new BufferedReader(new FileReader(this.ipBanFile));
			String string2 = "";

			while((string2 = bufferedReader1.readLine()) != null) {
				this.bannedIPs.add(string2.trim().toLowerCase());
			}

			bufferedReader1.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load ip ban list: " + exception3);
		}

	}

	private void saveBannedList() {
		try {
			PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.ipBanFile, false));
			Iterator<String> iterator2 = this.bannedIPs.iterator();

			while(iterator2.hasNext()) {
				String string3 = (String)iterator2.next();
				printWriter1.println(string3);
			}

			printWriter1.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ip ban list: " + exception4);
		}

	}

	public void addOp(String string1) {
		this.ops.add(string1.toLowerCase());
		this.saveOps();
	}

	public void removeOp(String string1) {
		this.ops.remove(string1.toLowerCase());
		this.saveOps();
	}

	private void loadOps() {
		try {
			this.ops.clear();
			BufferedReader bufferedReader1 = new BufferedReader(new FileReader(this.opFile));
			String string2 = "";

			while((string2 = bufferedReader1.readLine()) != null) {
				this.ops.add(string2.trim().toLowerCase());
			}

			bufferedReader1.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load operators list: " + exception3);
		}

	}

	private void saveOps() {
		try {
			PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.opFile, false));
			Iterator<String> iterator2 = this.ops.iterator();

			while(iterator2.hasNext()) {
				String string3 = (String)iterator2.next();
				printWriter1.println(string3);
			}

			printWriter1.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save operators list: " + exception4);
		}

	}

	private void loadWhiteList() {
		try {
			this.whiteListedIPs.clear();
			BufferedReader bufferedReader1 = new BufferedReader(new FileReader(this.whitelistPlayersFile));
			String string2 = "";

			while((string2 = bufferedReader1.readLine()) != null) {
				this.whiteListedIPs.add(string2.trim().toLowerCase());
			}

			bufferedReader1.close();
		} catch (Exception exception3) {
			logger.warning("Failed to load white-list: " + exception3);
		}

	}

	private void saveWhiteList() {
		try {
			PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.whitelistPlayersFile, false));
			Iterator<String> iterator2 = this.whiteListedIPs.iterator();

			while(iterator2.hasNext()) {
				String string3 = (String)iterator2.next();
				printWriter1.println(string3);
			}

			printWriter1.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save white-list: " + exception4);
		}

	}

	public boolean isAllowedToLogin(String string1) {
		string1 = string1.trim().toLowerCase();
		return !this.whiteListEnforced || this.ops.contains(string1) || this.whiteListedIPs.contains(string1);
	}

	public boolean isOp(String string1) {
		return this.ops.contains(string1.trim().toLowerCase());
	}

	public EntityPlayerMP getPlayerEntity(String string1) {
		for(int i2 = 0; i2 < this.playerEntities.size(); ++i2) {
			EntityPlayerMP entityPlayerMP3 = (EntityPlayerMP)this.playerEntities.get(i2);
			if(entityPlayerMP3.username.equalsIgnoreCase(string1)) {
				return entityPlayerMP3;
			}
		}

		return null;
	}

	public void sendChatMessageToPlayer(String string1, String string2) {
		EntityPlayerMP entityPlayerMP3 = this.getPlayerEntity(string1);
		if(entityPlayerMP3 != null) {
			entityPlayerMP3.playerNetServerHandler.sendPacket(new Packet3Chat(string2));
		}

	}

	public void sendPacketToPlayersAroundPoint(double d1, double d3, double d5, double d7, int i9, Packet packet10) {
		this.s_func_28171_a((EntityPlayer)null, d1, d3, d5, d7, i9, packet10);
	}

	public void s_func_28171_a(EntityPlayer entityPlayer1, double d2, double d4, double d6, double d8, int i10, Packet packet11) {
		for(int i12 = 0; i12 < this.playerEntities.size(); ++i12) {
			EntityPlayerMP entityPlayerMP13 = (EntityPlayerMP)this.playerEntities.get(i12);
			if(entityPlayerMP13 != entityPlayer1 && entityPlayerMP13.dimension == i10) {
				double d14 = d2 - entityPlayerMP13.posX;
				double d16 = d4 - entityPlayerMP13.posY;
				double d18 = d6 - entityPlayerMP13.posZ;
				if(d14 * d14 + d16 * d16 + d18 * d18 < d8 * d8) {
					entityPlayerMP13.playerNetServerHandler.sendPacket(packet11);
				}
			}
		}

	}

	public void sendChatMessageToAllOps(String string1) {
		Packet3Chat packet3Chat2 = new Packet3Chat(string1);

		for(int i3 = 0; i3 < this.playerEntities.size(); ++i3) {
			EntityPlayerMP entityPlayerMP4 = (EntityPlayerMP)this.playerEntities.get(i3);
			if(this.isOp(entityPlayerMP4.username)) {
				entityPlayerMP4.playerNetServerHandler.sendPacket(packet3Chat2);
			}
		}

	}

	public boolean sendPacketToPlayer(String string1, Packet packet2) {
		EntityPlayerMP entityPlayerMP3 = this.getPlayerEntity(string1);
		if(entityPlayerMP3 != null) {
			entityPlayerMP3.playerNetServerHandler.sendPacket(packet2);
			return true;
		} else {
			return false;
		}
	}

	public void savePlayerStates() {
		for(int i1 = 0; i1 < this.playerEntities.size(); ++i1) {
			this.playerNBTManagerObj.writePlayerData((EntityPlayer)this.playerEntities.get(i1));
		}

	}

	public void sentTileEntityToPlayer(int i1, int i2, int i3, TileEntity tileEntity4) {
	}

	public void addToWhiteList(String string1) {
		this.whiteListedIPs.add(string1);
		this.saveWhiteList();
	}

	public void removeFromWhiteList(String string1) {
		this.whiteListedIPs.remove(string1);
		this.saveWhiteList();
	}

	public Set<String> getWhiteListedIPs() {
		return this.whiteListedIPs;
	}

	public void reloadWhiteList() {
		this.loadWhiteList();
	}

	public void updateTimeAndWeather(EntityPlayerMP thePlayer, WorldServer world) {
		thePlayer.playerNetServerHandler.sendPacket(new Packet4UpdateTime(world.getWorldTime()));
		System.out.println ("Sending time = " + world.getWorldTime());
		thePlayer.playerNetServerHandler.sendPacket(new Packet70GameEvent(world.isRaining(), world.isSnowing(), world.isThundering()));

	}

	public void s_func_30008_g(EntityPlayerMP entityPlayerMP1) {
		entityPlayerMP1.s_func_28017_a(entityPlayerMP1.inventorySlots);
		entityPlayerMP1.s_func_30001_B();
	}

	public void sendChatMessageToAllPlayers(String string1) {
		Packet3Chat packet3Chat2 = new Packet3Chat(string1);

		for(int i3 = 0; i3 < this.playerEntities.size(); ++i3) {
			((EntityPlayerMP)this.playerEntities.get(i3)).playerNetServerHandler.sendPacket(packet3Chat2);
		}

	}	
	
	public int playersOnline() {
		return this.playerEntities.size();
	}

	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	public String[] s_func_52019_t() {
		return this.mcServer.worldMngr[0].getSaveHandler().getPlayerNBTManager().s_func_52007_g();
	}

}
