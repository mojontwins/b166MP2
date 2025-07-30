package net.minecraft.src;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

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
			this.serverId = Long.toHexString(rand.nextLong());
			this.netManager.addToSendQueue(new Packet2Handshake(this.serverId));
		} else {
			this.netManager.addToSendQueue(new Packet2Handshake("-"));
		}

	}

	public void handleLogin(Packet1Login packet1Login1) {
		this.username = packet1Login1.username;
		if(packet1Login1.protocolVersion != 13) {
			if(packet1Login1.protocolVersion > 13) {
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

	public void doLogin(Packet1Login packet1Login1) {
		EntityPlayerMP entityPlayerMP2 = this.mcServer.configManager.login(this, packet1Login1.username);
		if(entityPlayerMP2 != null) {
			this.mcServer.configManager.func_28166_b(entityPlayerMP2);
			entityPlayerMP2.setWorldHandler(this.mcServer.getWorldManager(entityPlayerMP2.dimension));
			logger.info(this.getUserAndIPString() + " logged in with entity id " + entityPlayerMP2.entityId + " at (" + entityPlayerMP2.posX + ", " + entityPlayerMP2.posY + ", " + entityPlayerMP2.posZ + ")");
			WorldServer worldServer3 = this.mcServer.getWorldManager(entityPlayerMP2.dimension);
			ChunkCoordinates chunkCoordinates4 = worldServer3.getSpawnPoint();
			NetServerHandler netServerHandler5 = new NetServerHandler(this.mcServer, this.netManager, entityPlayerMP2);
			netServerHandler5.sendPacket(new Packet1Login("", entityPlayerMP2.entityId, worldServer3.getRandomSeed(), (byte)worldServer3.worldProvider.worldType));
			netServerHandler5.sendPacket(new Packet6SpawnPosition(chunkCoordinates4.posX, chunkCoordinates4.posY, chunkCoordinates4.posZ));
			this.mcServer.configManager.func_28170_a(entityPlayerMP2, worldServer3);
			this.mcServer.configManager.sendPacketToAllPlayers(new Packet3Chat("\u00a7e" + entityPlayerMP2.username + " joined the game."));
			this.mcServer.configManager.playerLoggedIn(entityPlayerMP2);
			netServerHandler5.teleportTo(entityPlayerMP2.posX, entityPlayerMP2.posY, entityPlayerMP2.posZ, entityPlayerMP2.rotationYaw, entityPlayerMP2.rotationPitch);
			this.mcServer.networkServer.addPlayer(netServerHandler5);
			netServerHandler5.sendPacket(new Packet4UpdateTime(worldServer3.getWorldTime()));
			entityPlayerMP2.func_20057_k();
		}

		this.finishedProcessing = true;
	}

	public void handleErrorMessage(String string1, Object[] object2) {
		logger.info(this.getUserAndIPString() + " lost connection");
		this.finishedProcessing = true;
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

	static String getServerId(NetLoginHandler netLoginHandler0) {
		return netLoginHandler0.serverId;
	}

	static Packet1Login setLoginPacket(NetLoginHandler netLoginHandler0, Packet1Login packet1Login1) {
		return netLoginHandler0.packet1login = packet1Login1;
	}
}
