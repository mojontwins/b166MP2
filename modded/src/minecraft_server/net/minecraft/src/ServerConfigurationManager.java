package net.minecraft.src;

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

import net.minecraft.server.MinecraftServer;

public class ServerConfigurationManager {
	public static Logger logger = Logger.getLogger("Minecraft");
	public List playerEntities = new ArrayList();
	private MinecraftServer mcServer;
	private PlayerManager[] playerManagerObj = new PlayerManager[2];
	private int maxPlayers;
	private Set bannedPlayers = new HashSet();
	private Set bannedIPs = new HashSet();
	private Set ops = new HashSet();
	private Set whiteListedIPs = new HashSet();
	private File bannedPlayersFile;
	private File ipBanFile;
	private File opFile;
	private File whitelistPlayersFile;
	private IPlayerFileData playerNBTManagerObj;
	private boolean whiteListEnforced;

	public ServerConfigurationManager(MinecraftServer minecraftServer1) {
		this.mcServer = minecraftServer1;
		this.bannedPlayersFile = minecraftServer1.getFile("banned-players.txt");
		this.ipBanFile = minecraftServer1.getFile("banned-ips.txt");
		this.opFile = minecraftServer1.getFile("ops.txt");
		this.whitelistPlayersFile = minecraftServer1.getFile("white-list.txt");
		int i2 = minecraftServer1.propertyManagerObj.getIntProperty("view-distance", 10);
		this.playerManagerObj[0] = new PlayerManager(minecraftServer1, 0, i2);
		this.playerManagerObj[1] = new PlayerManager(minecraftServer1, -1, i2);
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

	public void setPlayerManager(WorldServer[] worldServer1) {
		this.playerNBTManagerObj = worldServer1[0].func_22075_m().func_22090_d();
	}

	public void func_28172_a(EntityPlayerMP entityPlayerMP1) {
		this.playerManagerObj[0].removePlayer(entityPlayerMP1);
		this.playerManagerObj[1].removePlayer(entityPlayerMP1);
		this.func_28167_a(entityPlayerMP1.dimension).addPlayer(entityPlayerMP1);
		WorldServer worldServer2 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		worldServer2.chunkProviderServer.loadChunk((int)entityPlayerMP1.posX >> 4, (int)entityPlayerMP1.posZ >> 4);
	}

	public int getMaxTrackingDistance() {
		return this.playerManagerObj[0].getMaxTrackingDistance();
	}

	private PlayerManager func_28167_a(int i1) {
		return i1 == -1 ? this.playerManagerObj[1] : this.playerManagerObj[0];
	}

	public void func_28166_b(EntityPlayerMP entityPlayerMP1) {
		this.playerNBTManagerObj.readPlayerData(entityPlayerMP1);
	}

	public void playerLoggedIn(EntityPlayerMP entityPlayerMP1) {
		this.playerEntities.add(entityPlayerMP1);
		WorldServer worldServer2 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		worldServer2.chunkProviderServer.loadChunk((int)entityPlayerMP1.posX >> 4, (int)entityPlayerMP1.posZ >> 4);

		while(worldServer2.getCollidingBoundingBoxes(entityPlayerMP1, entityPlayerMP1.boundingBox).size() != 0) {
			entityPlayerMP1.setPosition(entityPlayerMP1.posX, entityPlayerMP1.posY + 1.0D, entityPlayerMP1.posZ);
		}

		worldServer2.entityJoinedWorld(entityPlayerMP1);
		this.func_28167_a(entityPlayerMP1.dimension).addPlayer(entityPlayerMP1);
	}

	public void func_613_b(EntityPlayerMP entityPlayerMP1) {
		this.func_28167_a(entityPlayerMP1.dimension).func_543_c(entityPlayerMP1);
	}

	public void playerLoggedOut(EntityPlayerMP entityPlayerMP1) {
		this.playerNBTManagerObj.writePlayerData(entityPlayerMP1);
		this.mcServer.getWorldManager(entityPlayerMP1.dimension).func_22085_d(entityPlayerMP1);
		this.playerEntities.remove(entityPlayerMP1);
		this.func_28167_a(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
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

	public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP entityPlayerMP1, int i2) {
		this.mcServer.getEntityTracker(entityPlayerMP1.dimension).removeTrackedPlayerSymmetric(entityPlayerMP1);
		this.mcServer.getEntityTracker(entityPlayerMP1.dimension).untrackEntity(entityPlayerMP1);
		this.func_28167_a(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
		this.playerEntities.remove(entityPlayerMP1);
		this.mcServer.getWorldManager(entityPlayerMP1.dimension).removePlayer(entityPlayerMP1);
		ChunkCoordinates chunkCoordinates3 = entityPlayerMP1.getSpawnChunk();
		entityPlayerMP1.dimension = i2;
		EntityPlayerMP entityPlayerMP4 = new EntityPlayerMP(this.mcServer, this.mcServer.getWorldManager(entityPlayerMP1.dimension), entityPlayerMP1.username, new ItemInWorldManager(this.mcServer.getWorldManager(entityPlayerMP1.dimension)));
		entityPlayerMP4.entityId = entityPlayerMP1.entityId;
		entityPlayerMP4.playerNetServerHandler = entityPlayerMP1.playerNetServerHandler;
		WorldServer worldServer5 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		if(chunkCoordinates3 != null) {
			ChunkCoordinates chunkCoordinates6 = EntityPlayer.func_25051_a(this.mcServer.getWorldManager(entityPlayerMP1.dimension), chunkCoordinates3);
			if(chunkCoordinates6 != null) {
				entityPlayerMP4.setLocationAndAngles((double)((float)chunkCoordinates6.posX + 0.5F), (double)((float)chunkCoordinates6.posY + 0.1F), (double)((float)chunkCoordinates6.posZ + 0.5F), 0.0F, 0.0F);
				entityPlayerMP4.setSpawnChunk(chunkCoordinates3);
			} else {
				entityPlayerMP4.playerNetServerHandler.sendPacket(new Packet70Bed(0));
			}
		}

		worldServer5.chunkProviderServer.loadChunk((int)entityPlayerMP4.posX >> 4, (int)entityPlayerMP4.posZ >> 4);

		while(worldServer5.getCollidingBoundingBoxes(entityPlayerMP4, entityPlayerMP4.boundingBox).size() != 0) {
			entityPlayerMP4.setPosition(entityPlayerMP4.posX, entityPlayerMP4.posY + 1.0D, entityPlayerMP4.posZ);
		}

		entityPlayerMP4.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)entityPlayerMP4.dimension));
		entityPlayerMP4.playerNetServerHandler.teleportTo(entityPlayerMP4.posX, entityPlayerMP4.posY, entityPlayerMP4.posZ, entityPlayerMP4.rotationYaw, entityPlayerMP4.rotationPitch);
		this.func_28170_a(entityPlayerMP4, worldServer5);
		this.func_28167_a(entityPlayerMP4.dimension).addPlayer(entityPlayerMP4);
		worldServer5.entityJoinedWorld(entityPlayerMP4);
		this.playerEntities.add(entityPlayerMP4);
		entityPlayerMP4.func_20057_k();
		entityPlayerMP4.func_22068_s();
		return entityPlayerMP4;
	}

