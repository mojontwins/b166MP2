package net.minecraft.server.network;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.network.NetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet1Login;
import net.minecraft.network.packet.Packet202PlayerAbilities;
import net.minecraft.network.packet.Packet254ServerPing;
import net.minecraft.network.packet.Packet255KickDisconnect;
import net.minecraft.network.packet.Packet2Handshake;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet6SpawnPosition;
import net.minecraft.network.packet.Packet95UpdateDayOfTheYear;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ThreadLoginVerifier;
import net.minecraft.server.WorldServer;
import net.minecraft.server.player.EntityPlayerMP;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.chunk.ChunkCoordinates;

public class NetLoginHandler extends NetHandler {
	public static Logger logger = Logger.getLogger("Minecraft");
	private static Random rand = new Random();
	public NetworkManager netManager;
	public boolean finishedProcessing = false;
	private MinecraftServer mcServer;
	private int loginTimer = 0;
	private String username = null;
	private Packet1Login packet1login = null;
	private String serverId = "";

	public NetLoginHandler(MinecraftServer minecraftServer1, Socket socket2, String string3) throws IOException {
		this.mcServer = minecraftServer1;
		this.netManager = new NetworkManager(socket2, string3, this);
		this.netManager.chunkDataSendCounter = 0;
	}

	public void tryLogin() {
		if(this.packet1login != null) {
			this.doLogin(this.packet1login);
			this.packet1login = null;
		}

		if(this.loginTimer++ == 600) {
			this.kickUser("Took too long to log in");
		} else {
			this.netManager.processReadPackets();
		}

	}

	public void kickUser(String string1) {
		try {
			logger.info("Disconnecting " + this.getUserAndIPString() + ": " + string1);
			this.netManager.addToSendQueue(new Packet255KickDisconnect(string1));
			this.netManager.serverShutdown();
			this.finishedProcessing = true;
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

	}

	public void handleHandshake(Packet2Handshake packet2Handshake1) {
		if(this.mcServer.onlineMode) {
			this.serverId = Long.toString(rand.nextLong(), 16);
			this.netManager.addToSendQueue(new Packet2Handshake(this.serverId));
		} else {
			this.netManager.addToSendQueue(new Packet2Handshake("-"));
		}

	}

	public void handleLogin(Packet1Login packet1Login1) {
		this.username = packet1Login1.username;
		if(packet1Login1.protocolVersion != 29) {
			if(packet1Login1.protocolVersion > 29) {
				this.kickUser("Outdated server!");
			} else {
				this.kickUser("Outdated client!");
			}

		} else {
			if(!this.mcServer.onlineMode) {
				this.doLogin(packet1Login1);
			} else {
				(new ThreadLoginVerifier(this, packet1Login1)).start();
			}

		}
	}

	public void doLogin(Packet1Login packet) {
		EntityPlayerMP thePlayer = this.mcServer.configManager.login(this, packet.username);

		if(thePlayer != null) {
			this.mcServer.configManager.readPlayerDataFromFile(thePlayer);
			thePlayer.setWorld(this.mcServer.getWorldManager(thePlayer.dimension));
			thePlayer.itemInWorldManager.setWorld((WorldServer)thePlayer.worldObj);

			logger.info(this.getUserAndIPString() + " logged in with entity id " + thePlayer.entityId + " at (" + thePlayer.posX + ", " + thePlayer.posY + ", " + thePlayer.posZ + ")");

			WorldServer theWorld = this.mcServer.getWorldManager(thePlayer.dimension);
			ChunkCoordinates chunkCoordinates4 = theWorld.getSpawnPoint();

			thePlayer.itemInWorldManager.setGameMode(theWorld.getWorldInfo().getGameType());
			
			NetServerHandler netHandler = new NetServerHandler(this.mcServer, this.netManager, thePlayer);
			netHandler.sendPacket(new Packet1Login(
					"", 
					thePlayer.entityId, 
					theWorld.getWorldInfo().getTerrainType(), 
					thePlayer.itemInWorldManager.getGameType(), 
					theWorld.worldProvider.worldType, 
					(byte)theWorld.difficultySetting, 
					(byte)theWorld.getHeight(), 
					(byte)this.mcServer.configManager.getMaxPlayers(),
					theWorld.getWorldInfo().isEnableSeasons()));
			netHandler.sendPacket(new Packet6SpawnPosition(chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ));
			netHandler.sendPacket(new Packet202PlayerAbilities(thePlayer.capabilities));

			this.mcServer.configManager.updateTimeAndWeather(thePlayer, theWorld);
			this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + thePlayer.username + " joined the game."));
			this.mcServer.configManager.playerLoggedIn(thePlayer);
			
			netHandler.teleportTo(thePlayer.posX, thePlayer.posY, thePlayer.posZ, thePlayer.rotationYaw, thePlayer.rotationPitch);
			this.mcServer.networkServer.addPlayer(netHandler);
			netHandler.sendPacket(new Packet4UpdateTime(theWorld.getWorldTime()));
			netHandler.sendPacket(new Packet95UpdateDayOfTheYear(Seasons.dayOfTheYear));

			thePlayer.s_func_20057_k();
		}

		this.finishedProcessing = true;
	}

	public void handleErrorMessage(String string1, Object[] object2) {
		logger.info(this.getUserAndIPString() + " lost connection");
		this.finishedProcessing = true;
	}

	public void handleServerPing(Packet254ServerPing packet254ServerPing1) {
		try {
			String string2 = this.mcServer.motd + "\u00a7" + this.mcServer.configManager.playersOnline() + "\u00a7" + this.mcServer.configManager.getMaxPlayers();
			this.netManager.addToSendQueue(new Packet255KickDisconnect(string2));
			this.netManager.serverShutdown();
			this.mcServer.networkServer.s_func_35505_a(this.netManager.getSocket());
			this.finishedProcessing = true;
		} catch (Exception exception3) {
			exception3.printStackTrace();
		}

	}

	public void registerPacket(Packet packet1) {
		this.kickUser("Protocol error");
	}

	public String getUserAndIPString() {
		return this.username != null ? this.username + " [" + this.netManager.getRemoteAddress().toString() + "]" : this.netManager.getRemoteAddress().toString();
	}

	public boolean isServerHandler() {
		return true;
	}

	public static String getServerId(NetLoginHandler netLoginHandler0) {
		return netLoginHandler0.serverId;
	}

	public static Packet1Login setLoginPacket(NetLoginHandler netLoginHandler0, Packet1Login packet1Login1) {
		return netLoginHandler0.packet1login = packet1Login1;
	}
}
