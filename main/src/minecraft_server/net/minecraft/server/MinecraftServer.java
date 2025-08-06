package net.minecraft.server;

import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mojontwins.minecraft.commands.CommandProcessor;
import com.mojontwins.minecraft.commands.ComplexCommand;
import com.mojontwins.minecraft.worldedit.WorldEdit;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet4UpdateTime;
import net.minecraft.network.packet.Packet95UpdateDayOfTheYear;
import net.minecraft.server.gui.ConsoleLogManager;
import net.minecraft.server.gui.ConvertProgressUpdater;
import net.minecraft.server.gui.ServerGUI;
import net.minecraft.server.network.EntityTracker;
import net.minecraft.server.network.NetworkListenThread;
import net.minecraft.server.rcon.RConConsoleSource;
import net.minecraft.server.rcon.RConThreadMain;
import net.minecraft.server.rcon.RConThreadQuery;
import net.minecraft.src.MathHelper;
import net.minecraft.world.GameRules;
import net.minecraft.world.Version;
import net.minecraft.world.level.Seasons;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.level.chunk.storage.AnvilSaveHandler;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;
import net.minecraft.world.level.chunk.storage.ISaveFormat;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.Vec3D;

public class MinecraftServer implements Runnable, ICommandListener, IServer {
	public static final int dimensions = 2;
	
	public static Logger logger = Logger.getLogger("Minecraft");
	public static HashMap<String, Integer> s_field_6037_b = new HashMap<String, Integer>();
	private String hostname;
	private int serverPort;
	public NetworkListenThread networkServer;
	public PropertyManager propertyManagerObj;
	public WorldServer[] worldMngr;
	public long[] s_field_40027_f = new long[100];
	public long[][] s_field_40028_g;
	public ServerConfigurationManager configManager;
	private ConsoleCommandHandler commandHandler;
	private boolean serverRunning = true;
	public boolean serverStopped = false;
	int deathTime = 0;
	public String currentTask;
	public int percentDone;
	private List<IUpdatePlayerListBox> playersOnline = new ArrayList<IUpdatePlayerListBox>();
	private List<ServerCommand> commands = Collections.synchronizedList(new ArrayList<ServerCommand>());
	public EntityTracker[] entityTracker = new EntityTracker[MinecraftServer.dimensions];
	public boolean onlineMode;
	public boolean spawnPeacefulMobs;
	public boolean s_field_44002_p;
	public boolean pvpOn;
	public boolean allowFlight;
	public String motd;
	public int buildLimit;
	private long s_field_48074_E;
	private long s_field_48075_F;
	private long s_field_48076_G;
	private long s_field_48077_H;
	public long[] s_field_48080_u = new long[100];
	public long[] s_field_48079_v = new long[100];
	public long[] s_field_48078_w = new long[100];
	public long[] s_field_48082_x = new long[100];
	private RConThreadQuery rconQueryThread;
	private RConThreadMain rconMainThread;

	public MinecraftServer() {
		new ThreadServerSleep(this);
	}