	public void sendPlayerToOtherDimension(EntityPlayerMP entityPlayerMP1) {
		WorldServer worldServer2 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		boolean z3 = false;
		byte b11;
		if(entityPlayerMP1.dimension == -1) {
			b11 = 0;
		} else {
			b11 = -1;
		}

		entityPlayerMP1.dimension = b11;
		WorldServer worldServer4 = this.mcServer.getWorldManager(entityPlayerMP1.dimension);
		entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet9Respawn((byte)entityPlayerMP1.dimension));
		worldServer2.removePlayer(entityPlayerMP1);
		entityPlayerMP1.isDead = false;
		double d5 = entityPlayerMP1.posX;
		double d7 = entityPlayerMP1.posZ;
		double d9 = 8.0D;
		if(entityPlayerMP1.dimension == -1) {
			d5 /= d9;
			d7 /= d9;
			entityPlayerMP1.setLocationAndAngles(d5, entityPlayerMP1.posY, d7, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
			if(entityPlayerMP1.isEntityAlive()) {
				worldServer2.updateEntityWithOptionalForce(entityPlayerMP1, false);
			}
		} else {
			d5 *= d9;
			d7 *= d9;
			entityPlayerMP1.setLocationAndAngles(d5, entityPlayerMP1.posY, d7, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
			if(entityPlayerMP1.isEntityAlive()) {
				worldServer2.updateEntityWithOptionalForce(entityPlayerMP1, false);
			}
		}

		if(entityPlayerMP1.isEntityAlive()) {
			worldServer4.entityJoinedWorld(entityPlayerMP1);
			entityPlayerMP1.setLocationAndAngles(d5, entityPlayerMP1.posY, d7, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
			worldServer4.updateEntityWithOptionalForce(entityPlayerMP1, false);
			worldServer4.chunkProviderServer.chunkLoadOverride = true;
			(new Teleporter()).setExitLocation(worldServer4, entityPlayerMP1);
			worldServer4.chunkProviderServer.chunkLoadOverride = false;
		}

		this.func_28172_a(entityPlayerMP1);
		entityPlayerMP1.playerNetServerHandler.teleportTo(entityPlayerMP1.posX, entityPlayerMP1.posY, entityPlayerMP1.posZ, entityPlayerMP1.rotationYaw, entityPlayerMP1.rotationPitch);
		entityPlayerMP1.setWorldHandler(worldServer4);
		this.func_28170_a(entityPlayerMP1, worldServer4);
		this.func_30009_g(entityPlayerMP1);
	}

	public void onTick() {
		for(int i1 = 0; i1 < this.playerManagerObj.length; ++i1) {
			this.playerManagerObj[i1].updatePlayerInstances();
		}

	}

	public void markBlockNeedsUpdate(int i1, int i2, int i3, int i4) {
		this.func_28167_a(i4).markBlockNeedsUpdate(i1, i2, i3);
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
			Iterator iterator2 = this.bannedPlayers.iterator();

			while(iterator2.hasNext()) {
				String string3 = (String)iterator2.next();
				printWriter1.println(string3);
			}

			printWriter1.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ban list: " + exception4);
		}

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
			Iterator iterator2 = this.bannedIPs.iterator();

			while(iterator2.hasNext()) {
				String string3 = (String)iterator2.next();
				printWriter1.println(string3);
			}

			printWriter1.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ip ban list: " + exception4);
		}

	}

