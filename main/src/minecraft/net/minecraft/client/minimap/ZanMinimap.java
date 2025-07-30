package net.minecraft.client.minimap;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import net.minecraft.client.GameSettingsKeys;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConflictWarning;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMinimap;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.world.level.EnumSkyBlock;
import net.minecraft.world.level.World;
import net.minecraft.world.level.chunk.Chunk;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.tile.Block;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ZanMinimap implements Runnable {
	private Minecraft game;
	// private mod_MotionTracker motionTracker = null;
	private Boolean motionTrackerExists = false;
	private BufferedImage[] map = new BufferedImage[4];
	private int[] blockColors = new int[4096];
	private int q = 0;
	private Random generator = new Random();
	public int iMenu = 1;
	private boolean enabled = true;
	private boolean hide = true;
	private boolean showNether = false;
	private boolean showCaves = true;
	private boolean lfclick = false;
	private boolean full = false;
	public boolean active = false;
	private int zoom = 2;
	public String zmodver = "v0.9.7c";
	private String inStr = "";
	private String way = "";
	private int wayX = 0;
	private int wayZ = 0;
	private boolean rc = true;
	private String error = "";
	private String[][] sMenu = new String[2][13];
	private int ztimer = 0;
	private int timer = 0;
	private int fudge = 0;
	private int lastX = 0;
	private int lastZ = 0;
	private int lZoom = 0;
	private int next = 0;
	private int blink = 0;
	private int lastKey = 0;
	private float direction = 0.0F;
	private File settingsFile;
	private String world = "";
	private boolean scrClick = false;
	private int scrStart = 0;
	private int sMin = 0;
	private int sMax = 67;
	private int min = 0;
	private int zoomKey;
	private int menuKey;
	private boolean squareMap = true;
	public boolean oldNorth = true;
	private int northRotate = 0;
	private boolean coords = true;
	private boolean lightmap = false;
	private boolean heightmap = false;
	private boolean welcome = false;
	public ArrayList<Waypoint> wayPts;
	public Thread zCalc = new Thread(this);
	public static boolean threading;
	private boolean haveLoadedBefore;
	private Tessellator lDraw = Tessellator.instance;
	private FontRenderer lang;
	private RenderEngine renderEngine;
	public static ZanMinimap instance;
	private final File mcDataDir;

	public static File getAppDir(String app) {
		return Minecraft.getAppDir(app);
	}

	public void chatInfo(String s) {
		this.game.ingameGUI.addChatMessage(s);
	}

	public String getMapName() {
		return this.game.theWorld.worldInfo.getWorldName();
	}

	public String getServerName() {
		NetClientHandler netClientHandler = this.game.getSendQueue();
		if(netClientHandler == null) {
			return "unknown1";
		} else {
			Object networkManager = this.getPrivateField(netClientHandler, "g");
			if(networkManager == null) {
				return "unknown2";
			} else {
				Object oSocket = this.getPrivateField(networkManager, "h");
				if(oSocket != null && oSocket instanceof Socket) {
					@SuppressWarnings("resource")
					Socket sock = (Socket)oSocket;
					String serverName = sock.getInetAddress().getHostName();
					return serverName;
				} else {
					return "unknown3";
				}
			}
		}
	}

	public Object getPrivateField(Object o, String fieldName) {
		Field[] fields = o.getClass().getDeclaredFields();

		for(int i = 0; i < fields.length; ++i) {
			if(fieldName.equals(fields[i].getName())) {
				try {
					fields[i].setAccessible(true);
					return fields[i].get(o);
				} catch (IllegalAccessException illegalAccessException6) {
				}
			}
		}

		return null;
	}

	public void drawPre() {
		this.lDraw.startDrawingQuads();
	}

	public void drawPost() {
		this.lDraw.draw();
	}

	public void glah(int g) {
		this.renderEngine.deleteTexture(g);
	}

	public void ldrawone(int a, int b, double c, double d, double e) {
		this.lDraw.addVertexWithUV((double)a, (double)b, c, d, e);
	}

	public void ldrawtwo(double a, double b, double c) {
		this.lDraw.addVertex(a, b, c);
	}

	public void ldrawthree(double a, double b, double c, double d, double e) {
		this.lDraw.addVertexWithUV(a, b, c, d, e);
	}

	public int getMouseX(int scWidth) {
		return Mouse.getX() * (scWidth + 5) / this.game.displayWidth;
	}

	public int getMouseY(int scHeight) {
		return scHeight + 5 - Mouse.getY() * (scHeight + 5) / this.game.displayHeight - 1;
	}

	public void setMenuNull() {
		this.game.currentScreen = null;
	}

	public Object getMenu() {
		return this.game.currentScreen;
	}

	public void OnTickInGame(Minecraft mc) {
		this.northRotate = this.oldNorth ? 0 : 90;
		if(this.game == null) {
			this.game = mc;
		}
		
		// Get keys from game settings
		this.zoomKey = GameSettingsKeys.keyBindMapZoom.keyCode;
		this.menuKey = GameSettingsKeys.keyBindMapMenu.keyCode;

		/*if(this.motionTrackerExists.booleanValue() && this.motionTracker.activated) {
			this.motionTracker.OnTickInGame(mc);
		} else*/ {
			if(threading) {
				if(!this.zCalc.isAlive() && threading) {
					this.zCalc = new Thread(this);
					this.zCalc.start();
				}

				if(!(this.game.currentScreen instanceof GuiGameOver) && !(this.game.currentScreen instanceof GuiConflictWarning) && this.game.currentScreen != null) {
					try {
						this.zCalc.notify();
					} catch (Exception exception5) {
					}
				}
			} else if(!threading && this.enabled && !this.hide && (this.lastX != this.xCoord() || this.lastZ != this.zCoord() || this.timer > 300)) {
				this.mapCalc();
			}

			if(this.lang == null) {
				this.lang = this.game.fontRenderer;
			}

			if(this.renderEngine == null) {
				this.renderEngine = this.game.renderEngine;
			}

			ScaledResolution scSize = new ScaledResolution(this.game.displayWidth, this.game.displayHeight);
			int scWidth = scSize.getScaledWidth();
			int scHeight = scSize.getScaledHeight();
			if(Keyboard.isKeyDown(this.menuKey) && this.game.currentScreen == null) {
				this.iMenu = 2;
				this.game.displayGuiScreen(new GuiMinimap()); // was: GuiScreen()
			}

			if(Keyboard.isKeyDown(this.zoomKey) && this.game.currentScreen == null && (this.showNether || this.game.thePlayer.dimension != -1)) {
				this.SetZoom();
			}

			this.loadWaypoints();
			if(this.iMenu == 1 && !this.welcome) {
				this.iMenu = 0;
			}

			if(!(this.game.currentScreen instanceof GuiIngameMenu) /* && !Keyboard.isKeyDown(Keyboard.KEY_F3)*/) {
				this.enabled = true;
			} else {
				this.enabled = false;
			}

			if(this.game.currentScreen == null && this.iMenu > 1) {
				this.iMenu = 0;
			}

			scWidth -= 5;
			scHeight -= 5;
			this.direction = -this.radius();
			if(this.direction >= 360.0F) {
				while(this.direction >= 360.0F) {
					this.direction -= 360.0F;
				}
			}

			if(this.direction < 0.0F) {
				while(this.direction < 0.0F) {
					this.direction += 360.0F;
				}
			}

			if(!this.error.equals("") && this.ztimer == 0) {
				this.ztimer = 500;
			}

			if(this.ztimer > 0) {
				--this.ztimer;
			}

			if(this.fudge > 0) {
				--this.fudge;
			}

			if(this.ztimer == 0 && !this.error.equals("")) {
				this.error = "";
			}

			if(this.enabled) {
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDepthMask(false);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ZERO);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				if(this.showNether || this.game.thePlayer.dimension != -1) {
					if(this.full) {
						this.renderMapFull(scWidth, scHeight);
					} else {
						this.renderMap(scWidth);
					}
				}

				if(this.ztimer > 0) {
					this.write(this.error, 20, 20, 0xFFFFFF);
				}

				if(this.iMenu > 0) {
					this.showMenu(scWidth, scHeight);
				}

				GL11.glDepthMask(true);
				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				if((this.showNether || this.game.thePlayer.dimension != -1) && this.coords) {
					this.showCoords(scWidth, scHeight);
				}
			}

		}
	}

	private int chkLen(String paramStr) {
		return this.lang.getStringWidth(paramStr);
	}

	private void write(String paramStr, int paramInt1, int paramInt2, int paramInt3) {
		this.lang.drawStringWithShadow(paramStr, paramInt1, paramInt2, paramInt3);
	}

	private int xCoord() {
		return (int)(this.game.thePlayer.posX < 0.0D ? this.game.thePlayer.posX - 1.0D : this.game.thePlayer.posX);
	}

	private int zCoord() {
		return (int)(this.game.thePlayer.posZ < 0.0D ? this.game.thePlayer.posZ - 1.0D : this.game.thePlayer.posZ);
	}

	private int yCoord() {
		return (int)this.game.thePlayer.posY;
	}

	private float radius() {
		return this.game.thePlayer.rotationYaw;
	}

	private String dCoord(int paramInt1) {
		return paramInt1 < 0 ? "-" + Math.abs(paramInt1 + 1) : "+" + paramInt1;
	}

	private int tex(BufferedImage paramImg) {
		return this.renderEngine.allocateAndSetupTexture(paramImg);
	}

	private int img(String paramStr) {
		return this.renderEngine.getTexture(paramStr);
	}

	private void disp(int paramInt) {
		this.renderEngine.bindTexture(paramInt);
	}

	public World getWorld() {
		return this.game.theWorld;
	}

	private final int getBlockHeightNether(World world, int x, int z, int starty) {
		int y = starty;
		if(Block.lightOpacity[world.getBlockId(x, starty, z)] == 0) {
			while(y > 0) {
				--y;
				if(Block.lightOpacity[world.getBlockId(x, y, z)] > 0) {
					return y + 1;
				}
			}
		} else {
			while(y <= starty + 10 && y < 127) {
				++y;
				if(Block.lightOpacity[world.getBlockId(x, y, z)] == 0) {
					return y;
				}
			}
		}

		return -1;
	}

	private void mapCalcNether() {
		World data = this.getWorld();
		int skylightsubtract = data.calculateSkylightSubtracted(1.0F);
		this.lZoom = this.zoom;
		int multi = (int)Math.pow(2.0D, (double)this.lZoom);
		int startX = this.xCoord();
		int startZ = this.zCoord();
		this.lastX = startX;
		this.lastZ = startZ;
		startX -= 16 * multi;
		startZ -= 16 * multi;
		boolean solidNether = false;

		for(int imageY = 0; imageY < 32 * multi; ++imageY) {
			for(int imageX = 0; imageX < 32 * multi; ++imageX) {
				int i18 = 0;
				boolean check = false;
				if(Math.sqrt((double)((16 * multi - imageY) * (16 * multi - imageY) + (16 * multi - imageX) * (16 * multi - imageX))) < (double)(16 * multi - (int)Math.sqrt((double)multi))) {
					check = true;
				}

				int i19 = this.getBlockHeightNether(data, startX + imageX, startZ + imageY, this.yCoord());
				if(i19 == -1) {
					i19 = this.yCoord() + 1;
					solidNether = true;
				} else {
					solidNether = false;
				}

				if(check || this.squareMap || this.full) {
					if(this.rc) {
						if(data.getBlockMaterial(startX + imageX, i19, startZ + imageY) != Material.snow && data.getBlockMaterial(startX + imageX, i19, startZ + imageY) != Material.craftedSnow) {
							i18 = this.getBlockColor(data.getBlockId(startX + imageX, i19 - 1, startZ + imageY), data.getBlockMetadata(startX + imageX, i19 - 1, startZ + imageY));
						} else {
							i18 = 0xFFFFFF;
						}
					} else {
						i18 = 0xFFFFFF;
					}
				}

				if(i18 != this.blockColors[0] && i18 != 0 && (check || this.squareMap || this.full)) {
					int i3;
					if(this.heightmap) {
						i3 = i19 - this.yCoord();
						double sc = Math.log10((double)Math.abs(i3) / 8.0D + 1.0D) / 1.3D;
						int r = i18 / 65536;
						int g = (i18 - r * 65536) / 256;
						int b = i18 - r * 65536 - g * 256;
						if(i3 >= 0) {
							r += (int)(sc * (double)(255 - r));
							g += (int)(sc * (double)(255 - g));
							b += (int)(sc * (double)(255 - b));
						} else {
							i3 = Math.abs(i3);
							r -= (int)(sc * (double)r);
							g -= (int)(sc * (double)g);
							b -= (int)(sc * (double)b);
						}

						i18 = r * 65536 + g * 256 + b;
					}

					i3 = 255;
					if(this.lightmap) {
						i3 = this.calcLightSMPtoo(startX + imageX, i19, startZ + imageY, skylightsubtract) * 17;
					} else if(solidNether) {
						i3 = 32;
					}

					if(i3 > 255) {
						i3 = 255;
					}

					if(i3 < 76 && !solidNether) {
						i3 = 76;
					} else if(i3 < 32) {
						i3 = 32;
					}

					i18 += i3 * 16777216;
				}

				this.map[this.lZoom].setRGB(imageX, imageY, i18);
			}
		}

	}

	private void mapCalcOverworld() {
		World data = this.getWorld();
		int skylightsubtract = data.calculateSkylightSubtracted(1.0F);
		this.lZoom = this.zoom;
		int multi = (int)Math.pow(2.0D, (double)this.lZoom);
		int startX = this.xCoord();
		int startZ = this.zCoord();
		this.lastX = startX;
		this.lastZ = startZ;
		startX -= 16 * multi;
		startZ -= 16 * multi;
		int multiX32 = multi << 5;
		for(int imageY = 0; imageY < multiX32; ++imageY) {
			for(int imageX = 0; imageX < multiX32; ++imageX) {
				int pixelColor = 0;
				int blockID = 0;
				boolean check = false;
				if(!this.squareMap) {
				if(Math.sqrt((double)((16 * multi - imageY) * (16 * multi - imageY) + (16 * multi - imageX) * (16 * multi - imageX))) < (double)(16 * multi - (int)Math.sqrt((double)multi))) {
					check = true;
				}
				}

				int height = data.getHeightValue(startX + imageX, startZ + imageY);
				//int landSurfaceHeight = data.getLandSurfaceHeightValue(startX + imageX, startZ + imageY);

				int surfaceMeta = 0;

				if(check || this.squareMap || this.full) {
					if(this.rc) {
						if(data.getBlockMaterial(startX + imageX, height, startZ + imageY) != Material.snow /* && data.getBlockMaterial(startX + imageX, i18, startZ + imageY) != Material.craftedSnow*/) {
							blockID = data.getBlockId(startX + imageX, height - 1, startZ + imageY);
							
							/*
							Block block = Block.blocksList[blockID];
							if(block != null && block.blockMaterial == Material.water) {
								blockID = data.getBlockId(startX + imageX, landSurfaceHeight - 1, startZ + imageY);
								surfaceMeta = data.getBlockMetadata(startX + imageX, landSurfaceHeight - 1, startZ + imageY);
								pixelColor = this.getBlockColor(blockID, surfaceMeta);

								pixelColor = pixelColor & 0xFFFF00 | 0xFE; 
							} else*/ {
								surfaceMeta = data.getBlockMetadata(startX + imageX, height - 1, startZ + imageY);
								pixelColor = this.getBlockColor(blockID, surfaceMeta);
							}
						} else {
							pixelColor = 0xFFFFFF;
						}
					} else {
						pixelColor = 0xFFFFFF;
					}
				}
				
				if(pixelColor != this.blockColors[0] && pixelColor != 0 && (check || this.squareMap || this.full)) {
					int i3;
					if(this.heightmap) {
						i3 = height - this.yCoord();
						double sc = Math.log10((double)Math.abs(i3) / 8.0D + 1.0D) / 1.3D;
						int r = pixelColor >> 16;
						int g = (pixelColor >> 8) & 255;
						int b = pixelColor & 255;
						if(i3 >= 0) {
							r += (int)(sc * (double)(255 - r));
							g += (int)(sc * (double)(255 - g));
							b += (int)(sc * (double)(255 - b));
						} else {
							i3 = Math.abs(i3);
							r -= (int)(sc * (double)r);
							g -= (int)(sc * (double)g);
							b -= (int)(sc * (double)b);
						}

						//pixelColor = r * 65536 + g * 256 + b;
						pixelColor = (r << 16) | (g << 8) | b;
					}

					i3 = 255;
					if(this.lightmap) {
						i3 = this.calcLightSMPtoo(startX + imageX, height, startZ + imageY, skylightsubtract) * 17;

					if(i3 > 255) {
						i3 = 255;
					}

					if(i3 < 32) {
						i3 = 32;
					}

						pixelColor += i3 * 16777216;
					} else {
						pixelColor |= 0xFF000000;
					}

				} else {
					// Chivate:
					// System.out.println (pixelColor + " for " + surfaceBlock + " h " + height + " lsh " + landSurfaceHeight);
				}

				this.map[this.lZoom].setRGB(imageX, imageY, pixelColor);
			}
		}

	}

	private void mapCalc() {
		if(this.game.thePlayer.dimension != -1) {
			if((this.showCaves && this.getWorld().getChunkFromBlockCoords(this.xCoord(), this.zCoord()).getSavedLightValue(EnumSkyBlock.Sky, this.xCoord() & 15, this.yCoord(), this.zCoord() & 15) <= 0)) {
				this.mapCalcNether();
			} else {
				this.mapCalcOverworld();
			}
		} else if(this.showNether) {
			this.mapCalcNether();
		}

	}

	private int calcLightSMPtoo(int x, int y, int z, int skylightsubtract) {
		World data = this.getWorld();
		Chunk chunk = data.getChunkFromChunkCoords(x >> 4, z >> 4);
		return chunk.getBlockLightValue(x &= 15, y, z &= 15, skylightsubtract);
	}

	public void run() {
		if(this.game != null) {
			while(true) {
				while(!threading) {
					try {
						Thread.sleep(1000L);
					} catch (Exception exception4) {
					}

					try {
						this.zCalc.wait(0L);
					} catch (Exception exception3) {
					}
				}

				for(this.active = true; this.game.thePlayer != null && this.active; this.active = false) {
					if(this.enabled && !this.hide && (this.lastX != this.xCoord() || this.lastZ != this.zCoord() || this.timer > 300)) {
						try {
							this.mapCalc();
							this.timer = 1;
						} catch (Exception exception2) {
						}
					}

					++this.timer;
				}

				this.active = false;

				try {
					Thread.sleep(10L);
				} catch (Exception exception6) {
				}

				try {
					this.zCalc.wait(0L);
				} catch (Exception exception5) {
				}
			}
		}

	}

	public ZanMinimap(Minecraft mc, File mcDataDir) {
		instance = this;
		this.mcDataDir = mcDataDir;
		
		/*
		if(this.classExists("mod_MotionTracker")) {
			this.motionTracker = new mod_MotionTracker();
			this.motionTrackerExists = true;
		}
		*/

		threading = false;
		this.zCalc.start();
		this.map[0] = new BufferedImage(32, 32, 2);
		this.map[1] = new BufferedImage(64, 64, 2);
		this.map[2] = new BufferedImage(128, 128, 2);
		this.map[3] = new BufferedImage(256, 256, 2);

		int e;
		int i;
		for(e = 0; e < 2; ++e) {
			for(i = 0; i < 13; ++i) {
				this.sMenu[e][i] = "";
			}
		}

		this.sMenu[0][0] = "\u00a74Zan\'s\u00a7F Mod! " + this.zmodver;
		this.sMenu[0][1] = "Welcome to Zan\'s Minimap, there are a";
		this.sMenu[0][2] = "number of features and commands available to you.";
		this.sMenu[0][3] = "- Press \u00a7B" + Keyboard.getKeyName(this.zoomKey) + " \u00a7Fto zoom in/out, or \u00a7B" + Keyboard.getKeyName(this.menuKey) + "\u00a7F for options.";
		this.sMenu[1][0] = "Options";
		this.sMenu[1][1] = "Display Coordinates:";
		this.sMenu[1][2] = "Hide Minimap:";
		this.sMenu[1][3] = "Function in Nether:";
		this.sMenu[1][4] = "Enable Cave Mode:";
		this.sMenu[1][5] = "Dynamic Lighting:";
		this.sMenu[1][6] = "Terrain Depth:";
		this.sMenu[1][7] = "Square Map:";
		this.sMenu[1][8] = "Old North:";
		this.sMenu[1][9] = "Welcome Screen:";
		this.sMenu[1][10] = "Threading:";
		if(this.motionTrackerExists.booleanValue()) {
			this.sMenu[1][11] = "Radar Mode:";
		}

		//this.settingsFile = new File(getAppDir("minecraft"), "zan.settings");
		this.settingsFile = new File(this.mcDataDir, "zan.settings");
		
		// Changed: zoom / menu keys saved / edited / loaded in game settings.
		String[] meta;
		BufferedReader bufferedReader9;
		String string10;
		try {
			if(this.settingsFile.exists()) {
				
				bufferedReader9 = new BufferedReader(new FileReader(this.settingsFile));
				this.haveLoadedBefore = false;

				while((string10 = bufferedReader9.readLine()) != null) {
					meta = string10.split(":");
					if(meta[0].equals("Show Minimap")) {
						this.squareMap = Boolean.parseBoolean(meta[1]);
					}

					if(meta[0].equals("Old North")) {
						this.oldNorth = Boolean.parseBoolean(meta[1]);
					} else if(meta[0].equals("Show Map in Nether")) {
						this.showNether = Boolean.parseBoolean(meta[1]);
					} else if(meta[0].equals("Enable Cave Mode")) {
						this.showCaves = Boolean.parseBoolean(meta[1]);
					} else if(meta[0].equals("Show Coordinates")) {
						this.coords = Boolean.parseBoolean(meta[1]);
					} else if(meta[0].equals("Dynamic Lighting")) {
						this.lightmap = Boolean.parseBoolean(meta[1]);
					} else if(meta[0].equals("Terrain Depth")) {
						this.heightmap = Boolean.parseBoolean(meta[1]);
					} else if(meta[0].equals("Welcome Message")) {
						this.welcome = Boolean.parseBoolean(meta[1]);
					} /*else if(meta[0].equals("Zoom Key")) {
						this.zoomKey = Keyboard.getKeyIndex(meta[1]);
					} else if(meta[0].equals("Menu Key")) {
						this.menuKey = Keyboard.getKeyIndex(meta[1]);
					} */else if(meta[0].equals("Threading")) {
						this.haveLoadedBefore = true;
						threading = Boolean.parseBoolean(meta[1]);
					} else if(meta[0].equals("Hide")) {
						this.hide = Boolean.parseBoolean(meta[1]);
					}
				}

				bufferedReader9.close();
			}
		} catch (Exception exception8) {
		}

		if(!this.haveLoadedBefore) {
			this.saveAll();
		}

		for(e = 0; e < this.blockColors.length; ++e) {
			this.blockColors[e] = 0xFF01FF;
		}

		//this.settingsFile = new File(getAppDir("minecraft"), "colours.txt");
		this.settingsFile = new File(this.mcDataDir, "colours.txt");
		
		PrintWriter printWriter11 = null;
		
		try {
			this.blockColors[this.blockColorID(1, 0)] = 6842472;
			this.blockColors[this.blockColorID(2, 0)] = 7648330;
			this.blockColors[this.blockColorID(3, 0)] = 7951674;
			this.blockColors[this.blockColorID(4, 0)] = 9803157;
			this.blockColors[this.blockColorID(5, 0)] = 12359778;
			this.blockColors[this.blockColorID(6, 0)] = 9724968;
			this.blockColors[this.blockColorID(7, 0)] = 3355443;
			this.blockColors[this.blockColorID(8, 0)] = 3299071;
			this.blockColors[this.blockColorID(8, 1)] = 3299071;
			this.blockColors[this.blockColorID(8, 2)] = 3299071;
			this.blockColors[this.blockColorID(8, 3)] = 3299071;
			this.blockColors[this.blockColorID(8, 4)] = 3299071;
			this.blockColors[this.blockColorID(8, 5)] = 3299071;
			this.blockColors[this.blockColorID(8, 6)] = 3299071;
			this.blockColors[this.blockColorID(8, 7)] = 3299071;
			this.blockColors[this.blockColorID(9, 0)] = 3299071;
			this.blockColors[this.blockColorID(10, 0)] = 14181652;
			this.blockColors[this.blockColorID(10, 1)] = 14116116;
			this.blockColors[this.blockColorID(10, 2)] = 14050324;
			this.blockColors[this.blockColorID(10, 3)] = 13984788;
			this.blockColors[this.blockColorID(10, 4)] = 13918996;
			this.blockColors[this.blockColorID(10, 5)] = 13853460;
			this.blockColors[this.blockColorID(10, 6)] = 13787668;
			this.blockColors[this.blockColorID(11, 0)] = 14247188;
			this.blockColors[this.blockColorID(12, 0)] = 14538656;
			this.blockColors[this.blockColorID(13, 0)] = 7631988;
			this.blockColors[this.blockColorID(14, 0)] = 7631988;
			this.blockColors[this.blockColorID(15, 0)] = 7631988;
			this.blockColors[this.blockColorID(16, 0)] = 7631988;
			this.blockColors[this.blockColorID(17, 0)] = 3418393;
			this.blockColors[this.blockColorID(17, 1)] = 3418393;
			this.blockColors[this.blockColorID(17, 2)] = 3418393;
			this.blockColors[this.blockColorID(18, 0)] = 1461516;
			this.blockColors[this.blockColorID(18, 1)] = 1461516;
			this.blockColors[this.blockColorID(18, 2)] = 1461516;
			this.blockColors[this.blockColorID(18, 3)] = 1461516;
			this.blockColors[this.blockColorID(19, 0)] = 15066446;
			this.blockColors[this.blockColorID(20, 0)] = 0xFFFFFF;
			this.blockColors[this.blockColorID(21, 0)] = 6779015;
			this.blockColors[this.blockColorID(22, 0)] = 863922;
			this.blockColors[this.blockColorID(23, 0)] = 7631988;
			this.blockColors[this.blockColorID(24, 0)] = 13024621;
			this.blockColors[this.blockColorID(25, 0)] = 9398557;
			this.blockColors[this.blockColorID(35, 0)] = 16053492;
			this.blockColors[this.blockColorID(35, 1)] = 15434814;
			this.blockColors[this.blockColorID(35, 2)] = 12934351;
			this.blockColors[this.blockColorID(35, 3)] = 8232154;
			this.blockColors[this.blockColorID(35, 4)] = 14537018;
			this.blockColors[this.blockColorID(35, 5)] = 4115249;
			this.blockColors[this.blockColorID(35, 6)] = 14719661;
			this.blockColors[this.blockColorID(35, 7)] = 4408131;
			this.blockColors[this.blockColorID(35, 8)] = 11513775;
			this.blockColors[this.blockColorID(35, 9)] = 3113606;
			this.blockColors[this.blockColorID(35, 10)] = 9455057;
			this.blockColors[this.blockColorID(35, 11)] = 2964391;
			this.blockColors[this.blockColorID(35, 12)] = 5713942;
			this.blockColors[this.blockColorID(35, 13)] = 4282399;
			this.blockColors[this.blockColorID(35, 14)] = 11676711;
			this.blockColors[this.blockColorID(35, 15)] = 1775383;
			this.blockColors[this.blockColorID(37, 0)] = 15857922;
			this.blockColors[this.blockColorID(38, 0)] = 16189199;
			this.blockColors[this.blockColorID(39, 0)] = 9530709;
			this.blockColors[this.blockColorID(40, 0)] = 10098460;
			this.blockColors[this.blockColorID(41, 0)] = 16710493;
			this.blockColors[this.blockColorID(42, 0)] = 15329769;
			this.blockColors[this.blockColorID(43, 0)] = 11053224;
			this.blockColors[this.blockColorID(43, 1)] = 13024621;
			this.blockColors[this.blockColorID(43, 2)] = 12359778;
			this.blockColors[this.blockColorID(43, 3)] = 9803157;
			this.blockColors[this.blockColorID(43, 4)] = 11162683;
			this.blockColors[this.blockColorID(43, 5)] = 8026746;
			this.blockColors[this.blockColorID(43, 6)] = 11053224;
			this.blockColors[this.blockColorID(44, 0)] = 11053224;
			this.blockColors[this.blockColorID(44, 1)] = 13024621;
			this.blockColors[this.blockColorID(44, 2)] = 12359778;
			this.blockColors[this.blockColorID(44, 3)] = 9803157;
			this.blockColors[this.blockColorID(44, 4)] = 11162683;
			this.blockColors[this.blockColorID(44, 5)] = 8026746;
			this.blockColors[this.blockColorID(44, 6)] = 11053224;
			this.blockColors[this.blockColorID(45, 0)] = 11162683;
			this.blockColors[this.blockColorID(46, 0)] = 14369818;
			this.blockColors[this.blockColorID(47, 0)] = 11833434;
			this.blockColors[this.blockColorID(48, 0)] = 2049823;
			this.blockColors[this.blockColorID(49, 0)] = 1052696;
			this.blockColors[this.blockColorID(50, 0)] = 16766976;
			this.blockColors[this.blockColorID(51, 0)] = 12605953;
			this.blockColors[this.blockColorID(52, 0)] = 2514823;
			this.blockColors[this.blockColorID(53, 0)] = 12359778;
			this.blockColors[this.blockColorID(53, 1)] = 12359778;
			this.blockColors[this.blockColorID(53, 2)] = 12359778;
			this.blockColors[this.blockColorID(53, 3)] = 12359778;
			this.blockColors[this.blockColorID(54, 0)] = 9398557;
			this.blockColors[this.blockColorID(55, 0)] = 4718592;
			this.blockColors[this.blockColorID(56, 0)] = 7631988;
			this.blockColors[this.blockColorID(57, 0)] = 8578272;
			this.blockColors[this.blockColorID(58, 0)] = 10644286;
			this.blockColors[this.blockColorID(59, 0)] = 57872;
			this.blockColors[this.blockColorID(60, 0)] = 6504228;
			this.blockColors[this.blockColorID(61, 0)] = 7631988;
			this.blockColors[this.blockColorID(62, 0)] = 7631988;
			this.blockColors[this.blockColorID(63, 0)] = 11833434;
			this.blockColors[this.blockColorID(64, 0)] = 8018731;
			this.blockColors[this.blockColorID(65, 0)] = 11307090;
			this.blockColors[this.blockColorID(66, 0)] = 10790052;
			this.blockColors[this.blockColorID(67, 0)] = 10395294;
			this.blockColors[this.blockColorID(67, 1)] = 10395294;
			this.blockColors[this.blockColorID(67, 2)] = 10395294;
			this.blockColors[this.blockColorID(67, 3)] = 10395294;
			this.blockColors[this.blockColorID(68, 0)] = 10454093;
			this.blockColors[this.blockColorID(69, 0)] = 6902835;
			this.blockColors[this.blockColorID(70, 0)] = 9408399;
			this.blockColors[this.blockColorID(71, 0)] = 12698049;
			this.blockColors[this.blockColorID(72, 0)] = 12359778;
			this.blockColors[this.blockColorID(73, 0)] = 7631988;
			this.blockColors[this.blockColorID(74, 0)] = 7631988;
			this.blockColors[this.blockColorID(75, 0)] = 2686976;
			this.blockColors[this.blockColorID(76, 0)] = 16580608;
			this.blockColors[this.blockColorID(77, 0)] = 7631988;
			this.blockColors[this.blockColorID(78, 0)] = 16515071;
			this.blockColors[this.blockColorID(79, 0)] = 9355263;
			this.blockColors[this.blockColorID(80, 0)] = 0xFFFFFF;
			this.blockColors[this.blockColorID(81, 0)] = 1146910;
			this.blockColors[this.blockColorID(82, 0)] = 0xFFFFFF;
			this.blockColors[this.blockColorID(83, 0)] = 10594226;
			this.blockColors[this.blockColorID(84, 0)] = 9398557;
			this.blockColors[this.blockColorID(85, 0)] = 10184267;
			this.blockColors[this.blockColorID(86, 0)] = 12359778;
			this.blockColors[this.blockColorID(87, 0)] = 5775896;
			this.blockColors[this.blockColorID(88, 0)] = 10053425;
			this.blockColors[this.blockColorID(89, 0)] = 13477944;
			this.blockColors[this.blockColorID(90, 0)] = 7545990;
			this.blockColors[this.blockColorID(91, 0)] = 16763021;
			this.blockColors[this.blockColorID(92, 0)] = 14929101;
			this.blockColors[this.blockColorID(93, 0)] = 9933715;
			this.blockColors[this.blockColorID(94, 0)] = 12620691;
			this.blockColors[this.blockColorID(95, 0)] = 9398557;
			this.blockColors[this.blockColorID(96, 0)] = 8281389;
			this.blockColors[this.blockColorID(97, 0)] = 6842472;
			this.blockColors[this.blockColorID(98, 0)] = 8026746;
			this.blockColors[this.blockColorID(98, 1)] = 2049823;
			this.blockColors[this.blockColorID(98, 2)] = 8026746;
			this.blockColors[this.blockColorID(99, 0)] = 13282168;
			this.blockColors[this.blockColorID(100, 0)] = 13282168;
			this.blockColors[this.blockColorID(101, 0)] = 7171178;
			this.blockColors[this.blockColorID(102, 0)] = 0xFFFFFF;
			this.blockColors[this.blockColorID(103, 0)] = 9935140;
			this.blockColors[this.blockColorID(104, 0)] = 39168;
			this.blockColors[this.blockColorID(105, 0)] = 39168;
			this.blockColors[this.blockColorID(106, 0)] = 2051594;
			this.blockColors[this.blockColorID(107, 0)] = 12359778;
			this.blockColors[this.blockColorID(108, 0)] = 11162683;
			this.blockColors[this.blockColorID(108, 1)] = 11162683;
			this.blockColors[this.blockColorID(108, 2)] = 11162683;
			this.blockColors[this.blockColorID(108, 3)] = 11162683;
			this.blockColors[this.blockColorID(109, 0)] = 8026746;
			this.blockColors[this.blockColorID(109, 1)] = 8026746;
			this.blockColors[this.blockColorID(109, 2)] = 8026746;
			this.blockColors[this.blockColorID(109, 3)] = 8026746;
			this.blockColors[this.blockColorID(110, 0)] = 7234666;
			this.blockColors[this.blockColorID(112, 0)] = 4400687;
			this.blockColors[this.blockColorID(114, 0)] = 4400687;
			this.blockColors[this.blockColorID(114, 1)] = 4400687;
			this.blockColors[this.blockColorID(114, 2)] = 4400687;
			this.blockColors[this.blockColorID(114, 3)] = 4400687;
			this.blockColors[this.blockColorID(121, 0)] = 13884580;
			this.blockColors[this.blockColorID(123, 0)] = 9398557;
			this.blockColors[this.blockColorID(124, 0)] = 13477944;
			
			// r1.3 blocks:
			this.blockColors[this.blockColorID(128, 0)] = 0xDED6A5;
			this.blockColors[this.blockColorID(131, 0)] = 0x979797;
			this.blockColors[this.blockColorID(132, 0)] = 0x979797;
			
			// Custom blocks:

			// Infhell
			this.blockColors[this.blockColorID(133, 0)] = 0xEEEEEE;
			this.blockColors[this.blockColorID(134, 0)] = 0x525D3F;
			this.blockColors[this.blockColorID(135, 0)] = 0x7F9C50;
			this.blockColors[this.blockColorID(136, 0)] = 0x333333;
			this.blockColors[this.blockColorID(137, 0)] = 0x111111;
			this.blockColors[this.blockColorID(138, 0)] = 0xFFCA14;
			
			// ExtraBiomesXL
			this.blockColors[this.blockColorID(140, 0)] = 0x2DAA1D;
			this.blockColors[this.blockColorID(141, 0)] = 0xFF9806;
			this.blockColors[this.blockColorID(142, 0)] = 0x3BD617;
			this.blockColors[this.blockColorID(143, 0)] = 0x668F4D;
			this.blockColors[this.blockColorID(144, 0)] = 0x55C5FF;
			this.blockColors[this.blockColorID(145, 0)] = 0xE4C478;
			this.blockColors[this.blockColorID(146, 0)] = 0xFF9936;
			this.blockColors[this.blockColorID(147, 0)] = 0x6FCC52;
			this.blockColors[this.blockColorID(148, 0)] = 0x4B9634;
			this.blockColors[this.blockColorID(149, 0)] = 0xF4CC51;
			
			// BoP
			this.blockColors[this.blockColorID(150, 0)] = 0x53410A;
			this.blockColors[this.blockColorID(151, 0)] = 0xA68D3F;	// Daylight sensor
			this.blockColors[this.blockColorID(152, 0)] = 0x94DE74;
			
			this.blockColors[this.blockColorID(153, 0)] = 5775896; // Ore quartz
			
			this.blockColors[this.blockColorID(154, 0)] = 0x508E58;
			
			this.blockColors[this.blockColorID(155, 0)] = 0xF0EEE8; // Quartz block
			this.blockColors[this.blockColorID(156, 0)] = 0xF0EEE8;	// Stairs quartz
			
			this.blockColors[this.blockColorID(157, 0)] = 0x69B093;
			this.blockColors[this.blockColorID(158, 0)] = 0x85B069;
			this.blockColors[this.blockColorID(159, 0)] = 0xFC6036;
			this.blockColors[this.blockColorID(160, 0)] = 0x474747;
			this.blockColors[this.blockColorID(161, 0)] = 0x9B7442;
			this.blockColors[this.blockColorID(162, 0)] = 0x8C8C8C;
			this.blockColors[this.blockColorID(163, 0)] = 0xFFDA2F;
			this.blockColors[this.blockColorID(164, 0)] = 0xFDE472;
			this.blockColors[this.blockColorID(165, 0)] = 0xFFF6CD;
			
			// Twilight
			this.blockColors[this.blockColorID(180, 0)] = 0xFC00FF;
			this.blockColors[this.blockColorID(181, 0)] = 0x0CFF00;
			this.blockColors[this.blockColorID(182, 0)] = 0x388734;
			this.blockColors[this.blockColorID(183, 0)] = 0x5DA25A;
			this.blockColors[this.blockColorID(184, 0)] = 0x625247;
			this.blockColors[this.blockColorID(185, 0)] = 0x5B5B5B;
			this.blockColors[this.blockColorID(186, 0)] = 0xBE5F5F;
			this.blockColors[this.blockColorID(187, 0)] = 0x975FBE;
			this.blockColors[this.blockColorID(188, 0)] = 0x373737;
			
			// Coral
			this.blockColors[this.blockColorID(190, 0)] = 0xFFD800;
			this.blockColors[this.blockColorID(191, 0)] = 0xFF6C00;
			this.blockColors[this.blockColorID(192, 0)] = 0xCB9939;
			this.blockColors[this.blockColorID(193, 0)] = 0xFF00AE;
			this.blockColors[this.blockColorID(194, 0)] = 0x006CFF;
			
			// Copper
			this.blockColors[this.blockColorID(195, 0)] = 0xE5703B;
			this.blockColors[this.blockColorID(196, 0)] = 0xE5703B;
			
			// More Twilight
			this.blockColors[this.blockColorID(197, 0)] = 0xFFB137;
			this.blockColors[this.blockColorID(198, 0)] = 0x858D85;
			this.blockColors[this.blockColorID(198, 1)] = 0x727D72;
			this.blockColors[this.blockColorID(198, 2)] = 0x8C998C;
			this.blockColors[this.blockColorID(198, 3)] = 0x727D72;
			this.blockColors[this.blockColorID(198, 4)] = 0x727D72;
			this.blockColors[this.blockColorID(198, 5)] = 0x727D72;
			this.blockColors[this.blockColorID(198, 6)] = 0x707074;
			this.blockColors[this.blockColorID(198, 7)] = 0x707074;
			
			this.blockColors[this.blockColorID(199, 0)] = 0x6970A7;
			this.blockColors[this.blockColorID(200, 0)] = 0x55A830;
			this.blockColors[this.blockColorID(201, 0)] = 0x979797;
			this.blockColors[this.blockColorID(202, 0)] = 0x00FCFF;
			
			this.blockColors[this.blockColorID(203, 0)] = 0x8AAECC; 	// holy Dirt
			this.blockColors[this.blockColorID(204, 0)] = 0x55A961; 	// BOP plants
			this.blockColors[this.blockColorID(205, 0)] = 0x507852;		// Overgrown netherrack
			this.blockColors[this.blockColorID(206, 0)] = 0xA68D3F;		// BOP sapling
			this.blockColors[this.blockColorID(207, 0)] = 5775896;		// Nether diamond ore
			this.blockColors[this.blockColorID(208, 0)] = 5775896;		// Nether gold ore
			
			// Desert
			
			this.blockColors[this.blockColorID(209, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(210, 0)] = 0xC8B6A5;
			this.blockColors[this.blockColorID(211, 0)] = 0xE7DED0;
			this.blockColors[this.blockColorID(212, 0)] = 0xE7DED0;
			this.blockColors[this.blockColorID(213, 0)] = 0xEDDBCA;
			this.blockColors[this.blockColorID(213, 1)] = 0xEDDBCA;
			this.blockColors[this.blockColorID(213, 2)] = 0xEDDBCA;
			this.blockColors[this.blockColorID(214, 0)] = 0xCAB692;
			
			this.blockColors[this.blockColorID(215, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(216, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(217, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(218, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(219, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(220, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(221, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(222, 0)] = 0xDED3BD;
			this.blockColors[this.blockColorID(223, 0)] = 0xDED3BD;
			
			this.blockColors[this.blockColorID(224, 0)] = 0xC8B6A5;
			this.blockColors[this.blockColorID(225, 0)] = 0x78E483;
			
			/*
			int id;
			
			if(this.settingsFile.exists()) {
				bufferedReader9 = new BufferedReader(new FileReader(this.settingsFile));

				while((string10 = bufferedReader9.readLine()) != null) {
					meta = string10.split(":");

					try {
						if(meta[0].equals("Block") && meta.length == 4) {
							id = Integer.parseInt(meta[1]);
							int meta1 = Integer.parseInt(meta[2]);
							this.blockColors[this.blockColorID(id, meta1)] = Integer.parseInt(meta[3], 16);
						}
					} catch (NumberFormatException numberFormatException6) {
						numberFormatException6.printStackTrace();
					}
				}

				bufferedReader9.close();
			}

			printWriter11 = new PrintWriter(new FileWriter(this.settingsFile));

			for(i = 0; i < this.blockColors.length; ++i) {
				if(this.blockColors[i] != 16711935) {
					int i12 = i >> 8;
					id = i & 255;
					printWriter11.println("Block:" + id + ":" + i12 + ":" + Integer.toHexString(this.blockColors[i]));					
				}
			}
			*/
		} catch (Exception exception7) {
			exception7.printStackTrace();
		} finally {
			if(printWriter11 != null) printWriter11.close();
		}

	}

	private final int blockColorID(int blockid, int meta) {
		return blockid | meta << 8;
	}

	private final int getBlockColor(int blockid, int meta) { 
		try {
			int e = this.blockColors[this.blockColorID(blockid, meta)];
	
			if(e != 0xFF01FF) {
				return e;
			} else {
				e = this.blockColors[this.blockColorID(blockid, 0)];
				if(e != 0xFF01FF) {
					return e;
				} else {
					return this.blockColors[0];
				}
			}
		} catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException4) {
			throw arrayIndexOutOfBoundsException4;
		}
	}

	private void saveAll() {
		this.settingsFile = new File(this.mcDataDir, "zan.settings");
		// Changed: zoom / menu keys saved / edited / loaded in game settings.
		try {
			PrintWriter local = new PrintWriter(new FileWriter(this.settingsFile));
			local.println("Show Minimap:" + Boolean.toString(this.squareMap));
			local.println("Old North:" + Boolean.toString(this.oldNorth));
			local.println("Show Map in Nether:" + Boolean.toString(this.showNether));
			local.println("Enable Cave Mode:" + Boolean.toString(this.showCaves));
			local.println("Show Coordinates:" + Boolean.toString(this.coords));
			local.println("Dynamic Lighting:" + Boolean.toString(this.lightmap));
			local.println("Terrain Depth:" + Boolean.toString(this.heightmap));
			local.println("Welcome Message:" + Boolean.toString(this.welcome));
			/*
			local.println("Zoom Key:" + Keyboard.getKeyName(this.zoomKey));
			local.println("Menu Key:" + Keyboard.getKeyName(this.menuKey));
			*/
			local.println("Threading:" + Boolean.toString(threading));
			local.println("Hide:" + Boolean.toString(this.hide));
			local.close();
		} catch (Exception exception2) {
			this.chatInfo("\u00a7EError Saving Settings");
		}

	}

	private void saveWaypoints() {
		this.settingsFile = new File(this.mcDataDir, this.world + ".points");

		try {
			PrintWriter local = new PrintWriter(new FileWriter(this.settingsFile));
			Iterator<Waypoint> i$ = this.wayPts.iterator();

			while(i$.hasNext()) {
				Waypoint pt = (Waypoint)i$.next();
				if(!pt.name.startsWith("^")) {
					local.println(pt.name + ":" + pt.x + ":" + pt.z + ":" + Boolean.toString(pt.enabled) + ":" + pt.dimension + ":" + pt.red + ":" + pt.green + ":" + pt.blue);
				}
			}

			local.close();
		} catch (Exception exception4) {
			this.chatInfo("\u00a7EError Saving Waypoints");
		}

	}

	private void loadWaypoints() {
		String mapName = this.getMapName();
		String j;
		if(mapName.equals("MpServer")) {
			String[] local = this.getServerName().toLowerCase().split(":");
			j = local[0];
		} else {
			j = mapName;
		}

		if(!this.world.equals(j)) {
			this.world = j;
			this.iMenu = 1;
			this.wayPts = new ArrayList<Waypoint>();
			this.settingsFile = new File(this.mcDataDir, this.world + ".points");

			try {
				if(this.settingsFile.exists()) {
					BufferedReader local1 = new BufferedReader(new FileReader(this.settingsFile));

					String sCurrentLine;
					while((sCurrentLine = local1.readLine()) != null) {
						String[] curLine = sCurrentLine.split(":");
						if(curLine.length == 5) {
							this.wayPts.add(new Waypoint(curLine[0], Integer.parseInt(curLine[1]), Integer.parseInt(curLine[2]), Boolean.parseBoolean(curLine[3]), Integer.parseInt(curLine[4])));
						} else {
							this.wayPts.add(new Waypoint(curLine[0], Integer.parseInt(curLine[1]), Integer.parseInt(curLine[2]), Boolean.parseBoolean(curLine[3]), Integer.parseInt(curLine[4]), Float.parseFloat(curLine[5]), Float.parseFloat(curLine[6]), Float.parseFloat(curLine[7])));
						}
					}

					local1.close();
					this.chatInfo("\u00a7EWaypoints loaded for " + this.world);
				} else {
					//this.chatInfo("\u00a7EPress " + Keyboard.getKeyName(this.menuKey) + " configure Minimap!");
				}
			} catch (Exception exception6) {
				this.chatInfo("\u00a7EError Loading Waypoints");
			}
		}

	}

	private void renderMap(int scWidth) {
		if(!this.hide && !this.full) {
			if(this.q != 0) {
				this.glah(this.q);
			}

			int wayX1;
			int wayY1;
			Iterator<Waypoint> i$;
			Waypoint pt;
			float locate;
			double hypot;
			if(this.squareMap) {
				if(this.zoom == 3) {
					GL11.glPushMatrix();
					GL11.glScalef(0.5F, 0.5F, 1.0F);
					this.q = this.tex(this.map[this.zoom]);
					GL11.glPopMatrix();
				} else {
					this.q = this.tex(this.map[this.zoom]);
				}

				GL11.glPushMatrix();
				GL11.glTranslatef((float)scWidth - 32.0F, 37.0F, 0.0F);
				GL11.glRotatef(90.0F - (float)this.northRotate, 0.0F, 0.0F, 1.0F);
				GL11.glTranslatef(-((float)scWidth - 32.0F), -37.0F, 0.0F);
				this.drawPre();
				this.setMap(scWidth);
				this.drawPost();
				GL11.glPopMatrix();

				try {
					this.disp(this.img("/minimap/minimap.png"));
					this.drawPre();
					this.setMap(scWidth);
					this.drawPost();
				} catch (Exception exception79) {
					this.error = "error: minimap overlay not found!";
				}

				try {
					GL11.glPushMatrix();
					this.disp(this.img("/minimap/mmarrow.png"));
					GL11.glTranslatef((float)scWidth - 32.0F, 37.0F, 0.0F);
					GL11.glRotatef(-this.direction - 90.0F - (float)this.northRotate, 0.0F, 0.0F, 1.0F);
					GL11.glTranslatef(-((float)scWidth - 32.0F), -37.0F, 0.0F);
					this.drawPre();
					this.setMap(scWidth);
					this.drawPost();
				} catch (Exception exception77) {
					this.error = "Error: minimap arrow not found!";
				} finally {
					GL11.glPopMatrix();
				}

				i$ = this.wayPts.iterator();

				while(true) {
					while(true) {
						do {
							if(!i$.hasNext()) {
								return;
							}

							pt = (Waypoint)i$.next();
						} while(!pt.enabled || pt.dimension != this.game.thePlayer.dimension);

						if(this.game.thePlayer.dimension != -1) {
							wayX1 = this.xCoord() - pt.x;
							wayY1 = this.zCoord() - pt.z;
						} else {
							wayX1 = this.xCoord() - pt.x / 8;
							wayY1 = this.zCoord() - pt.z / 8;
						}

						if((double)Math.abs(wayX1) / (Math.pow(2.0D, (double)this.zoom) / 2.0D) <= 31.0D && (double)Math.abs(wayY1) / (Math.pow(2.0D, (double)this.zoom) / 2.0D) <= 31.0D) {
							locate = (float)Math.toDegrees(Math.atan2((double)wayX1, (double)wayY1));
							hypot = Math.sqrt((double)(wayX1 * wayX1 + wayY1 * wayY1)) / (Math.pow(2.0D, (double)this.zoom) / 2.0D);

							try {
								GL11.glPushMatrix();
								GL11.glColor3f(pt.red, pt.green, pt.blue);
								this.disp(this.img("/minimap/waypoint.png"));
								GL11.glTranslatef((float)scWidth - 32.0F, 37.0F, 0.0F);
								GL11.glRotatef(-locate + 90.0F - (float)this.northRotate, 0.0F, 0.0F, 1.0F);
								GL11.glTranslated(0.0D, -hypot, 0.0D);
								GL11.glRotatef(-(-locate + 90.0F - (float)this.northRotate), 0.0F, 0.0F, 1.0F);
								GL11.glTranslated(0.0D, hypot, 0.0D);
								GL11.glTranslatef(-((float)scWidth - 32.0F), -37.0F, 0.0F);
								GL11.glTranslated(0.0D, -hypot, 0.0D);
								this.drawPre();
								this.setMap(scWidth);
								this.drawPost();
							} catch (Exception exception73) {
								this.error = "Error: waypoint overlay not found!";
							} finally {
								GL11.glPopMatrix();
							}
						} else {
							locate = (float)Math.toDegrees(Math.atan2((double)wayX1, (double)wayY1));
							hypot = Math.sqrt((double)(wayX1 * wayX1 + wayY1 * wayY1));
							hypot = hypot / (double)Math.max(Math.abs(wayX1), Math.abs(wayY1)) * 34.0D;

							try {
								GL11.glPushMatrix();
								GL11.glColor3f(pt.red, pt.green, pt.blue);
								this.disp(this.img("/minimap/marker.png"));
								GL11.glTranslatef((float)scWidth - 32.0F, 37.0F, 0.0F);
								GL11.glRotatef(-locate + 90.0F - (float)this.northRotate, 0.0F, 0.0F, 1.0F);
								GL11.glTranslatef(-((float)scWidth - 32.0F), -37.0F, 0.0F);
								GL11.glTranslated(0.0D, -hypot, 0.0D);
								this.drawPre();
								this.setMap(scWidth);
								this.drawPost();
							} catch (Exception exception75) {
								this.error = "Error: marker overlay not found!";
							} finally {
								GL11.glPopMatrix();
							}
						}
					}
				}
			} else {
				GL11.glPushMatrix();
				if(this.zoom == 3) {
					GL11.glPushMatrix();
					GL11.glScalef(0.5F, 0.5F, 1.0F);
					this.q = this.tex(this.map[this.zoom]);
					GL11.glPopMatrix();
				} else {
					this.q = this.tex(this.map[this.zoom]);
				}

				GL11.glTranslatef((float)scWidth - 32.0F, 37.0F, 0.0F);
				GL11.glRotatef(this.direction + 180.0F, 0.0F, 0.0F, 1.0F);
				GL11.glTranslatef(-((float)scWidth - 32.0F), -37.0F, 0.0F);
				if(this.zoom == 0) {
					GL11.glTranslatef(-1.1F, -0.8F, 0.0F);
				} else {
					GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
				}

				this.drawPre();
				this.setMap(scWidth);
				this.drawPost();
				GL11.glPopMatrix();
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glColor3f(1.0F, 1.0F, 1.0F);
				this.drawRound(scWidth);
				this.drawDirections(scWidth);
				i$ = this.wayPts.iterator();

				while(i$.hasNext()) {
					pt = (Waypoint)i$.next();
					if(pt.enabled && pt.dimension == this.game.thePlayer.dimension) {
						if(this.game.thePlayer.dimension != -1) {
							wayX1 = this.xCoord() - pt.x;
							wayY1 = this.zCoord() - pt.z;
						} else {
							wayX1 = this.xCoord() - pt.x / 8;
							wayY1 = this.zCoord() - pt.z / 8;
						}

						locate = (float)Math.toDegrees(Math.atan2((double)wayX1, (double)wayY1));
						hypot = Math.sqrt((double)(wayX1 * wayX1 + wayY1 * wayY1)) / (Math.pow(2.0D, (double)this.zoom) / 2.0D);
						if(hypot >= 31.0D) {
							try {
								GL11.glPushMatrix();
								GL11.glColor3f(pt.red, pt.green, pt.blue);
								this.disp(this.img("/minimap/marker.png"));
								GL11.glTranslatef((float)scWidth - 32.0F, 37.0F, 0.0F);
								GL11.glRotatef(-locate + this.direction + 180.0F, 0.0F, 0.0F, 1.0F);
								GL11.glTranslatef(-((float)scWidth - 32.0F), -37.0F, 0.0F);
								GL11.glTranslated(0.0D, -34.0D, 0.0D);
								this.drawPre();
								this.setMap(scWidth);
								this.drawPost();
							} catch (Exception exception71) {
								this.error = "Error: marker overlay not found!";
							} finally {
								GL11.glPopMatrix();
							}
						} else {
							try {
								GL11.glPushMatrix();
								GL11.glColor3f(pt.red, pt.green, pt.blue);
								this.disp(this.img("/minimap/waypoint.png"));
								GL11.glTranslatef((float)scWidth - 32.0F, 37.0F, 0.0F);
								GL11.glRotatef(-locate + this.direction + 180.0F, 0.0F, 0.0F, 1.0F);
								GL11.glTranslated(0.0D, -hypot, 0.0D);
								GL11.glRotatef(-(-locate + this.direction + 180.0F), 0.0F, 0.0F, 1.0F);
								GL11.glTranslated(0.0D, hypot, 0.0D);
								GL11.glTranslatef(-((float)scWidth - 32.0F), -37.0F, 0.0F);
								GL11.glTranslated(0.0D, -hypot, 0.0D);
								this.drawPre();
								this.setMap(scWidth);
								this.drawPost();
							} catch (Exception exception69) {
								this.error = "Error: waypoint overlay not found!";
							} finally {
								GL11.glPopMatrix();
							}
						}
					}
				}
			}
		}

	}

	private void renderMapFull(int scWidth, int scHeight) {
		if(this.game.thePlayer.username.equals("lzztopz")) {
			this.error = "no map for you, Doubting Thomas";
		} else {
			this.q = this.tex(this.map[this.zoom]);
			GL11.glPushMatrix();
			GL11.glTranslatef((float)(scWidth + 5) / 2.0F, (float)(scHeight + 5) / 2.0F, 0.0F);
			GL11.glRotatef(90.0F - (float)this.northRotate, 0.0F, 0.0F, 1.0F);
			GL11.glTranslatef(-((float)(scWidth + 5) / 2.0F), -((float)(scHeight + 5) / 2.0F), 0.0F);
			this.drawPre();
			this.ldrawone((scWidth + 5) / 2 - 128, (scHeight + 5) / 2 + 128, 1.0D, 0.0D, 1.0D);
			this.ldrawone((scWidth + 5) / 2 + 128, (scHeight + 5) / 2 + 128, 1.0D, 1.0D, 1.0D);
			this.ldrawone((scWidth + 5) / 2 + 128, (scHeight + 5) / 2 - 128, 1.0D, 1.0D, 0.0D);
			this.ldrawone((scWidth + 5) / 2 - 128, (scHeight + 5) / 2 - 128, 1.0D, 0.0D, 0.0D);
			this.drawPost();
			GL11.glPopMatrix();

			try {
				GL11.glPushMatrix();
				this.disp(this.img("/minimap/mmarrow.png"));
				GL11.glTranslatef((float)((scWidth + 5) / 2), (float)((scHeight + 5) / 2), 0.0F);
				GL11.glRotatef(-this.direction - 90.0F - (float)this.northRotate, 0.0F, 0.0F, 1.0F);
				GL11.glTranslatef((float)(-((scWidth + 5) / 2)), (float)(-((scHeight + 5) / 2)), 0.0F);
				this.drawPre();
				this.ldrawone((scWidth + 5) / 2 - 32, (scHeight + 5) / 2 + 32, 1.0D, 0.0D, 1.0D);
				this.ldrawone((scWidth + 5) / 2 + 32, (scHeight + 5) / 2 + 32, 1.0D, 1.0D, 1.0D);
				this.ldrawone((scWidth + 5) / 2 + 32, (scHeight + 5) / 2 - 32, 1.0D, 1.0D, 0.0D);
				this.ldrawone((scWidth + 5) / 2 - 32, (scHeight + 5) / 2 - 32, 1.0D, 0.0D, 0.0D);
				this.drawPost();
			} catch (Exception exception7) {
				this.error = "Error: minimap arrow not found!";
			} finally {
				GL11.glPopMatrix();
			}

		}
	}

	private void showMenu(int scWidth, int scHeight) {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		int maxSize = 0;
		byte border = 2;
		boolean set = false;
		boolean click = false;
		int MouseX = this.getMouseX(scWidth);
		int MouseY = this.getMouseY(scHeight);
		if(Mouse.getEventButtonState() && Mouse.getEventButton() == 0) {
			if(!this.lfclick) {
				set = true;
				this.lfclick = true;
			} else {
				click = true;
			}
		} else if(this.lfclick) {
			this.lfclick = false;
		}

		String head = "Waypoints";
		String opt1 = "Exit Menu";
		String opt2 = "Waypoints";
		String opt3 = "Remove";
		int height;
		int title;
		if(this.iMenu < 3) {
			head = this.sMenu[this.iMenu - 1][0];

			for(height = 1; !this.sMenu[this.iMenu - 1][height].equals(""); ++height) {
				if(this.chkLen(this.sMenu[this.iMenu - 1][height]) > maxSize) {
					maxSize = this.chkLen(this.sMenu[this.iMenu - 1][height]);
				}
			}
		} else {
			opt1 = "Back";
			if(this.iMenu == 4) {
				opt2 = "Cancel";
			} else {
				opt2 = "Add";
			}

			maxSize = 80;

			for(title = 0; title < this.wayPts.size(); ++title) {
				if(this.chkLen(title + 1 + ") " + ((Waypoint)this.wayPts.get(title)).name) > maxSize) {
					maxSize = this.chkLen(title + 1 + ") " + ((Waypoint)this.wayPts.get(title)).name) + 32;
				}
			}

			height = 10;
		}

		title = this.chkLen(head);
		int centerX = (int)((double)(scWidth + 5) / 2.0D);
		int centerY = (int)((double)(scHeight + 5) / 2.0D);
		String hide = "\u00a77Press \u00a7F" + Keyboard.getKeyName(this.zoomKey) + "\u00a77 to hide.";
		int footer = this.chkLen(hide);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
		double leftX = (double)centerX - (double)title / 2.0D - (double)border;
		double rightX = (double)centerX + (double)title / 2.0D + (double)border;
		double topY = (double)centerY - (double)(height - 1) / 2.0D * 10.0D - (double)border - 20.0D;
		double botY = (double)centerY - (double)(height - 1) / 2.0D * 10.0D + (double)border - 10.0D;
		this.drawBox(leftX, rightX, topY, botY);
		if(this.iMenu == 1) {
			leftX = (double)centerX - (double)maxSize / 2.0D - (double)border;
			rightX = (double)centerX + (double)maxSize / 2.0D + (double)border;
			topY = (double)centerY - (double)(height - 1) / 2.0D * 10.0D - (double)border;
			botY = (double)centerY + (double)(height - 1) / 2.0D * 10.0D + (double)border;
			this.drawBox(leftX, rightX, topY, botY);
			leftX = (double)centerX - (double)footer / 2.0D - (double)border;
			rightX = (double)centerX + (double)footer / 2.0D + (double)border;
			topY = (double)centerY + (double)(height - 1) / 2.0D * 10.0D - (double)border + 10.0D;
			botY = (double)centerY + (double)(height - 1) / 2.0D * 10.0D + (double)border + 20.0D;
			this.drawBox(leftX, rightX, topY, botY);
		} else {
			leftX = (double)centerX - (double)maxSize / 2.0D - 25.0D - (double)border;
			rightX = (double)centerX + (double)maxSize / 2.0D + 25.0D + (double)border;
			topY = (double)centerY - (double)(height - 1) / 2.0D * 10.0D - (double)border;
			botY = (double)centerY + (double)(height - 1) / 2.0D * 10.0D + (double)border;
			this.drawBox(leftX, rightX, topY, botY);
			this.drawOptions(rightX - (double)border, topY + (double)border, MouseX, MouseY, set, click);
			footer = this.drawFooter(centerX, centerY, height, opt1, opt2, opt3, border, MouseX, MouseY, set, click);
		}

		GL11.glEnable(GL11.GL_TEXTURE_2D);
		this.write(head, centerX - title / 2, centerY - (height - 1) * 10 / 2 - 19, 0xFFFFFF);
		int verify;
		if(this.iMenu == 1) {
			for(verify = 1; verify < height; ++verify) {
				this.write(this.sMenu[this.iMenu - 1][verify], centerX - maxSize / 2, centerY - (height - 1) * 10 / 2 + verify * 10 - 9, 0xFFFFFF);
			}

			this.write(hide, centerX - footer / 2, (scHeight + 5) / 2 + (height - 1) * 10 / 2 + 11, 0xFFFFFF);
		} else {
			if(this.iMenu == 2) {
				for(verify = 1; verify < height; ++verify) {
					this.write(this.sMenu[this.iMenu - 1][verify], (int)leftX + border + 1, centerY - (height - 1) * 10 / 2 + verify * 10 - 9, 0xFFFFFF);
					if(this.chkOptions(verify - 1)) {
						hide = "On";
					} else {
						hide = "Off";
					}

					this.write(hide, (int)rightX - border - 15 - this.chkLen(hide) / 2, centerY - (height - 1) * 10 / 2 + verify * 10 - 8, 0xFFFFFF);
				}
			} else {
				verify = this.min + 9;
				if(verify > this.wayPts.size()) {
					verify = this.wayPts.size();
					if(this.min >= 0) {
						if(verify - 9 > 0) {
							this.min = verify - 9;
						} else {
							this.min = 0;
						}
					}
				}

				for(int out = this.min; out < verify; ++out) {
					int localException = centerY - (height - 1) * 10 / 2 + (out + 1 - this.min) * 10;
					int leftTxt = (int)leftX + border + 1;
					this.write(out + 1 + ") " + ((Waypoint)this.wayPts.get(out)).name, leftTxt, localException - 9, 0xFFFFFF);
					if(this.iMenu == 4) {
						hide = "X";
					} else if(((Waypoint)this.wayPts.get(out)).enabled) {
						hide = "On";
					} else {
						hide = "Off";
					}

					this.write(hide, (int)rightX - border - 29 - this.chkLen(hide) / 2, localException - 8, 0xFFFFFF);
					if(MouseX > leftTxt && (double)MouseX < rightX - (double)border - 77.0D && MouseY > localException - 10 && MouseY < localException - 1) {
						Waypoint waypoint = ((Waypoint)this.wayPts.get(out));
						String out1 = waypoint.x + ", " + waypoint.z + " [" + waypoint.dimension + "]";
						int len = this.chkLen(out1) / 2;
						GL11.glDisable(GL11.GL_TEXTURE_2D);
						GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.8F);
						this.drawBox((double)(MouseX - len - 1), (double)(MouseX + len + 1), (double)(MouseY - 11), (double)(MouseY - 1));
						GL11.glEnable(GL11.GL_TEXTURE_2D);
						this.write(out1, MouseX - len, MouseY - 10, 0xFFFFFF);
					}
				}
			}

			verify = (scHeight + 5) / 2 + (height - 1) * 10 / 2 + 11;
			if(this.iMenu == 2) {
				this.write(opt1, centerX - 5 - border - footer - this.chkLen(opt1) / 2, verify, 0xFFFFFF);
				this.write(opt2, centerX + border + 5 + footer - this.chkLen(opt2) / 2, verify, 0xFFFFFF);
			} else {
				if(this.iMenu != 4) {
					this.write(opt1, centerX - 5 - border * 2 - footer * 2 - this.chkLen(opt1) / 2, verify, 0xFFFFFF);
				}

				this.write(opt2, centerX - this.chkLen(opt2) / 2, verify, 0xFFFFFF);
				if(this.iMenu != 4) {
					this.write(opt3, centerX + 5 + border * 2 + footer * 2 - this.chkLen(opt3) / 2, verify, 0xFFFFFF);
				}
			}
		}

		if(this.iMenu > 4) {
			String string38 = " !\"#$%&\'()*+,-./0123456789;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_\'abcdefghijklmnopqrstuvwxyz{|}~\u00e2\u0152\u201a\u00c3\u2021\u00c3\u00bc\u00c3\u00a9\u00c3\u00a2\u00c3\u00a4\u00c3\u00a0\u00c3\u00a5\u00c3\u00a7\u00c3\u00aa\u00c3\u00ab\u00c3\u00a8\u00c3\u00af\u00c3\u00ae\u00c3\u00ac\u00c3\u201e\u00c3\u2026\u00c3\u2030\u00c3\u00a6\u00c3\u2020\u00c3\u00b4\u00c3\u00b6\u00c3\u00b2\u00c3\u00bb\u00c3\u00b9\u00c3\u00bf\u00c3\u2013\u00c3\u0153\u00c3\u00b8\u00c2\u00a3\u00c3\u02dc\u00c3\u2014\u00c6\u2019\u00c3\u00a1\u00c3\u00ad\u00c3\u00b3\u00c3\u00ba\u00c3\u00b1\u00c3\u2018\u00c2\u00aa\u00c2\u00ba\u00c2\u00bf\u00c2\u00ae\u00c2\u00ac\u00c2\u00bd\u00c2\u00bc\u00c2\u00a1\u00c2\u00ab\u00c2\u00bb";
			if(this.iMenu > 5 && this.inStr.equals("")) {
				string38 = "-0123456789";
			} else if(this.iMenu > 5) {
				string38 = "0123456789";
			}

			if(!Keyboard.getEventKeyState()) {
				this.lastKey = 0;
			} else {
				do {
					if(Keyboard.getEventKey() == Keyboard.KEY_RETURN && this.lastKey != Keyboard.KEY_RETURN) {
						if(this.inStr.equals("")) {
							this.next = 3;
						} else if(this.iMenu == 5) {
							this.next = 6;
							this.way = this.inStr;
							if(this.game.thePlayer.dimension != -1) {
								this.inStr = Integer.toString(this.xCoord());
							} else {
								this.inStr = Integer.toString(this.xCoord() * 8);
							}
						} else if(this.iMenu == 6) {
							this.next = 7;

							try {
								this.wayX = Integer.parseInt(this.inStr);
							} catch (Exception exception36) {
								this.next = 3;
							}

							if(this.game.thePlayer.dimension != -1) {
								this.inStr = Integer.toString(this.zCoord());
							} else {
								this.inStr = Integer.toString(this.zCoord() * 8);
							}
						} else {
							this.next = 3;

							try {
								this.wayZ = Integer.parseInt(this.inStr);
							} catch (Exception exception35) {
								this.inStr = "";
							}

							if(!this.inStr.equals("")) {
								this.wayPts.add(new Waypoint(this.way, this.wayX, this.wayZ, true, this.game.thePlayer.dimension));
								this.saveWaypoints();
								if(this.wayPts.size() > 9) {
									this.min = this.wayPts.size() - 9;
								}
							}
						}
					} else if(Keyboard.getEventKey() == Keyboard.KEY_BACK && this.lastKey != Keyboard.KEY_BACK && this.inStr.length() > 0) {
						this.inStr = this.inStr.substring(0, this.inStr.length() - 1);
					}

					if(string38.indexOf(Keyboard.getEventCharacter()) >= 0 && Keyboard.getEventKey() != this.lastKey && this.chkLen(this.inStr + Keyboard.getEventCharacter()) < 148) {
						this.inStr = this.inStr + Keyboard.getEventCharacter();
					}

					this.lastKey = Keyboard.getEventKey();
				} while(Keyboard.next());
			}

			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
			leftX = (double)(centerX - 75 - border);
			rightX = (double)(centerX + 75 + border);
			topY = (double)(centerY - 10 - border);
			botY = (double)(centerY + 10 + border);
			this.drawBox(leftX, rightX, topY, botY);
			leftX += (double)border;
			rightX -= (double)border;
			topY += 11.0D;
			botY -= (double)border;
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			this.drawBox(leftX, rightX, topY, botY);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			String string37 = "Please enter a name:";
			if(this.iMenu == 6) {
				string37 = "Enter X coordinate:";
			} else if(this.iMenu == 7) {
				string37 = "Enter Z coordinate:";
			}

			this.write(string37, (int)leftX + border, (int)topY - 11 + border, 0xFFFFFF);
			if(this.blink > 60) {
				this.blink = 0;
			}

			if(this.blink < 30) {
				this.write(this.inStr + "|", (int)leftX + border, (int)topY + border, 0xFFFFFF);
			} else {
				this.write(this.inStr, (int)leftX + border, (int)topY + border, 0xFFFFFF);
			}

			if(this.iMenu == 6) {
				try {
					if(Integer.parseInt(this.inStr) == this.xCoord()) {
						this.write("(Current)", (int)leftX + border + this.chkLen(this.inStr) + 5, (int)topY + border, 10526880);
					}
				} catch (Exception exception34) {
				}
			} else if(this.iMenu == 7) {
				try {
					if(Integer.parseInt(this.inStr) == this.zCoord()) {
						this.write("(Current)", (int)leftX + border + this.chkLen(this.inStr) + 5, (int)topY + border, 10526880);
					}
				} catch (Exception exception33) {
				}
			}

			++this.blink;
		}

		if(this.next != 0) {
			this.iMenu = this.next;
			this.next = 0;
		}

	}

	private void showCoords(int scWidth, int scHeight) {
		if(!this.hide) {
			int x = this.xCoord();
			int z = this.zCoord();
			GL11.glPushMatrix();
			GL11.glScalef(0.5F, 0.5F, 1.0F);
			String xy = "";
			xy = this.dCoord(x) + ", " + this.yCoord() + ", " + this.dCoord(z);
			int m = this.chkLen(xy) / 2;
			this.write(xy, scWidth * 2 - 64 - m, 146, 0xFFFFFF);
			
			Chunk chunk48 = this.game.theWorld.getChunkFromBlockCoords(x, z);
			xy = chunk48.getBiomeForCoords(x & 15, z & 15, this.game.theWorld.getWorldChunkManager()).biomeName ;
			
			m = this.chkLen(xy) / 2;
			this.write(xy, scWidth * 2 - 64 - m, 156, 0xFFFFFF);
			GL11.glPopMatrix();
		} 

	}

	private void drawRound(int paramInt1) {
		try {
			this.disp(this.img("/minimap/roundmap.png"));
			this.drawPre();
			this.setMap(paramInt1);
			this.drawPost();
		} catch (Exception exception3) {
			this.error = "Error: minimap overlay not found!";
		}

	}

	private void drawBox(double leftX, double rightX, double topY, double botY) {
		this.drawPre();
		this.ldrawtwo(leftX, botY, 0.0D);
		this.ldrawtwo(rightX, botY, 0.0D);
		this.ldrawtwo(rightX, topY, 0.0D);
		this.ldrawtwo(leftX, topY, 0.0D);
		this.drawPost();
	}

	private void drawOptions(double rightX, double topY, int MouseX, int MouseY, boolean set, boolean click) {
		if(this.iMenu > 2) {
			if(this.min < 0) {
				this.min = 0;
			}

			if(!Mouse.isButtonDown(0) && this.scrClick) {
				this.scrClick = false;
			}

			if((double)MouseX > rightX - 10.0D && (double)MouseX < rightX - 2.0D && (double)MouseY > topY + 1.0D && (double)MouseY < topY + 10.0D) {
				if(!set && !click) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
				} else {
					if(set && this.min > 0) {
						--this.min;
					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				}
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
			}

			this.drawPre();
			this.ldrawtwo(rightX - 10.0D, topY + 10.0D, 0.0D);
			this.ldrawtwo(rightX - 2.0D, topY + 10.0D, 0.0D);
			this.ldrawtwo(rightX - 6.0D, topY + 1.0D, 0.0D);
			this.ldrawtwo(rightX - 6.0D, topY + 1.0D, 0.0D);
			this.drawPost();
			if(this.wayPts.size() > 9) {
				this.sMax = (int)(9.0D / (double)this.wayPts.size() * 67.0D);
			} else {
				this.sMin = 0;
				this.sMax = 67;
			}

			if(((double)MouseX <= rightX - 10.0D || (double)MouseX >= rightX - 2.0D || (double)MouseY <= topY + 12.0D + (double)this.sMin || (double)MouseY >= topY + 12.0D + (double)this.sMin + (double)this.sMax) && !this.scrClick) {
				if(this.wayPts.size() > 9) {
					this.sMin = (int)((double)this.min / (double)(this.wayPts.size() - 9) * (67.0D - (double)this.sMax));
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
			} else if(Mouse.isButtonDown(0) && !this.scrClick) {
				this.scrClick = true;
				this.scrStart = MouseY;
			} else if(this.scrClick && this.wayPts.size() > 9) {
				int leftX = MouseY - this.scrStart;
				if(this.sMin + leftX < 0) {
					this.sMin = 0;
				} else if(this.sMin + leftX + this.sMax > 67) {
					this.sMin = 67 - this.sMax;
				} else {
					this.sMin += leftX;
					this.scrStart = MouseY;
				}

				this.min = (int)((double)this.sMin / (67.0D - (double)this.sMax) * (double)(this.wayPts.size() - 9));
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
			}

			this.drawBox(rightX - 10.0D, rightX - 2.0D, topY + 12.0D + (double)this.sMin, topY + 12.0D + (double)this.sMin + (double)this.sMax);
			if((double)MouseX > rightX - 10.0D && (double)MouseX < rightX - 2.0D && (double)MouseY > topY + 81.0D && (double)MouseY < topY + 90.0D) {
				if(!set && !click) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
				} else {
					if(set && this.min < this.wayPts.size() - 9) {
						++this.min;
					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.7F);
				}
			} else {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.3F);
			}

			this.drawPre();
			this.ldrawtwo(rightX - 6.0D, topY + 90.0D, 0.0D);
			this.ldrawtwo(rightX - 6.0D, topY + 90.0D, 0.0D);
			this.ldrawtwo(rightX - 2.0D, topY + 81.0D, 0.0D);
			this.ldrawtwo(rightX - 10.0D, topY + 81.0D, 0.0D);
			this.drawPost();
		}

		double d19 = rightX - 30.0D;
		double botY = 0.0D;
		++topY;
		int max = this.min + 9;
		if(max > this.wayPts.size()) {
			max = this.wayPts.size();
			if(this.min > 0) {
				if(max - 9 > 0) {
					this.min = max - 9;
				} else {
					this.min = 0;
				}
			}
		}

		double leftCl = 0.0D;
		double rightCl = 0.0D;
		if(this.iMenu > 2) {
			d19 -= 14.0D;
			rightX -= 14.0D;
			rightCl = rightX - 32.0D;
			leftCl = rightCl - 9.0D;
		} else {
			this.min = 0;
			if(this.motionTrackerExists.booleanValue()) {
				max = 11;
			} else {
				max = 10;
			}
		}

		for(int i = this.min; i < max; ++i) {
			if(i > this.min) {
				topY += 10.0D;
			}

			botY = topY + 9.0D;
			if((double)MouseX > d19 && (double)MouseX < rightX && (double)MouseY > topY && (double)MouseY < botY && this.iMenu < 5) {
				if(!set && !click) {
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.6F);
				} else {
					if(set) {
						if(this.iMenu == 2) {
							this.setOptions(i);
						} else if(this.iMenu == 3) {
							((Waypoint)this.wayPts.get(i)).enabled = !((Waypoint)this.wayPts.get(i)).enabled;
							this.saveWaypoints();
						} else {
							this.delWay(i);
							this.next = 3;
						}
					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				}
			} else if(this.iMenu == 2) {
				if(this.chkOptions(i)) {
					GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.6F);
				} else {
					GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.6F);
				}
			} else if(this.iMenu == 4) {
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.4F);
			} else if(((Waypoint)this.wayPts.get(i)).enabled) {
				GL11.glColor4f(0.0F, 1.0F, 0.0F, 0.6F);
			} else {
				GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.6F);
			}

			this.drawBox(d19, rightX, topY, botY);
			if(this.iMenu > 2 && (this.iMenu != 4 || this.next != 3)) {
				if((double)MouseX > leftCl && (double)MouseX < rightCl && (double)MouseY > topY && (double)MouseY < botY && this.iMenu == 3 && set) {
					((Waypoint)this.wayPts.get(i)).red = this.generator.nextFloat();
					((Waypoint)this.wayPts.get(i)).green = this.generator.nextFloat();
					((Waypoint)this.wayPts.get(i)).blue = this.generator.nextFloat();
					this.saveWaypoints();
				}

				GL11.glColor3f(((Waypoint)this.wayPts.get(i)).red, ((Waypoint)this.wayPts.get(i)).green, ((Waypoint)this.wayPts.get(i)).blue);
				this.drawBox(leftCl, rightCl, topY, botY);
			}
		}

	}

	private void delWay(int i) {
		this.wayPts.remove(i);
		this.saveWaypoints();
	}

	private int drawFooter(int centerX, int centerY, int m, String opt1, String opt2, String opt3, int border, int MouseX, int MouseY, boolean set, boolean click) {
		int footer = this.chkLen(opt1);
		if(this.chkLen(opt2) > footer) {
			footer = this.chkLen(opt2);
		}

		double leftX = (double)(centerX - footer - border * 2 - 5);
		double rightX = (double)(centerX - 5);
		double topY = (double)centerY + (double)(m - 1) / 2.0D * 10.0D - (double)border + 10.0D;
		double botY = (double)centerY + (double)(m - 1) / 2.0D * 10.0D + (double)border + 20.0D;
		if(this.iMenu > 2) {
			if(this.chkLen(opt3) > footer) {
				footer = this.chkLen(opt3);
			}

			leftX = (double)(centerX - border * 3) - (double)footer * 1.5D - 5.0D;
			rightX = (double)(centerX - footer / 2 - border - 5);
		}

		if((double)MouseX > leftX && (double)MouseX < rightX && (double)MouseY > topY && (double)MouseY < botY && this.iMenu < 4) {
			if(!set && !click) {
				GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.7F);
			} else {
				if(set) {
					if(this.iMenu == 2) {
						this.setMenuNull();
					} else {
						this.next = 2;
					}
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		} else {
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
		}

		if(this.iMenu != 4) {
			this.drawBox(leftX, rightX, topY, botY);
		}

		if(this.iMenu == 2) {
			leftX = (double)(centerX + 5);
			rightX = (double)(centerX + footer + border * 2 + 5);
		} else {
			leftX = (double)(centerX - footer / 2 - border);
			rightX = (double)(centerX + footer / 2 + border);
		}

		if((double)MouseX > leftX && (double)MouseX < rightX && (double)MouseY > topY && (double)MouseY < botY && this.iMenu < 5) {
			if(!set && !click) {
				GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.7F);
			} else {
				if(set) {
					if(this.iMenu != 2 && this.iMenu != 4) {
						this.next = 5;
						this.inStr = "";
					} else {
						this.next = 3;
					}
				}

				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			}
		} else {
			GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
		}

		this.drawBox(leftX, rightX, topY, botY);
		if(this.iMenu > 2) {
			rightX = (double)(centerX + border * 3) + (double)footer * 1.5D + 5.0D;
			leftX = (double)(centerX + footer / 2 + border + 5);
			if((double)MouseX > leftX && (double)MouseX < rightX && (double)MouseY > topY && (double)MouseY < botY && this.iMenu < 4) {
				if(!set && !click) {
					GL11.glColor4f(0.5F, 0.5F, 0.5F, 0.7F);
				} else {
					if(set) {
						this.next = 4;
					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				}
			} else {
				GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.7F);
			}

			if(this.iMenu != 4) {
				this.drawBox(leftX, rightX, topY, botY);
			}
		}

		return footer / 2;
	}

	private boolean chkOptions(int i) {
		if(i == 0) {
			return this.coords;
		} else if(i == 1) {
			return this.hide;
		} else if(i == 2) {
			return this.showNether;
		} else if(i == 3) {
			return this.showCaves;
		} else if(i == 4) {
			return this.lightmap;
		} else if(i == 5) {
			return this.heightmap;
		} else if(i == 6) {
			return this.squareMap;
		} else if(i == 7) {
			return this.oldNorth;
		} else if(i == 8) {
			return this.welcome;
		} else if(i == 9) {
			return threading;
		} else if(i == 10 && this.motionTrackerExists.booleanValue()) {
			return false;
		} else {
			throw new IllegalArgumentException("bad option number " + i);
		}
	}

	private void setOptions(int i) {
		if(i == 0) {
			this.coords = !this.coords;
		} else if(i == 1) {
			this.hide = !this.hide;
		} else if(i == 2) {
			this.showNether = !this.showNether;
		} else if(i == 3) {
			this.showCaves = !this.showCaves;
		} else if(i == 4) {
			this.lightmap = !this.lightmap;
		} else if(i == 5) {
			this.heightmap = !this.heightmap;
		} else if(i == 6) {
			this.squareMap = !this.squareMap;
		} else if(i == 7) {
			this.oldNorth = !this.oldNorth;
		} else if(i == 8) {
			this.welcome = !this.welcome;
		} else if(i == 9) {
			threading = !threading;
		} else {
			if(i != 10 || !this.motionTrackerExists.booleanValue()) {
				throw new IllegalArgumentException("bad option number " + i);
			}

			//this.motionTracker.activated = true;
		}

		this.saveAll();
		this.timer = 500;
	}

	private void setMap(int paramInt1) {
		this.ldrawthree((double)paramInt1 - 64.0D, 69.0D, 1.0D, 0.0D, 1.0D);
		this.ldrawthree((double)paramInt1, 69.0D, 1.0D, 1.0D, 1.0D);
		this.ldrawthree((double)paramInt1, 5.0D, 1.0D, 1.0D, 0.0D);
		this.ldrawthree((double)paramInt1 - 64.0D, 5.0D, 1.0D, 0.0D, 0.0D);
	}

	private void drawDirections(int scWidth) {
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 1.0F);
		GL11.glTranslated(64.0D * Math.sin(Math.toRadians(-((double)this.direction - 90.0D + (double)this.northRotate))), 64.0D * Math.cos(Math.toRadians(-((double)this.direction - 90.0D + (double)this.northRotate))), 0.0D);
		this.write("N", scWidth * 2 - 66, 70, 0xFFFFFF);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 1.0F);
		GL11.glTranslated(64.0D * Math.sin(Math.toRadians((double)(-(this.direction + (float)this.northRotate)))), 64.0D * Math.cos(Math.toRadians((double)(-(this.direction + (float)this.northRotate)))), 0.0D);
		this.write("E", scWidth * 2 - 66, 70, 0xFFFFFF);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 1.0F);
		GL11.glTranslated(64.0D * Math.sin(Math.toRadians(-((double)this.direction + 90.0D + (double)this.northRotate))), 64.0D * Math.cos(Math.toRadians(-((double)this.direction + 90.0D + (double)this.northRotate))), 0.0D);
		this.write("S", scWidth * 2 - 66, 70, 0xFFFFFF);
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glScalef(0.5F, 0.5F, 1.0F);
		GL11.glTranslated(64.0D * Math.sin(Math.toRadians(-((double)this.direction + 180.0D + (double)this.northRotate))), 64.0D * Math.cos(Math.toRadians(-((double)this.direction + 180.0D + (double)this.northRotate))), 0.0D);
		this.write("W", scWidth * 2 - 66, 70, 0xFFFFFF);
		GL11.glPopMatrix();
	}

	private void SetZoom() {
		if(this.fudge <= 0) {
			if(this.iMenu != 0) {
				this.iMenu = 0;
				if(this.getMenu() != null) {
					this.setMenuNull();
				}
			} else {
				if(this.zoom == 3) {
					if(!this.full) {
						this.full = true;
					} else {
						this.zoom = 2;
						this.full = false;
						this.error = "Zoom Level: (1.0x)";
					}
				} else if(this.zoom == 0) {
					this.zoom = 3;
					this.error = "Zoom Level: (0.5x)";
				} else if(this.zoom == 2) {
					this.zoom = 1;
					this.error = "Zoom Level: (2.0x)";
				} else {
					this.zoom = 0;
					this.error = "Zoom Level: (4.0x)";
				}

				this.timer = 500;
			}

			this.fudge = 20;
		}
	}

	public String Version() {
		return "1.3_01 - " + this.zmodver;
	}
}
