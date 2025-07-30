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

import net.minecraft.src.AxisAlignedBB;
import net.minecraft.src.ChunkCoordinates;
import net.minecraft.src.ConsoleCommandHandler;
import net.minecraft.src.ConsoleLogManager;
import net.minecraft.src.ConvertProgressUpdater;
import net.minecraft.src.EntityTracker;
import net.minecraft.src.ICommandListener;
import net.minecraft.src.IProgressUpdate;
import net.minecraft.src.ISaveFormat;
import net.minecraft.src.IUpdatePlayerListBox;
import net.minecraft.src.NetworkListenThread;
import net.minecraft.src.Packet4UpdateTime;
import net.minecraft.src.PropertyManager;
import net.minecraft.src.SaveConverterMcRegion;
import net.minecraft.src.SaveOldDir;
import net.minecraft.src.ServerCommand;
import net.minecraft.src.ServerConfigurationManager;
import net.minecraft.src.ServerGUI;
import net.minecraft.src.StatList;
import net.minecraft.src.ThreadCommandReader;
import net.minecraft.src.ThreadServerApplication;
import net.minecraft.src.ThreadSleepForever;
import net.minecraft.src.Vec3D;
import net.minecraft.src.WorldManager;
import net.minecraft.src.WorldServer;
import net.minecraft.src.WorldServerMulti;

public class MinecraftServer implements Runnable, ICommandListener {
	public static Logger logger = Logger.getLogger("Minecraft");
	public static HashMap field_6037_b = new HashMap();
	public NetworkListenThread networkServer;
	public PropertyManager propertyManagerObj;
	public WorldServer[] worldMngr;
	public ServerConfigurationManager configManager;
	private ConsoleCommandHandler commandHandler;
	private boolean serverRunning = true;
	public boolean serverStopped = false;
	int deathTime = 0;
	public String currentTask;
	public int percentDone;
	private List field_9010_p = new ArrayList();
	private List commands = Collections.synchronizedList(new ArrayList());
	public EntityTracker[] entityTracker = new EntityTracker[2];
	public boolean onlineMode;
	public boolean spawnPeacefulMobs;
	public boolean pvpOn;
	public boolean allowFlight;

	public MinecraftServer() {
		new ThreadSleepForever(this);
	}