	public void opPlayer(String string1) {
		this.ops.add(string1.toLowerCase());
		this.saveOps();
	}

	public void deopPlayer(String string1) {
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
			logger.warning("Failed to load ip ban list: " + exception3);
		}

	}

	private void saveOps() {
		try {
			PrintWriter printWriter1 = new PrintWriter(new FileWriter(this.opFile, false));
			Iterator iterator2 = this.ops.iterator();

			while(iterator2.hasNext()) {
				String string3 = (String)iterator2.next();
				printWriter1.println(string3);
			}

			printWriter1.close();
		} catch (Exception exception4) {
			logger.warning("Failed to save ip ban list: " + exception4);
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
			Iterator iterator2 = this.whiteListedIPs.iterator();

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
		this.func_28171_a((EntityPlayer)null, d1, d3, d5, d7, i9, packet10);
	}

	public void func_28171_a(EntityPlayer entityPlayer1, double d2, double d4, double d6, double d8, int i10, Packet packet11) {
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

	public Set getWhiteListedIPs() {
		return this.whiteListedIPs;
	}

	public void reloadWhiteList() {
		this.loadWhiteList();
	}

	public void func_28170_a(EntityPlayerMP entityPlayerMP1, WorldServer worldServer2) {
		entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet4UpdateTime(worldServer2.getWorldTime()));
		if(worldServer2.func_27068_v()) {
			entityPlayerMP1.playerNetServerHandler.sendPacket(new Packet70Bed(1));
		}

	}

	public void func_30009_g(EntityPlayerMP entityPlayerMP1) {
		entityPlayerMP1.func_28017_a(entityPlayerMP1.personalCraftingInventory);
		entityPlayerMP1.func_30002_B();
	}
}