	private boolean startServer() throws UnknownHostException {
		this.commandHandler = new ConsoleCommandHandler(this);
		ThreadCommandReader tCommandReader = new ThreadCommandReader(this);
		tCommandReader.setDaemon(true);
		tCommandReader.start();
		ConsoleLogManager.init();
		logger.info("Starting minecraft server version " + Version.getVersion());
		
		// Init game rules
		GameRules.withMcDataDir(new File("."));
		GameRules.loadRulesFromOptions();
		GameRules.saveRulesAsOptions();
		
		// Init commands
		CommandProcessor.registerCommands();
		WorldEdit.registerCommands();
		
		if(Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		logger.info("Loading properties");
		this.propertyManagerObj = new PropertyManager(new File("server.properties"));
		this.hostname = this.propertyManagerObj.getStringProperty("server-ip", "");
		this.onlineMode = this.propertyManagerObj.getBooleanProperty("online-mode", true);
		this.spawnPeacefulMobs = this.propertyManagerObj.getBooleanProperty("spawn-animals", true);
		this.s_field_44002_p = this.propertyManagerObj.getBooleanProperty("spawn-npcs", true);
		this.pvpOn = this.propertyManagerObj.getBooleanProperty("pvp", true);
		this.allowFlight = this.propertyManagerObj.getBooleanProperty("allow-flight", false);
		this.motd = this.propertyManagerObj.getStringProperty("motd", "A Minecraft Server");
		this.motd.replace('\u00a7', '$');
		InetAddress inetAddress2 = null;

		if(this.hostname.length() > 0) {
			inetAddress2 = InetAddress.getByName(this.hostname);
		}

		this.serverPort = this.propertyManagerObj.getIntProperty("server-port", 25565);
		logger.info("Starting Minecraft server on " + (this.hostname.length() == 0 ? "*" : this.hostname) + ":" + this.serverPort);

		try {
			this.networkServer = new NetworkListenThread(this, inetAddress2, this.serverPort);
		} catch (IOException e) {
			logger.warning("**** FAILED TO BIND TO PORT!");
			logger.log(Level.WARNING, "The exception was: " + e.toString());
			logger.warning("Perhaps a server is already running on that port?");
			return false;
		}

		if(!this.onlineMode) {
			logger.warning("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
			logger.warning("The server will make no attempt to authenticate usernames. Beware.");
			logger.warning("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
			logger.warning("To change this, set \"online-mode\" to \"true\" in the server.settings file.");
		}

		this.configManager = new ServerConfigurationManager(this);

		// Todo: Proper DimensionManager to dehardcode this
		this.entityTracker[0] = new EntityTracker(this, 0);
		this.entityTracker[1] = new EntityTracker(this, -1);
		/*
		this.entityTracker[2] = new EntityTracker(this, 1);
		this.entityTracker[3] = new EntityTracker(this, 7);
		this.entityTracker[4] = new EntityTracker(this, 9);
		*/
		
		long nowMillis = System.nanoTime();
		String levelName = this.propertyManagerObj.getStringProperty("level-name", "world");
		String levelSeed = this.propertyManagerObj.getStringProperty("level-seed", "");
		String levelType = this.propertyManagerObj.getStringProperty("level-type", "DEFAULT");
		
		logger.info("Configured level type: " + levelType);
		
		long seed = (new Random()).nextLong();
		if(levelSeed.length() > 0) {
			try {
				long newSeed = Long.parseLong(levelSeed);
				if(newSeed != 0L) {
					seed = newSeed;
				}
			} catch (NumberFormatException numberFormatException14) {
				seed = (long)levelSeed.hashCode();
			}
		}

		WorldType terrainType = WorldType.parseWorldType(levelType);
		if(terrainType == null) {
			terrainType = WorldType.DEFAULT;
		}

		this.buildLimit = this.propertyManagerObj.getIntProperty("max-build-height", 256);
		this.buildLimit = (this.buildLimit + 8) / 16 * 16;
		this.buildLimit = MathHelper.clamp_int(this.buildLimit, 64, 256);
		this.propertyManagerObj.setProperty("max-build-height", this.buildLimit);

		logger.info("Preparing level \"" + levelName + "\"");
		this.initWorld(new AnvilSaveConverter(new File(".")), levelName, seed, terrainType);

		long millis = System.nanoTime() - nowMillis;
		String timeS = String.format("%.3fs", new Object[]{(double)millis / 1.0E9D});
		logger.info("Done (" + timeS + ")! For help, type \"help\" or \"?\"");
		
		if(this.propertyManagerObj.getBooleanProperty("enable-query", false)) {
			logger.info("Starting GS4 status listener");
			this.rconQueryThread = new RConThreadQuery(this);
			this.rconQueryThread.startThread();
		}

		if(this.propertyManagerObj.getBooleanProperty("enable-rcon", false)) {
			logger.info("Starting remote control listener");
			this.rconMainThread = new RConThreadMain(this);
			this.rconMainThread.startThread();
		}

		return true;
	}

	private void initWorld(ISaveFormat saveManager, String levelName, long seed, WorldType terrainType) {
		if(saveManager.isOldMapFormat(levelName)) {
			logger.info("Converting map!");
			saveManager.convertMapFormat(levelName, new ConvertProgressUpdater(this));
		}

		// Todo: Proper DimensionManager to dehardcode this
		this.worldMngr = new WorldServer[MinecraftServer.dimensions];
		this.s_field_40028_g = new long[this.worldMngr.length][100];
		
		int gameMode = this.propertyManagerObj.getIntProperty("gamemode", 0);
		gameMode = WorldSettings.validGameType(gameMode);
		logger.info("Default game type: " + gameMode);
		
		//boolean generateStructures = this.propertyManagerObj.getBooleanProperty("generate-structures", true);
		boolean enableSeasons = this.propertyManagerObj.getBooleanProperty("enableSeasons", false);
		
		WorldSettings worldSettings = new WorldSettings(seed, gameMode, false, false, enableSeasons, terrainType);
		
		AnvilSaveHandler saveHandler = new AnvilSaveHandler(new File("."), levelName, true);

		// TODO: Make this dynamic and use a proper DimensionManager class.
		for(int i = 0; i < this.worldMngr.length; ++i) {
			byte dimensionId = 0;
			if(i == 1) {
				dimensionId = -1;
			}

			if(i == 2) {
				dimensionId = 1;
			}

			if(i == 3) {
				dimensionId = 7;
			}
			
			if(i == 4) {
				dimensionId = 9;
			}
			
			if(i == 0) {
				this.worldMngr[i] = new WorldServer(this, saveHandler, levelName, dimensionId, worldSettings, terrainType);
			} else {
				this.worldMngr[i] = new WorldServerMulti(this, saveHandler, levelName, dimensionId, worldSettings, this.worldMngr[0], terrainType);
			}

			this.worldMngr[i].addWorldAccess(new WorldManager(this, this.worldMngr[i]));
			this.worldMngr[i].difficultySetting = this.propertyManagerObj.getIntProperty("difficulty", 1);
			this.worldMngr[i].setAllowedSpawnTypes(this.propertyManagerObj.getBooleanProperty("spawn-monsters", true), this.spawnPeacefulMobs);
			this.worldMngr[i].getWorldInfo().setGameType(gameMode);
			this.configManager.setPlayerManager(this.worldMngr);
		}

		short radius = 196;
		long prevTime = System.currentTimeMillis();

		for(int dimension = 0; dimension < MinecraftServer.dimensions; ++dimension) {
			logger.info("Preparing start region for level " + dimension);
			WorldServer worldServer = this.worldMngr[dimension];
			ChunkCoordinates spawnCoords = worldServer.getSpawnPoint();

			for(int x = -radius; x <= radius && this.serverRunning; x += 16) {
				for(int z = -radius; z <= radius && this.serverRunning; z += 16) {
					long currentTime = System.currentTimeMillis();
					if(currentTime < prevTime) {
						prevTime = currentTime;
					}

					if(currentTime > prevTime + 1000L) {
						int totalBlocks = (radius * 2 + 1) * (radius * 2 + 1);
						int currentBlock = (x + radius) * (radius * 2 + 1) + z + 1;
						this.outputPercentRemaining("Preparing spawn area", currentBlock * 100 / totalBlocks);
						prevTime= currentTime;
					}

					worldServer.chunkProviderServer.loadChunk(spawnCoords.posX + x >> 4, spawnCoords.posZ + z >> 4);

				}
			}
		}

		this.clearCurrentTask();
	}

	private void outputPercentRemaining(String string1, int i2) {
		this.currentTask = string1;
		this.percentDone = i2;
		logger.info(string1 + ": " + i2 + "%");
	}

	private void clearCurrentTask() {
		this.currentTask = null;
		this.percentDone = 0;
	}

	private void saveServerWorld() {
		logger.info("Saving chunks");

		for(int i1 = 0; i1 < this.worldMngr.length; ++i1) {
			WorldServer worldServer2 = this.worldMngr[i1];
			worldServer2.saveWorld(true, (IProgressUpdate)null);
			worldServer2.s_func_30006_w();
		}

	}

	private void stopServer() {
		logger.info("Stopping server");
		if(this.configManager != null) {
			this.configManager.savePlayerStates();
		}

		for(int i1 = 0; i1 < this.worldMngr.length; ++i1) {
			WorldServer worldServer2 = this.worldMngr[i1];
			if(worldServer2 != null) {
				this.saveServerWorld();
			}
		}

	}

	public void initiateShutdown() {
		this.serverRunning = false;
	}

	public void run() {
		try {
			if(this.startServer()) {
				long j1 = System.currentTimeMillis();

				for(long j3 = 0L; this.serverRunning; Thread.sleep(1L)) {
					long j5 = System.currentTimeMillis();
					long j7 = j5 - j1;
					if(j7 > 2000L) {
						logger.warning("Can\'t keep up! Did the system time change, or is the server overloaded?");
						j7 = 2000L;
					}

					if(j7 < 0L) {
						logger.warning("Time ran backwards! Did the system time change?");
						j7 = 0L;
					}

					j3 += j7;
					j1 = j5;
					{
						while(j3 > 50L) {
							j3 -= 50L;
							this.doTick();
						}
					}
				}
			} else {
				while(this.serverRunning) {
					this.commandLineParser();

					try {
						Thread.sleep(10L);
					} catch (InterruptedException interruptedException57) {
						interruptedException57.printStackTrace();
					}
				}
			}
		} catch (Throwable throwable58) {
			throwable58.printStackTrace();
			logger.log(Level.SEVERE, "Unexpected exception", throwable58);

			while(this.serverRunning) {
				this.commandLineParser();

				try {
					Thread.sleep(10L);
				} catch (InterruptedException interruptedException56) {
					interruptedException56.printStackTrace();
				}
			}
		} finally {
			try {
				this.stopServer();
				this.serverStopped = true;
			} catch (Throwable throwable54) {
				throwable54.printStackTrace();
			} finally {
				System.exit(0);
			}

		}

	}

	private void doTick() {
		long millis = System.nanoTime();
		ArrayList<String> arrayList3 = new ArrayList<String>();
		Iterator<String> it = s_field_6037_b.keySet().iterator();

		while(it.hasNext()) {
			String string5 = (String)it.next();
			int i6 = ((Integer)s_field_6037_b.get(string5)).intValue();
			if(i6 > 0) {
				s_field_6037_b.put(string5, i6 - 1);
			} else {
				arrayList3.add(string5);
			}
		}

		int i;
		for(i = 0; i < arrayList3.size(); ++i) {
			s_field_6037_b.remove(arrayList3.get(i));
		}

		AxisAlignedBB.clearBoundingBoxPool();
		Vec3D.initialize();
		++this.deathTime;

		for(i = 0; i < this.worldMngr.length; ++i) {
			long millis0 = System.nanoTime();
			if(i == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				WorldServer world = this.worldMngr[i];
				if(this.deathTime % 20 == 0) {
					this.configManager.sendPacketToAllPlayersInDimension(new Packet4UpdateTime(world.getWorldTime()), world.worldProvider.worldType);
				}
				
				int dayOfTheYear = Seasons.dayOfTheYear;
				world.tick();
				if (Seasons.dayOfTheYear != dayOfTheYear) {
					this.configManager.sendPacketToAllPlayersInDimension(new Packet95UpdateDayOfTheYear(Seasons.dayOfTheYear), world.worldProvider.worldType);
				}

				world.updateEntities();
				
			}

			this.s_field_40028_g[i][this.deathTime % 100] = System.nanoTime() - millis0;
		}

		this.networkServer.handleNetworkListenThread();
		this.configManager.onTick();

		for(i = 0; i < this.entityTracker.length; ++i) {
			this.entityTracker[i].updateTrackedEntities();
		}

		for(i = 0; i < this.playersOnline.size(); ++i) {
			((IUpdatePlayerListBox)this.playersOnline.get(i)).update();
		}

		try {
			this.commandLineParser();
		} catch (Exception exception8) {
			logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception8);
		}

		this.s_field_40027_f[this.deathTime % 100] = System.nanoTime() - millis;
		this.s_field_48080_u[this.deathTime % 100] = Packet.field_48157_o - this.s_field_48074_E;
		this.s_field_48074_E = Packet.field_48157_o;
		this.s_field_48079_v[this.deathTime % 100] = Packet.field_48155_p - this.s_field_48075_F;
		this.s_field_48075_F = Packet.field_48155_p;
		this.s_field_48078_w[this.deathTime % 100] = Packet.field_48158_m - this.s_field_48076_G;
		this.s_field_48076_G = Packet.field_48158_m;
		this.s_field_48082_x[this.deathTime % 100] = Packet.field_48156_n - this.s_field_48077_H;
		this.s_field_48077_H = Packet.field_48156_n;
	}

	public void addCommand(ComplexCommand complexCommand, ICommandListener iCommandListener2) {
		this.commands.add(new ServerCommand(complexCommand, iCommandListener2));
	}

	public void commandLineParser() {
		while(this.commands.size() > 0) {
			ServerCommand serverCommand1 = (ServerCommand)this.commands.remove(0);
			this.commandHandler.handleCommand(serverCommand1);
		}

	}

	public void addToOnlinePlayerList(IUpdatePlayerListBox iUpdatePlayerListBox1) {
		this.playersOnline.add(iUpdatePlayerListBox1);
	}

	public static void main(String[] string0) {
		try {
			MinecraftServer minecraftServer1 = new MinecraftServer();
			if(!GraphicsEnvironment.isHeadless() && (string0.length <= 0 || !string0[0].equals("nogui"))) {
				ServerGUI.initGui(minecraftServer1);
			}

			(new ThreadServerApplication("Server thread", minecraftServer1)).start();
		} catch (Exception exception2) {
			logger.log(Level.SEVERE, "Failed to start the minecraft server", exception2);
		}

	}

	public File getFile(String string1) {
		return new File(string1);
	}

	public void log(String string1) {
		logger.info(string1);
	}

	public void logWarning(String string1) {
		logger.warning(string1);
	}

	public String getUsername() {
		return "CONSOLE";
	}

	// Added the twilight forest & the Desert Dimension
	public WorldServer getWorldManager(int i1) {
		//return i1 == -1 ? this.worldMngr[1] : (i1 == 1 ? this.worldMngr[2] : this.worldMngr[0]);
		switch(i1) {
		case -1: return this.worldMngr[1];
		case 1: return this.worldMngr[2];
		case 7: return this.worldMngr[3];
		case 9: return this.worldMngr[4];
		default: return this.worldMngr[0];
		}
	}

	// Added the twilight forest & the Desert Dimension
	public EntityTracker getEntityTracker(int i1) {
		//return i1 == -1 ? this.entityTracker[1] : (i1 == 1 ? this.entityTracker[2] : this.entityTracker[0]);
		
		switch(i1) {
		case -1: return this.entityTracker[1];
		case 1: return this.entityTracker[2];
		case 7: return this.entityTracker[3];
		case 9: return this.entityTracker[4];
		default: return this.entityTracker[0];
		}
	}

	public int getIntProperty(String string1, int i2) {
		return this.propertyManagerObj.getIntProperty(string1, i2);
	}

	public String getStringProperty(String string1, String string2) {
		return this.propertyManagerObj.getStringProperty(string1, string2);
	}

	public void setProperty(String string1, Object object2) {
		this.propertyManagerObj.setProperty(string1, object2);
	}

	public void saveProperties() {
		this.propertyManagerObj.saveProperties();
	}

	public String getSettingsFilename() {
		File file1 = this.propertyManagerObj.getPropertiesFile();
		return file1 != null ? file1.getAbsolutePath() : "No settings file";
	}

	public String getHostname() {
		return this.hostname;
	}

	public int getPort() {
		return this.serverPort;
	}

	public String getMotd() {
		return this.motd;
	}

	public String getVersionString() {
		return "1.2.5";
	}

	public int playersOnline() {
		return this.configManager.playersOnline();
	}

	public int getMaxPlayers() {
		return this.configManager.getMaxPlayers();
	}

	public String[] getPlayerNamesAsList() {
		return this.configManager.getPlayerNamesAsList();
	}

	public String getWorldName() {
		return this.propertyManagerObj.getStringProperty("level-name", "world");
	}

	public String getPlugin() {
		return "";
	}

	public void s_func_40010_o() {
	}

	public String handleRConCommand(String string1) {
		RConConsoleSource.instance.resetLog();
		this.commandHandler.handleCommand(new ServerCommand(new ComplexCommand(string1, null), RConConsoleSource.instance));
		return RConConsoleSource.instance.getLogContents();
	}

	public boolean isDebuggingEnabled() {
		return false;
	}

	public void logSevere(String string1) {
		logger.log(Level.SEVERE, string1);
	}

	public void logIn(String string1) {
		if(this.isDebuggingEnabled()) {
			logger.log(Level.INFO, string1);
		}

	}

	public String[] getBannedIPsList() {
		return (String[])this.configManager.getBannedIPsList().toArray(new String[0]);
	}

	public String[] getBannedPlayersList() {
		return (String[])this.configManager.getBannedPlayersList().toArray(new String[0]);
	}

	public String s_func_52003_getServerModName() {
		return "vanilla";
	}

	public static boolean isServerRunning(MinecraftServer minecraftServer0) {
		return minecraftServer0.serverRunning;
	}
}