	private boolean startServer() throws UnknownHostException {
		this.commandHandler = new ConsoleCommandHandler(this);
		ThreadCommandReader threadCommandReader1 = new ThreadCommandReader(this);
		threadCommandReader1.setDaemon(true);
		threadCommandReader1.start();
		ConsoleLogManager.init();
		logger.info("Starting minecraft server version Beta 1.6.6");
		if(Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
			logger.warning("**** NOT ENOUGH RAM!");
			logger.warning("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
		}

		logger.info("Loading properties");
		this.propertyManagerObj = new PropertyManager(new File("server.properties"));
		String string2 = this.propertyManagerObj.getStringProperty("server-ip", "");
		this.onlineMode = this.propertyManagerObj.getBooleanProperty("online-mode", true);
		this.spawnPeacefulMobs = this.propertyManagerObj.getBooleanProperty("spawn-animals", true);
		this.pvpOn = this.propertyManagerObj.getBooleanProperty("pvp", true);
		this.allowFlight = this.propertyManagerObj.getBooleanProperty("allow-flight", false);
		InetAddress inetAddress3 = null;
		if(string2.length() > 0) {
			inetAddress3 = InetAddress.getByName(string2);
		}

		int i4 = this.propertyManagerObj.getIntProperty("server-port", 25565);
		logger.info("Starting Minecraft server on " + (string2.length() == 0 ? "*" : string2) + ":" + i4);

		try {
			this.networkServer = new NetworkListenThread(this, inetAddress3, i4);
		} catch (IOException iOException13) {
			logger.warning("**** FAILED TO BIND TO PORT!");
			logger.log(Level.WARNING, "The exception was: " + iOException13.toString());
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
		this.entityTracker[0] = new EntityTracker(this, 0);
		this.entityTracker[1] = new EntityTracker(this, -1);
		long j5 = System.nanoTime();
		String string7 = this.propertyManagerObj.getStringProperty("level-name", "world");
		String string8 = this.propertyManagerObj.getStringProperty("level-seed", "");
		long j9 = (new Random()).nextLong();
		if(string8.length() > 0) {
			try {
				j9 = Long.parseLong(string8);
			} catch (NumberFormatException numberFormatException12) {
				j9 = (long)string8.hashCode();
			}
		}

		logger.info("Preparing level \"" + string7 + "\"");
		this.initWorld(new SaveConverterMcRegion(new File(".")), string7, j9);
		logger.info("Done (" + (System.nanoTime() - j5) + "ns)! For help, type \"help\" or \"?\"");
		return true;
	}

	private void initWorld(ISaveFormat iSaveFormat1, String string2, long j3) {
		if(iSaveFormat1.isOldSaveType(string2)) {
			logger.info("Converting map!");
			iSaveFormat1.converMapToMCRegion(string2, new ConvertProgressUpdater(this));
		}

		this.worldMngr = new WorldServer[2];
		SaveOldDir saveOldDir5 = new SaveOldDir(new File("."), string2, true);

		for(int i6 = 0; i6 < this.worldMngr.length; ++i6) {
			if(i6 == 0) {
				this.worldMngr[i6] = new WorldServer(this, saveOldDir5, string2, i6 == 0 ? 0 : -1, j3);
			} else {
				this.worldMngr[i6] = new WorldServerMulti(this, saveOldDir5, string2, i6 == 0 ? 0 : -1, j3, this.worldMngr[0]);
			}

			this.worldMngr[i6].addWorldAccess(new WorldManager(this, this.worldMngr[i6]));
			this.worldMngr[i6].difficultySetting = this.propertyManagerObj.getBooleanProperty("spawn-monsters", true) ? 1 : 0;
			this.worldMngr[i6].setAllowedSpawnTypes(this.propertyManagerObj.getBooleanProperty("spawn-monsters", true), this.spawnPeacefulMobs);
			this.configManager.setPlayerManager(this.worldMngr);
		}

		short s18 = 196;
		long j7 = System.currentTimeMillis();

		for(int i9 = 0; i9 < this.worldMngr.length; ++i9) {
			logger.info("Preparing start region for level " + i9);
			if(i9 == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				WorldServer worldServer10 = this.worldMngr[i9];
				ChunkCoordinates chunkCoordinates11 = worldServer10.getSpawnPoint();

				for(int i12 = -s18; i12 <= s18 && this.serverRunning; i12 += 16) {
					for(int i13 = -s18; i13 <= s18 && this.serverRunning; i13 += 16) {
						long j14 = System.currentTimeMillis();
						if(j14 < j7) {
							j7 = j14;
						}

						if(j14 > j7 + 1000L) {
							int i16 = (s18 * 2 + 1) * (s18 * 2 + 1);
							int i17 = (i12 + s18) * (s18 * 2 + 1) + i13 + 1;
							this.outputPercentRemaining("Preparing spawn area", i17 * 100 / i16);
							j7 = j14;
						}

						worldServer10.chunkProviderServer.loadChunk(chunkCoordinates11.posX + i12 >> 4, chunkCoordinates11.posZ + i13 >> 4);

						while(worldServer10.func_6156_d() && this.serverRunning) {
						}
					}
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
			worldServer2.func_30007_w();
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
					if(this.worldMngr[0].isAllPlayersFullyAsleep()) {
						this.doTick();
						j3 = 0L;
					} else {
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
		ArrayList arrayList1 = new ArrayList();
		Iterator iterator2 = field_6037_b.keySet().iterator();

		while(iterator2.hasNext()) {
			String string3 = (String)iterator2.next();
			int i4 = ((Integer)field_6037_b.get(string3)).intValue();
			if(i4 > 0) {
				field_6037_b.put(string3, i4 - 1);
			} else {
				arrayList1.add(string3);
			}
		}

		int i6;
		for(i6 = 0; i6 < arrayList1.size(); ++i6) {
			field_6037_b.remove(arrayList1.get(i6));
		}

		AxisAlignedBB.clearBoundingBoxPool();
		Vec3D.initialize();
		++this.deathTime;

		for(i6 = 0; i6 < this.worldMngr.length; ++i6) {
			if(i6 == 0 || this.propertyManagerObj.getBooleanProperty("allow-nether", true)) {
				WorldServer worldServer7 = this.worldMngr[i6];
				if(this.deathTime % 20 == 0) {
					this.configManager.sendPacketToAllPlayersInDimension(new Packet4UpdateTime(worldServer7.getWorldTime()), worldServer7.worldProvider.worldType);
				}

				worldServer7.tick();

				while(worldServer7.func_6156_d()) {
				}

				worldServer7.updateEntities();
			}
		}

		this.networkServer.handleNetworkListenThread();
		this.configManager.onTick();

		for(i6 = 0; i6 < this.entityTracker.length; ++i6) {
			this.entityTracker[i6].updateTrackedEntities();
		}

		for(i6 = 0; i6 < this.field_9010_p.size(); ++i6) {
			((IUpdatePlayerListBox)this.field_9010_p.get(i6)).update();
		}

		try {
			this.commandLineParser();
		} catch (Exception exception5) {
			logger.log(Level.WARNING, "Unexpected exception while parsing console command", exception5);
		}

	}

	public void addCommand(String string1, ICommandListener iCommandListener2) {
		this.commands.add(new ServerCommand(string1, iCommandListener2));
	}

	public void commandLineParser() {
		while(this.commands.size() > 0) {
			ServerCommand serverCommand1 = (ServerCommand)this.commands.remove(0);
			this.commandHandler.handleCommand(serverCommand1);
		}

	}

	public void func_6022_a(IUpdatePlayerListBox iUpdatePlayerListBox1) {
		this.field_9010_p.add(iUpdatePlayerListBox1);
	}

	public static void main(String[] string0) {
		StatList.func_27092_a();

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

	public WorldServer getWorldManager(int i1) {
		return i1 == -1 ? this.worldMngr[1] : this.worldMngr[0];
	}

	public EntityTracker getEntityTracker(int i1) {
		return i1 == -1 ? this.entityTracker[1] : this.entityTracker[0];
	}

	public static boolean isServerRunning(MinecraftServer minecraftServer0) {
		return minecraftServer0.serverRunning;
	}
}
