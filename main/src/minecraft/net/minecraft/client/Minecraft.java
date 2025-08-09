package net.minecraft.client;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.io.File;
import java.net.URL;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

import com.misc.moreresources.MoreResourcesInstaller;
import com.mojontwins.minecraft.commands.CommandProcessor;
import com.mojontwins.minecraft.worldedit.WorldEdit;

import net.minecraft.client.gui.EnumOptions;
import net.minecraft.client.gui.GameHints;
import net.minecraft.client.gui.GameSettings;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiConflictWarning;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiMinimap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.LoadingScreenRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.minimap.ZanMinimap;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.multiplayer.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.player.EntityPlayerSP;
import net.minecraft.client.player.MovementInputFromOptions;
import net.minecraft.client.player.PlayerController;
import net.minecraft.client.renderer.EffectRenderer;
import net.minecraft.client.renderer.FontRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.GraphicsMode;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MouseHelper;
import net.minecraft.client.renderer.OpenGlCapsChecker;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderEngine;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.ptexture.TextureAnimatedFX;
import net.minecraft.client.renderer.ptexture.TextureCompassFX;
import net.minecraft.client.renderer.ptexture.TextureFlamesFX;
import net.minecraft.client.renderer.ptexture.TextureLavaFX;
import net.minecraft.client.renderer.ptexture.TextureLavaFlowFX;
import net.minecraft.client.renderer.ptexture.TexturePortalFX;
import net.minecraft.client.renderer.ptexture.TextureWatchFX;
import net.minecraft.client.renderer.ptexture.TextureWaterFX;
import net.minecraft.client.renderer.ptexture.TextureWaterFlowFX;
import net.minecraft.client.skins.TexturePackList;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.title.GuiMainMenu;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.src.MathHelper;
import net.minecraft.util.MinecraftException;
import net.minecraft.util.StringTranslate;
import net.minecraft.util.Translator;
import net.minecraft.world.GameRules;
import net.minecraft.world.entity.EntityLiving;
import net.minecraft.world.entity.player.EntityPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemBlock;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.level.chunk.storage.ISaveFormat;
import net.minecraft.world.level.chunk.storage.ISaveHandler;
import net.minecraft.world.level.colorizer.ColorizerFog;
import net.minecraft.world.level.colorizer.ColorizerFoliage;
import net.minecraft.world.level.colorizer.ColorizerGrass;
import net.minecraft.world.level.colorizer.ColorizerWater;
import net.minecraft.world.level.dimension.Teleporter;
import net.minecraft.world.level.dimension.WorldProvider;
import net.minecraft.world.level.tile.Block;
import net.minecraft.world.level.tile.BlockLeaves;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.EnumMovingObjectType;
import net.minecraft.world.phys.MovingObjectPosition;
import net.minecraft.world.phys.Vec3D;

public abstract class Minecraft implements Runnable {
	public static byte[] field_28006_b = new byte[10485760];
	private static Minecraft theMinecraft;
	public PlayerController playerController;
	private boolean fullscreen = false;
	private boolean hasCrashed = false;
	public int displayWidth;
	public int displayHeight;
	private Timer timer = new Timer(20.0F);
	public World theWorld;
	public LevelRenderer renderGlobal;
	public EntityPlayerSP thePlayer;
	public EntityLiving renderViewEntity;
	public EffectRenderer effectRenderer;
	public User session = null;
	public String minecraftUri;
	public Canvas mcCanvas;
	public boolean hideQuitButton = false;
	public volatile boolean isGamePaused = false;
	public RenderEngine renderEngine;
	public FontRenderer fontRenderer;
	public FontRenderer standardGalacticFontRenderer;
	public GuiScreen currentScreen = null;
	public LoadingScreenRenderer loadingScreen;
	public GameRenderer entityRenderer;
	private ThreadDownloadResources downloadResourcesThread;
	public int ticksRan = 0;
	private int leftClickCounter = 0;
	private int tempDisplayWidth;
	private int tempDisplayHeight;
	public GuiIngame ingameGUI;
	public boolean skipRenderWorld = false;
	public ModelBiped playerModelBiped = new ModelBiped(0.0F);
	public MovingObjectPosition objectMouseOver = null;
	public GameSettings gameSettings;
	protected MinecraftApplet mcApplet;
	public SoundManager sndManager = new SoundManager();
	public MouseHelper mouseHelper;
	public TexturePackList texturePackList;
	public File mcDataDir;
	private ISaveFormat saveLoader;
	public static long[] frameTimes = new long[512];
	public static long[] tickTimes = new long[512];
	public static int numRecordedFrameTimes = 0;
	public static long hasPaidCheckTime = 0L;
	private int rightClickDelayTimer = 0;
	private String serverName;
	private int serverPort;
	private TextureWaterFX textureWaterFX = new TextureWaterFX();
	private TextureLavaFX textureLavaFX = new TextureLavaFX();
	private static File minecraftDir = null;
	public volatile boolean running = true;
	public String debug = "";
	long debugUpdateTime = System.currentTimeMillis();
	int fpsCounter = 0;
	boolean isTakingScreenshot = false;
	long prevFrameTime = -1L;
	public boolean inGameHasFocus = false;
	public boolean isRaining = false;
	long systemTime = System.currentTimeMillis();
	private int joinPlayerCounter = 0;
	
	public ZanMinimap zanMinimap;

	public Minecraft(Component component1, Canvas canvas2, MinecraftApplet minecraftApplet3, int i4, int i5, boolean z6) {
		this.tempDisplayHeight = i5;
		this.fullscreen = z6;
		this.mcApplet = minecraftApplet3;
		Packet3Chat.maxStringLength = 32767;
		new ThreadClientSleep(this, "Timer hack thread");
		this.mcCanvas = canvas2;
		this.displayWidth = i4;
		this.displayHeight = i5;
		this.fullscreen = z6;
		if(minecraftApplet3 == null || "true".equals(minecraftApplet3.getParameter("stand-alone"))) {
			this.hideQuitButton = false;
		}

		theMinecraft = this;
	}

	public void onMinecraftCrash(UnexpectedThrowable unexpectedThrowable1) {
		this.hasCrashed = true;
		this.displayUnexpectedThrowable(unexpectedThrowable1);
	}

	public abstract void displayUnexpectedThrowable(UnexpectedThrowable unexpectedThrowable1);

	public void setServer(String string1, int i2) {
		this.serverName = string1;
		this.serverPort = i2;
	}

	public void startGame() throws LWJGLException {
		if(this.mcCanvas != null) {
			Graphics graphics1 = this.mcCanvas.getGraphics();
			if(graphics1 != null) { 
				graphics1.setColor(Color.BLACK);
				graphics1.fillRect(0, 0, this.displayWidth, this.displayHeight);
				graphics1.dispose();
			}

			Display.setParent(this.mcCanvas);
		} else if(this.fullscreen) {
			Display.setFullscreen(true);
			this.displayWidth = Display.getDisplayMode().getWidth();
			this.displayHeight = Display.getDisplayMode().getHeight();
			if(this.displayWidth <= 0) {
				this.displayWidth = 1;
			}

			if(this.displayHeight <= 0) {
				this.displayHeight = 1;
			}
		} else {
			Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
		}

		Display.setTitle("Minecraft Minecraft 1.2.5");
		System.out.println("LWJGL Version: " + Sys.getVersion());

		try {
			PixelFormat pixelFormat7 = new PixelFormat();
			pixelFormat7 = pixelFormat7.withDepthBits(24);
			Display.create(pixelFormat7);
		} catch (LWJGLException lWJGLException6) {
			lWJGLException6.printStackTrace();

			try {
				Thread.sleep(1000L);
			} catch (InterruptedException interruptedException5) {
			}

			Display.create();
		}

		OpenGlHelper.initializeTextures();
		this.mcDataDir = getMinecraftDir();
		this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
		this.gameSettings = new GameSettings(this, this.mcDataDir);
		GameRules.withMcDataDir(this.mcDataDir);
		this.zanMinimap = new ZanMinimap(this, this.mcDataDir);
		this.texturePackList = new TexturePackList(this, this.mcDataDir);
		this.renderEngine = new RenderEngine(this.texturePackList);
		this.loadScreen();
		this.fontRenderer = new FontRenderer(this.gameSettings, "/font/default.png", this.renderEngine, false);
		this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, "/font/alternate.png", this.renderEngine, false);
		if(GameSettingsValues.language != null) {
			StringTranslate.getInstance().setLanguage(GameSettingsValues.language);
			this.fontRenderer.setUnicodeFlag(StringTranslate.getInstance().isUnicode());
			this.fontRenderer.setBidiFlag(StringTranslate.isBidrectional(GameSettingsValues.language));
		}

		ColorizerWater.setWaterBiomeColorizer(this.renderEngine.getTextureContents("/misc/watercolor.png"));
		ColorizerGrass.setGrassBiomeColorizer(this.renderEngine.getTextureContents("/misc/grasscolor.png"));
		ColorizerFoliage.setFoliageBiomeColorizer(this.renderEngine.getTextureContents("/misc/foliagecolor.png"));
		ColorizerFog.setFogBiomeColorizer(this.renderEngine.getTextureContents("/misc/fogcolor.png"));

		// Init commands
		CommandProcessor.registerCommands();
		WorldEdit.registerCommands();
		
		this.entityRenderer = new GameRenderer(this);
		RenderManager.instance.itemRenderer = new ItemRenderer(this);
		
		this.loadScreen();
		Mouse.create();
		this.mouseHelper = new MouseHelper(this.mcCanvas);

		try {
			Controllers.create();
		} catch (Exception exception4) {
			exception4.printStackTrace();
		}

		this.checkGLError("Pre startup");
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glClearDepth(1.0D);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDepthFunc(GL11.GL_LEQUAL);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		this.checkGLError("Startup");
		new OpenGlCapsChecker();
		this.sndManager.loadSoundSettings(this.gameSettings);
		this.renderEngine.registerTextureFX(this.textureLavaFX);
		this.renderEngine.registerTextureFX(this.textureWaterFX);
		this.renderEngine.registerTextureFX(new TextureCompassFX(this));
		this.renderEngine.registerTextureFX(new TextureWatchFX(this));
		this.renderEngine.registerTextureFX(new TextureWaterFlowFX());
		this.renderEngine.registerTextureFX(new TextureLavaFlowFX());
		this.renderEngine.registerTextureFX(new TextureFlamesFX(0));
		this.renderEngine.registerTextureFX(new TextureFlamesFX(1));
		this.renderEngine.registerTextureFX(new TexturePortalFX());
		
		// Custom texture atlas based animated textures
		this.renderEngine.registerTextureFX(new TextureAnimatedFX(12*16+11, 0, "/animated/block_seaweed.png", 1));
		
		this.renderGlobal = new LevelRenderer(this, this.renderEngine);
		GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
		this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);

		try {
			this.downloadResourcesThread = new ThreadDownloadResources(this.mcDataDir, this);
			this.downloadResourcesThread.start();
		} catch (Exception exception3) {
		}
		
		MoreResourcesInstaller moreResourcesInstaller = new MoreResourcesInstaller(this);
		moreResourcesInstaller.installResources();

		this.checkGLError("Post startup");
		this.ingameGUI = new GuiIngame(this);
		if(this.serverName != null) {
			this.displayGuiScreen(new GuiConnecting(this, this.serverName, this.serverPort));
		} else {
			this.displayGuiScreen(new GuiMainMenu());
		}

		this.loadingScreen = new LoadingScreenRenderer(this);
	}

	private void loadScreen() throws LWJGLException {
		ScaledResolution scaledResolution1 = new ScaledResolution(this.displayWidth, this.displayHeight);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0D, scaledResolution1.scaledWidthD, scaledResolution1.scaledHeightD, 0.0D, 1000.0D, 3000.0D);
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
		GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
		GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
		Tessellator tessellator2 = Tessellator.instance;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/title/mojang.png"));
		
		tessellator2.startDrawingQuads();
		tessellator2.setColorOpaque_I(0xFFFFFF);
		tessellator2.addVertexWithUV(0.0D, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
		tessellator2.addVertexWithUV((double)this.displayWidth, (double)this.displayHeight, 0.0D, 0.0D, 0.0D);
		tessellator2.addVertexWithUV((double)this.displayWidth, 0.0D, 0.0D, 0.0D, 0.0D);
		tessellator2.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
		tessellator2.draw();
		short s3 = 256;
		short s4 = 256;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		tessellator2.setColorOpaque_I(0xFFFFFF);
		this.scaledTessellator((scaledResolution1.getScaledWidth() - s3) / 2, (scaledResolution1.getScaledHeight() - s4) / 2, 0, 0, s3, s4);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_FOG);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		Display.swapBuffers();
	}

	public void scaledTessellator(int i1, int i2, int i3, int i4, int i5, int i6) {
		float f7 = 0.00390625F;
		float f8 = 0.00390625F;
		Tessellator tessellator9 = Tessellator.instance;
		tessellator9.startDrawingQuads();
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + i6), 0.0D, (double)((float)(i3 + 0) * f7), (double)((float)(i4 + i6) * f8));
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + i6), 0.0D, (double)((float)(i3 + i5) * f7), (double)((float)(i4 + i6) * f8));
		tessellator9.addVertexWithUV((double)(i1 + i5), (double)(i2 + 0), 0.0D, (double)((float)(i3 + i5) * f7), (double)((float)(i4 + 0) * f8));
		tessellator9.addVertexWithUV((double)(i1 + 0), (double)(i2 + 0), 0.0D, (double)((float)(i3 + 0) * f7), (double)((float)(i4 + 0) * f8));
		tessellator9.draw();
	}

	public static File getMinecraftDir() {
		if(minecraftDir == null) {
			minecraftDir = getAppDir("minecraft");
		}

		return minecraftDir;
	}

	public static File getAppDir(String string0) {
		String string1 = System.getProperty("user.home", ".");
		File file2;
		switch(EnumOSMappingHelper.enumOSMappingArray[getOs().ordinal()]) {
		case 1:
		case 2:
			file2 = new File(string1, '.' + string0 + '/');
			break;
		case 3:
			String string3 = System.getenv("APPDATA");
			if(string3 != null) {
				file2 = new File(string3, "." + string0 + '/');
			} else {
				file2 = new File(string1, '.' + string0 + '/');
			}
			break;
		case 4:
			file2 = new File(string1, "Library/Application Support/" + string0);
			break;
		default:
			file2 = new File(string1, string0 + '/');
		}

		if(!file2.exists() && !file2.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + file2);
		} else {
			return file2;
		}
	}

	private static EnumOS2 getOs() {
		String string0 = System.getProperty("os.name").toLowerCase();
		return string0.contains("win") ? EnumOS2.windows : (string0.contains("mac") ? EnumOS2.macos : (string0.contains("solaris") ? EnumOS2.solaris : (string0.contains("sunos") ? EnumOS2.solaris : (string0.contains("linux") ? EnumOS2.linux : (string0.contains("unix") ? EnumOS2.linux : EnumOS2.unknown)))));
	}

	public ISaveFormat getSaveLoader() {
		return this.saveLoader;
	}

	public void displayGuiScreen(GuiScreen guiScreen1) {
		if(!(this.currentScreen instanceof GuiErrorScreen)) {
			if(this.currentScreen != null) {
				this.currentScreen.onGuiClosed();
			}

			if(guiScreen1 == null && this.theWorld == null) {
				guiScreen1 = new GuiMainMenu();
			} else if(guiScreen1 == null && this.thePlayer.getHealth() <= 0) {
				guiScreen1 = new GuiGameOver();
			}

			if(guiScreen1 instanceof GuiMainMenu) {
				GameSettingsValues.showDebugInfo = false;
				this.ingameGUI.clearChatMessages();
			}

			this.currentScreen = (GuiScreen)guiScreen1;
			if(guiScreen1 != null) {
				this.setIngameNotInFocus();
				ScaledResolution scaledResolution2 = new ScaledResolution(this.displayWidth, this.displayHeight);
				int i3 = scaledResolution2.getScaledWidth();
				int i4 = scaledResolution2.getScaledHeight();
				((GuiScreen)guiScreen1).setWorldAndResolution(this, i3, i4);
				this.skipRenderWorld = false;
			} else {
				this.setIngameFocus();
			}

		}
	}

	private void checkGLError(String string1) {
		int i2 = GL11.glGetError();
		if(i2 != 0) {
			String string3 = GLU.gluErrorString(i2);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + string1);
			System.out.println(i2 + ": " + string3);
		}

	}

	public void shutdownMinecraftApplet() {
		try {
			if(this.mcApplet != null) {
				this.mcApplet.clearApplet();
			}

			try {
				if(this.downloadResourcesThread != null) {
					this.downloadResourcesThread.closeMinecraft();
				}
			} catch (Exception e) {
			}

			System.out.println("Stopping!");

			try {
				this.changeWorld((World)null);
			} catch (Throwable e) {
			}

			try {
				GLAllocation.deleteTexturesAndDisplayLists();
			} catch (Throwable e) {
			}

			this.sndManager.closeMinecraft();
			Mouse.destroy();
			Keyboard.destroy();
		} finally {
			Display.destroy();
			if(!this.hasCrashed) {
				System.exit(0);
			}

		}

		System.gc();
	}

	public void run() {
		this.running = true;

		try {
			this.startGame();
		} catch (Exception exception11) {
			exception11.printStackTrace();
			this.onMinecraftCrash(new UnexpectedThrowable("Failed to start game", exception11));
			return;
		}

		try {
			while(this.running) {
				try {
					this.runGameLoop();
				} catch (MinecraftException e) {
					e.printStackTrace();
					this.theWorld = null;
					this.changeWorld((World)null);
					this.displayGuiScreen(new GuiConflictWarning());
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					this.freeMemory();
					this.displayGuiScreen(new GuiMemoryErrorScreen());
					System.gc();
				}
			}
		} catch (MinecraftError minecraftError12) {
		} catch (Throwable throwable13) {
			this.freeMemory();
			throwable13.printStackTrace();
			this.onMinecraftCrash(new UnexpectedThrowable("Unexpected error", throwable13));
		} finally {
			this.shutdownMinecraftApplet();
		}

	}

	private void runGameLoop() {
		if(this.mcApplet != null && !this.mcApplet.isActive()) {
			this.running = false;
		} else {
			AxisAlignedBB.clearBoundingBoxPool();
			Vec3D.initialize();

			if(this.mcCanvas == null && Display.isCloseRequested()) {
				this.shutdown();
			}

			if(this.isGamePaused && this.theWorld != null) {
				float f1 = this.timer.renderPartialTicks;
				this.timer.updateTimer();
				this.timer.renderPartialTicks = f1;
			} else {
				this.timer.updateTimer();
			}

			for(int i3 = 0; i3 < this.timer.elapsedTicks; ++i3) {
				++this.ticksRan;

				try {
					this.runTick();
				} catch (MinecraftException minecraftException5) {
					this.theWorld = null;
					this.changeWorld((World)null);
					this.displayGuiScreen(new GuiConflictWarning());
				}
			}

			System.nanoTime();
			this.checkGLError("Pre render");
			RenderBlocks.fancyGrass = GameSettingsValues.fancyGraphics;

			this.sndManager.setListener(this.thePlayer, this.timer.renderPartialTicks);

			GL11.glEnable(GL11.GL_TEXTURE_2D);
			if(!Keyboard.isKeyDown(Keyboard.KEY_F7)) {
				Display.update();
			}

			if(this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
				GameSettingsValues.thirdPersonView = 0;
			}

			if(!this.skipRenderWorld) {

				if(this.playerController != null) {
					this.playerController.setPartialTime(this.timer.renderPartialTicks);
				}

				this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
				
				if (this.theWorld != null && this.thePlayer != null) {
					// Zan's
					if(!GameSettingsValues.showDebugInfo && (this.currentScreen == null || this.currentScreen instanceof GuiMinimap)) zanMinimap.OnTickInGame(this);
					
				}
			}

			GL11.glFlush();

			if(!Display.isActive() && this.fullscreen) {
				this.toggleFullscreen();
			}

			Thread.yield();
			if(Keyboard.isKeyDown(Keyboard.KEY_F7)) {
				Display.update();
			}

			this.screenshotListener();
			if(this.mcCanvas != null && !this.fullscreen && (this.mcCanvas.getWidth() != this.displayWidth || this.mcCanvas.getHeight() != this.displayHeight)) {
				this.displayWidth = this.mcCanvas.getWidth();
				this.displayHeight = this.mcCanvas.getHeight();
				if(this.displayWidth <= 0) {
					this.displayWidth = 1;
				}

				if(this.displayHeight <= 0) {
					this.displayHeight = 1;
				}

				this.resize(this.displayWidth, this.displayHeight);
			}

			this.checkGLError("Post render");
			++this.fpsCounter;

			for(this.isGamePaused = !this.isMultiplayerWorld() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame(); System.currentTimeMillis() >= this.debugUpdateTime + 1000L; this.fpsCounter = 0) {
				this.debug = this.fpsCounter + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
				WorldRenderer.chunksUpdated = 0;
				
				BlockLeaves.decays = 0;
				World.notifys = 0;
				this.debugUpdateTime += 1000L;
			}
		}
	}

	public void freeMemory() {
		try {
			field_28006_b = new byte[0];
			this.renderGlobal.func_28137_f();
		} catch (Throwable throwable4) {
		}

		try {
			System.gc();
			AxisAlignedBB.clearBoundingBoxes();
			Vec3D.clearVectorList();
		} catch (Throwable throwable3) {
		}

		try {
			System.gc();
			this.changeWorld((World)null);
		} catch (Throwable throwable2) {
		}

		System.gc();
	}

	private void screenshotListener() {
		if(Keyboard.isKeyDown(Keyboard.KEY_F2)) {
			if(!this.isTakingScreenshot) {
				this.isTakingScreenshot = true;
				this.ingameGUI.addChatMessage(ScreenShotHelper.saveScreenshot(minecraftDir, this.displayWidth, this.displayHeight));
			}
		} else {
			this.isTakingScreenshot = false;
		}

	}
	
	public void shutdown() {
		this.running = false;
	}

	public void setIngameFocus() {
		if(Display.isActive()) {
			if(!this.inGameHasFocus) {
				this.inGameHasFocus = true;
				this.mouseHelper.grabMouseCursor();
				this.displayGuiScreen((GuiScreen)null);
				this.leftClickCounter = 10000;
			}
		}
	}

	public void setIngameNotInFocus() {
		if(this.inGameHasFocus) {
			KeyBinding.unPressAllKeys();
			this.inGameHasFocus = false;
			this.mouseHelper.ungrabMouseCursor();
		}
	}

	public void displayInGameMenu() {
		if(this.currentScreen == null) {
			this.displayGuiScreen(new GuiIngameMenu());
		}
	}

	private void sendClickBlockToController(int i1, boolean z2) {
		if(!z2) {
			this.leftClickCounter = 0;
		}

		if(i1 != 0 || this.leftClickCounter <= 0) {
			if(z2 && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE && i1 == 0) {
				int i3 = this.objectMouseOver.blockX;
				int i4 = this.objectMouseOver.blockY;
				int i5 = this.objectMouseOver.blockZ;
				this.playerController.onPlayerDamageBlock(i3, i4, i5, this.objectMouseOver.sideHit);
				if(this.thePlayer.canPlayerEdit(i3, i4, i5)) {
					this.effectRenderer.addBlockHitEffects(i3, i4, i5, this.objectMouseOver.sideHit);
					this.thePlayer.swingItem();
				}
			} else {
				this.playerController.resetBlockRemoving();
			}

		}
	}

	private void clickMouse(int button) {
		if(button != 0 || this.leftClickCounter <= 0) {
			if(button == 0) {
				this.thePlayer.swingItem();
			}

			if(button == 1) {
				this.rightClickDelayTimer = 4;
			}

			boolean flag = true;
			ItemStack currentItem = this.thePlayer.inventory.getCurrentItem();
			if(this.objectMouseOver == null) {
				if(button == 0 && this.playerController.isNotCreative()) {
					this.leftClickCounter = 10;
				}
			} else if(this.objectMouseOver.typeOfHit == EnumMovingObjectType.ENTITY) {
				if(button == 0) {
					this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
				}

				if(button == 1) {
					this.playerController.interactWithEntity(this.thePlayer, this.objectMouseOver.entityHit);
				}
			} else if(this.objectMouseOver.typeOfHit == EnumMovingObjectType.TILE) {
				int x = this.objectMouseOver.blockX;
				int y = this.objectMouseOver.blockY;
				int z = this.objectMouseOver.blockZ;
				int side = this.objectMouseOver.sideHit;
				
				float xWithinFace = (float) (this.objectMouseOver.hitVec.xCoord - (float)x);
				float yWithinFace = (float) (this.objectMouseOver.hitVec.yCoord - (float)y);
				float zWithinFace = (float) (this.objectMouseOver.hitVec.zCoord - (float)z);
				
				if(button == 0) {
					this.playerController.clickBlock(x, y, z, this.objectMouseOver.sideHit);
				} else {
					int i9 = currentItem != null ? currentItem.stackSize : 0;
					if(this.playerController.onPlayerRightClick(this.thePlayer, this.theWorld, currentItem, x, y, z, side, xWithinFace, yWithinFace, zWithinFace)) {
						flag = false;
						this.thePlayer.swingItem();
					}

					if(currentItem == null) {
						return;
					}

					if(currentItem.stackSize == 0) {
						this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
					} else if(currentItem.stackSize != i9 || this.playerController.isInCreativeMode()) {
						this.entityRenderer.itemRenderer.resetEquippedProgress();
					}
				}
			}

			if(flag && button == 1) {
				currentItem = this.thePlayer.inventory.getCurrentItem();
				if(currentItem != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, currentItem)) {
					this.entityRenderer.itemRenderer.resetEquippedProgress();
				}
			}

		}
	}

	public void toggleFullscreen() {
		try {
			this.fullscreen = !this.fullscreen;
			if(this.fullscreen) {
				if(GameSettingsValues.displayMode == null || "DEFAULT".equals(GameSettingsValues.displayMode)) {
					Display.setDisplayMode(Display.getDesktopDisplayMode());
				} else {
					GraphicsMode graphicsMode = new GraphicsMode(GameSettingsValues.displayMode);
					final DisplayMode[] displayModes = Display.getAvailableDisplayModes();
					boolean found = false;
					for(int i = 0; i < displayModes.length && !found; i ++) {
						DisplayMode mode = displayModes[i];
						if(mode.getWidth() == graphicsMode.w && mode.getHeight() == graphicsMode.h &&
								mode.getBitsPerPixel() == graphicsMode.d && mode.getFrequency() == graphicsMode.f) {
							System.out.println("Setting mode " + mode.getWidth() + "x" + mode.getHeight() + "x" + mode.getBitsPerPixel() + " " + mode.getFrequency() + "Hz");
							Display.setDisplayMode(mode);
							found = true;
						}
					}
					
					if(!found) Display.setDisplayMode(Display.getDesktopDisplayMode());
				}
				this.displayWidth = Display.getDisplayMode().getWidth();
				this.displayHeight = Display.getDisplayMode().getHeight();
				if(this.displayWidth <= 0) {
					this.displayWidth = 1;
				}

				if(this.displayHeight <= 0) {
					this.displayHeight = 1;
				}
			} else {
				if(this.mcCanvas != null) {
					this.displayWidth = this.mcCanvas.getWidth();
					this.displayHeight = this.mcCanvas.getHeight();
				} else {
					this.displayWidth = this.tempDisplayWidth;
					this.displayHeight = this.tempDisplayHeight;
				}

				if(this.displayWidth <= 0) {
					this.displayWidth = 1;
				}

				if(this.displayHeight <= 0) {
					this.displayHeight = 1;
				}
			}

			if(this.currentScreen != null) {
				this.resize(this.displayWidth, this.displayHeight);
			}

			Display.setFullscreen(this.fullscreen);
			Display.update();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

	}

	private void resize(int i1, int i2) {
		if(i1 <= 0) {
			i1 = 1;
		}

		if(i2 <= 0) {
			i2 = 1;
		}

		this.displayWidth = i1;
		this.displayHeight = i2;
		if(this.currentScreen != null) {
			ScaledResolution scaledResolution3 = new ScaledResolution(i1, i2);
			int i4 = scaledResolution3.getScaledWidth();
			int i5 = scaledResolution3.getScaledHeight();
			this.currentScreen.setWorldAndResolution(this, i4, i5);
		}

	}

	/*
	private void startThreadCheckHasPaid() {
		(new ThreadCheckHasPaid(this)).start();
	}
	*/

	public void runTick() {
		if(this.rightClickDelayTimer > 0) {
			--this.rightClickDelayTimer;
		}

		/*
		if(this.ticksRan == 6000) {
			this.startThreadCheckHasPaid();
		}
		*/

		if(!this.isGamePaused) {
			this.ingameGUI.updateTick();
		}

		this.entityRenderer.getMouseOver(1.0F);
		int i3;

		if(!this.isGamePaused && this.theWorld != null) {
			this.playerController.updateController();
		}

		GL11.glBindTexture(GL11.GL_TEXTURE_2D, this.renderEngine.getTexture("/terrain.png"));

		if(!this.isGamePaused) {
			this.renderEngine.updateDynamicTextures();
		}

		if(this.currentScreen == null && this.thePlayer != null) {
			if(this.thePlayer.getHealth() <= 0) {
				this.displayGuiScreen((GuiScreen)null);
			} else if(this.thePlayer.isPlayerSleeping() && this.theWorld != null && this.theWorld.isRemote) {
				this.displayGuiScreen(new GuiSleepMP(this));
			}
		} else if(this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
			this.displayGuiScreen((GuiScreen)null);
		}

		if(this.currentScreen != null) {
			this.leftClickCounter = 10000;
		}

		if(this.currentScreen != null) {
			this.currentScreen.handleInput();
			if(this.currentScreen != null) {
				this.currentScreen.guiParticles.update();
				this.currentScreen.updateScreen();
			}
		}

		if(this.currentScreen == null || this.currentScreen.allowUserInput) {

			while(Mouse.next()) {
				KeyBinding.setKeyBindState(Mouse.getEventButton() - 100, Mouse.getEventButtonState());
				if(Mouse.getEventButtonState()) {
					KeyBinding.onTick(Mouse.getEventButton() - 100);
				}

				long j5 = System.currentTimeMillis() - this.systemTime;
				if(j5 <= 200L) {
					i3 = Mouse.getEventDWheel();
					if(i3 != 0) {
						this.thePlayer.inventory.changeCurrentItem(i3);
						if(GameSettingsValues.noclip) {
							if(i3 > 0) {
								i3 = 1;
							}

							if(i3 < 0) {
								i3 = -1;
							}

							GameSettingsValues.noclipRate += (float)i3 * 0.25F;
						}
					}

					if(this.currentScreen == null) {
						if(!this.inGameHasFocus && Mouse.getEventButtonState()) {
							this.setIngameFocus();
						}
					} else if(this.currentScreen != null) {
						this.currentScreen.handleMouseInput();
					}
				}
			}

			if(this.leftClickCounter > 0) {
				--this.leftClickCounter;
			}

			label361:
			while(true) {
				while(true) {
					do {
						if(!Keyboard.next()) {
							while(GameSettingsKeys.keyBindInventory.isPressed()) {
								this.displayGuiScreen(new GuiInventory(this.thePlayer));
							}

							while(GameSettingsKeys.keyBindDrop.isPressed()) {
								this.thePlayer.dropOneItem();
							}

							while((this.isMultiplayerWorld() || this.theWorld.getWorldInfo().isEnableCheats()) && GameSettingsKeys.keyBindChat.isPressed()) {
								this.displayGuiScreen(new GuiChat(this));
							}

							if(this.isMultiplayerWorld() && this.currentScreen == null && (Keyboard.isKeyDown(Keyboard.KEY_SLASH) || Keyboard.isKeyDown(Keyboard.KEY_DIVIDE))) {
								this.displayGuiScreen(new GuiChat("/"));
							}

							if(this.thePlayer.isUsingItem()) {
								if(!GameSettingsKeys.keyBindUseItem.pressed) {
									this.playerController.onStoppedUsingItem(this.thePlayer);
								}

								while(true) {
									if(!GameSettingsKeys.keyBindAttack.isPressed()) {
										while(GameSettingsKeys.keyBindUseItem.isPressed()) {
										}

										while(GameSettingsKeys.keyBindPickBlock.isPressed()) {
										}
										break;
									}
								}
							} else {
								while(GameSettingsKeys.keyBindAttack.isPressed()) {
									this.clickMouse(0);
								}

								while(GameSettingsKeys.keyBindUseItem.isPressed()) {
									this.clickMouse(1);
								}

								while(GameSettingsKeys.keyBindPickBlock.isPressed()) {
									this.clickMiddleMouseButton();
								}
							}

							if(GameSettingsKeys.keyBindUseItem.pressed && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
								this.clickMouse(1);
							}

							this.sendClickBlockToController(0, this.currentScreen == null && GameSettingsKeys.keyBindAttack.pressed && this.inGameHasFocus);
							break label361;
						}

						KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
						if(Keyboard.getEventKeyState()) {
							KeyBinding.onTick(Keyboard.getEventKey());
						}
					} while(!Keyboard.getEventKeyState());

					if(Keyboard.getEventKey() == Keyboard.KEY_F11) {
						this.toggleFullscreen();
					} else {
						if(this.currentScreen != null) {
							this.currentScreen.handleKeyboardInput();
						} else {
							if(Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
								this.displayInGameMenu();
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_S && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
								this.forceReload();
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_T && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
								this.renderEngine.refreshTextures();
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_F && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
								boolean z6 = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) | Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);
								this.gameSettings.setOptionValue(EnumOptions.RENDER_DISTANCE, z6 ? -1 : 1);
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_A && Keyboard.isKeyDown(Keyboard.KEY_F3)) {
								this.renderGlobal.loadRenderers();
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_F1) {
								GameSettingsValues.hideGUI = !GameSettingsValues.hideGUI;
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_F3) {
								GameSettingsValues.showDebugInfo = !GameSettingsValues.showDebugInfo;
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_F5) {
								++GameSettingsValues.thirdPersonView;
								if(GameSettingsValues.thirdPersonView > 2) {
									GameSettingsValues.thirdPersonView = 0;
								}
							}

							if(Keyboard.getEventKey() == Keyboard.KEY_F8) {
								GameSettingsValues.smoothCamera = !GameSettingsValues.smoothCamera;
							}
						}

						int i7;
						for(i7 = 0; i7 < 9; ++i7) {
							if(Keyboard.getEventKey() == Keyboard.KEY_1 + i7) {
								this.thePlayer.inventory.currentItem = i7;
							}
						}

					}
				}
			}
		}

		if(this.theWorld != null) {
			if(this.thePlayer != null) {
				++this.joinPlayerCounter;
				if(this.joinPlayerCounter == 30) {
					this.joinPlayerCounter = 0;
					this.theWorld.joinEntityInSurroundings(this.thePlayer);
				}
			}

			if(this.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
				this.theWorld.difficultySetting = 3;
			} else {
				this.theWorld.difficultySetting = GameSettingsValues.difficulty;
			}

			if(this.theWorld.isRemote) {
				this.theWorld.difficultySetting = 1;
			}

			if(!this.isGamePaused) {
				this.entityRenderer.updateRenderer();
			}

			if(!this.isGamePaused) {
				this.renderGlobal.updateClouds();
			}

			if(!this.isGamePaused) {
				if(this.theWorld.lightningFlash > 0) {
					--this.theWorld.lightningFlash;
				}

				this.theWorld.updateEntities();
			}

			if(!this.isGamePaused || this.isMultiplayerWorld()) {
				this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting > 0, true);
				this.theWorld.tick();
			}

			if(!this.isGamePaused && this.theWorld != null) {
				this.theWorld.randomDisplayUpdates(MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ));
			}

			if(!this.isGamePaused) {
				this.effectRenderer.updateEffects();
			}
		}

		this.systemTime = System.currentTimeMillis();
	}

	private void forceReload() {
		System.out.println("FORCING RELOAD!");
		this.sndManager = new SoundManager();
		this.sndManager.loadSoundSettings(this.gameSettings);
		this.downloadResourcesThread.reloadResources();
	}

	public boolean isMultiplayerWorld() {
		return this.theWorld != null && this.theWorld.isRemote;
	}

	public void startWorld(String saveName, String displayName, WorldSettings worldSettings) { 
		this.changeWorld((World)null);
		System.gc();
		if(this.saveLoader.isOldMapFormat(saveName)) {
			this.convertMapFormat(saveName, displayName);
		} else {
			if(this.loadingScreen != null) {
				this.loadingScreen.setHint(GameHints.getRandomHint());
				this.loadingScreen.printText(Translator.translateToLocal("menu.switchingLevel"));
				this.loadingScreen.displayLoadingString("");
			}

			ISaveHandler saveHandler = this.saveLoader.getSaveLoader(saveName, false);

			World world = null;
			
			// When loading a world from disk, worldSettings == null!
			world = new World(saveHandler, displayName, worldSettings);
			
			if(world.isNewWorld) {
				this.changeWorld(world, Translator.translateToLocal("menu.generatingLevel"));
			} else {
				this.changeWorld(world, Translator.translateToLocal("menu.loadingLevel"));
			}
		}

	}

	public void usePortal(int toDimension) {
		int fromDimension = this.thePlayer.dimension;
		this.thePlayer.dimension = toDimension;
		this.theWorld.setEntityDead(this.thePlayer);
		this.thePlayer.isDead = false;
		
		double newPosX = this.thePlayer.posX;
		double newPosY = this.thePlayer.posY;
		double newPosZ = this.thePlayer.posZ;

		World newWorld;
		
		Teleporter teleporter = new Teleporter();
		
		if(this.thePlayer.dimension == -1) {
			// Player goes to the nether.
			newPosX /= 8D;
			newPosZ /= 8D;
			
			this.thePlayer.setLocationAndAngles(newPosX, newPosY, newPosZ, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
			if(this.thePlayer.isEntityAlive()) {
				this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
			}

			newWorld = new World(this.theWorld, WorldProvider.getProviderForDimension(this.thePlayer.dimension, this.theWorld.getWorldInfo().getTerrainType()));
			this.changeWorld(newWorld, "Entering the Nether", this.thePlayer);
		} else if(this.thePlayer.dimension == 0) {
			// Player goes to the overworld
			if(fromDimension == -1) {
				newPosX *= 8D;
				newPosZ *= 8D;
			} 
			
			if(this.thePlayer.isEntityAlive()) {
				this.thePlayer.setLocationAndAngles(newPosX, newPosY, newPosZ, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
				this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
			}

			newWorld = new World(this.theWorld, WorldProvider.getProviderForTerrainType(this.theWorld.getWorldInfo().getTerrainType()));

			if(fromDimension == -1) {
				this.changeWorld(newWorld, "Leaving the Nether", this.thePlayer);
			} else if(fromDimension == 1) {
				this.changeWorld(newWorld, "Leaving the End", this.thePlayer);
			} else if(fromDimension == 7) {
				this.changeWorld(newWorld, "Leaving the Twilight Forest", this.thePlayer);
			} else if(fromDimension == 9) {
				this.changeWorld(newWorld, "Leaving the Desert Dimension", this.thePlayer);
			}
		}
		
		this.thePlayer.worldObj = this.theWorld;
		System.out.println("Teleported to " + this.theWorld.worldProvider.worldType);
		if(this.thePlayer.isEntityAlive() && fromDimension != 1) {
			this.thePlayer.setLocationAndAngles(newPosX, this.thePlayer.posY, newPosZ, this.thePlayer.rotationYaw, this.thePlayer.rotationPitch);
			this.theWorld.updateEntityWithOptionalForce(this.thePlayer, false);
			teleporter.placeInPortal(this.theWorld, this.thePlayer);
		}
 
	}


	public void exitToMainMenu(String string1) {
		this.theWorld = null;
		this.changeWorld((World)null, string1);
	}

	public void changeWorld(World world) {
		this.changeWorld(world, "");
	}

	public void changeWorld(World world, String message) {
		this.changeWorld(world, message, (EntityPlayer)null);
	}

	public void changeWorld(World world, String message, EntityPlayer entityPlayer3) {
		this.renderViewEntity = null;
		if(this.loadingScreen != null) {
			this.loadingScreen.printText(message);
			this.loadingScreen.displayLoadingString("");
		}

		this.sndManager.playStreaming((String)null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
		if(this.theWorld != null) {
			this.theWorld.saveWorldIndirectly(this.loadingScreen);
		}

		this.theWorld = world;
		if(world != null) {
			if(this.playerController != null) {
				this.playerController.onWorldChange(world);
			}
			
			if(!this.isMultiplayerWorld()) {
				if(entityPlayer3 == null) {
					this.thePlayer = null;
				}
			} else if(this.thePlayer != null) {
				this.thePlayer.preparePlayerToSpawn();
				if(world != null) {
					world.spawnEntityInWorld(this.thePlayer);
				}
			}

			if(!world.isRemote) {
				this.preloadWorld(message);
			}

			if(this.thePlayer == null) {
				this.thePlayer = (EntityPlayerSP)this.playerController.createPlayer(world);	
				this.thePlayer.preparePlayerToSpawn();
				this.playerController.flipPlayer(this.thePlayer);
			}

			this.thePlayer.movementInput = new MovementInputFromOptions();
			if(this.renderGlobal != null) {
				this.renderGlobal.changeWorld(world);
			}

			if(this.effectRenderer != null) {
				this.effectRenderer.clearEffects(world);
			}

			if(entityPlayer3 != null) {
				world.func_6464_c();
			}

			world.spawnPlayerWithLoadedChunks(this.thePlayer);
			this.playerController.func_6473_b(this.thePlayer);
			if(world.isNewWorld) {
				world.saveWorldIndirectly(this.loadingScreen);
			}

			this.renderViewEntity = this.thePlayer;
		} else {
			this.saveLoader.flushCache();
			this.thePlayer = null;
		}

		System.gc();
	}

	private void convertMapFormat(String saveName, String displayName) {
		if(this.saveLoader.getSaveVersion(saveName) == 0) {
			this.loadingScreen.printText("Converting World to MCRegion");
			this.loadingScreen.displayLoadingString("This may take a while :)");
			this.saveLoader.converMapToMCRegion(saveName, this.loadingScreen);
		}
		
		this.loadingScreen.printText("Converting World to " + this.saveLoader.getFormatName());
		this.loadingScreen.displayLoadingString("This may take a while :)");
		this.saveLoader.convertMapFormat(saveName, this.loadingScreen);
		this.startWorld(saveName, displayName, new WorldSettings(0L, 0, true, false, false, WorldType.DEFAULT));
	}

	private void preloadWorld(String message) {
		if(this.loadingScreen != null) {
			this.loadingScreen.printText(message);
			this.loadingScreen.displayLoadingString(Translator.translateToLocal("menu.generatingTerrain"));
		}

		short radius = 128;
		if(this.playerController.func_35643_e()) {
			radius = 64;
		}

		int progress = 0;
		int maxChunks = radius * 2 / 16 + 1;
		maxChunks *= maxChunks;
		ChunkCoordinates chunkCoordinates6 = this.theWorld.getSpawnPoint();
		
		// Make sure the spawn chunk loads FIRST
		this.theWorld.getBlockId(chunkCoordinates6.posX, 64, chunkCoordinates6.posZ);
		
		if(this.thePlayer != null) {
			chunkCoordinates6.posX = (int)this.thePlayer.posX;
			chunkCoordinates6.posZ = (int)this.thePlayer.posZ;
		}

		for(int dx = -radius; dx <= radius; dx += 16) {
			for(int dz = -radius; dz <= radius; dz += 16) {
				if(this.loadingScreen != null) {
					this.loadingScreen.setLoadingProgress(progress++ * 100 / maxChunks);
				}

				this.theWorld.getBlockId(chunkCoordinates6.posX + dx, 64, chunkCoordinates6.posZ + dz);
				
			}
		}

		if(!this.playerController.func_35643_e()) {
			if(this.loadingScreen != null) {
				this.loadingScreen.displayLoadingString(Translator.translateToLocal("menu.simulating"));
			}

			this.theWorld.dropOldChunks();
		}

	}

	public void installResource(String string1, File file2) {
		int i3 = string1.indexOf("/");
		String string4 = string1.substring(0, i3);
		string1 = string1.substring(i3 + 1);
		if(string4.equalsIgnoreCase("sound")) {
			this.sndManager.addSound(string1, file2);
		} else if(string4.equalsIgnoreCase("newsound")) {
			this.sndManager.addSound(string1, file2);
		} else if(string4.equalsIgnoreCase("streaming")) {
			this.sndManager.addStreaming(string1, file2);
		} else if(string4.equalsIgnoreCase("music")) {
			this.sndManager.addMusic(string1, file2);
		} else if(string4.equalsIgnoreCase("newmusic")) {
			this.sndManager.addMusic(string1, file2);
		}

	}
	
	public void installResourceURL(String name, URL url) {
		int i3 = name.indexOf("/");
		name = name.substring(i3 + 1);
		this.sndManager.addSoundURL(name, url);
		System.out.println(url);
	}

	public String debugInfoRenders() {
		return this.renderGlobal.getDebugInfoRenders();
	}

	public String getEntityDebug() {
		return this.renderGlobal.getDebugInfoEntities();
	}

	public String getWorldProviderName() {
		return this.theWorld.getProviderName();
	}

	public String debugInfoEntities() {
		return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
	}

	public void respawn(boolean doRespawn, int dimensionTo, boolean recreate) {
		// The world being remote means that you aren't here 'cause a packet 9 was received
		// Hence this is a proper respawn - upon game over or upon win game. So we must send
		// the player to the right dimension - i.e. the spawn dimension (on game over) or 0 (on win game)
		if(!this.theWorld.isRemote) {
			if (!this.theWorld.worldProvider.canRespawnHere() || dimensionTo != this.thePlayer.dimension) {
				this.usePortal(dimensionTo);
			}
		}

		// If doRespawn == false, that is, this wasn't called from NetClientHandler.handlePacket9Respawn...
		// If valid chunk coordinates could be retrieved & verified, store them in spawnDimension:chunkCoordinates 5
		ChunkCoordinates spawnCoordinates = null;
		ChunkCoordinates newSpawnCoordinates = null;
		int spawnDimension = 0;
		boolean copyRespawnInfo = true;
		if(this.thePlayer != null && !doRespawn) {
			spawnCoordinates = this.thePlayer.getSpawnChunk();	
			if(spawnCoordinates != null) {
				newSpawnCoordinates = EntityPlayer.verifyRespawnCoordinates(this.theWorld, spawnCoordinates);	
				if (newSpawnCoordinates == null) {
					this.thePlayer.addChatMessage("tile.bed.notValid");
				} else {
					spawnDimension = this.thePlayer.getSpawnDimension();
				}
			}
		}

		// If no valid chunk coordinates could be retrieved, get the default spawn point.
		if(newSpawnCoordinates == null) {
			newSpawnCoordinates = this.theWorld.getSpawnPoint();
			copyRespawnInfo = false;
		}

		this.theWorld.setSpawnLocation();
		this.theWorld.updateEntityList();
		
		// Destroy current player and recreate it.
		int i10 = 0;
		if(this.thePlayer != null) {
			i10 = this.thePlayer.entityId;
			this.theWorld.setEntityDead(this.thePlayer);
		}

		EntityPlayerSP entityPlayerSP9 = this.thePlayer;
		this.renderViewEntity = null;
		this.thePlayer = (EntityPlayerSP)this.playerController.createPlayer(this.theWorld);
		
		// If this was called after wining the game,
		// Player has not died, hence copy everything including the inventory.
		if(recreate) {
			this.thePlayer.copyPlayer(entityPlayerSP9);
		}

		// Of course, in the right dimension.
		this.thePlayer.dimension = dimensionTo;
		this.renderViewEntity = this.thePlayer;
		this.thePlayer.preparePlayerToSpawn();
		
		// If a respawn location was set correctly, copy it to the newly created player.
		if(copyRespawnInfo) {
			this.thePlayer.setSpawnChunk(spawnCoordinates);
			this.thePlayer.setSpawnDimension(spawnDimension);
			this.thePlayer.setLocationAndAngles((double)((float)newSpawnCoordinates.posX + 0.5F), (double)((float)newSpawnCoordinates.posY + 0.1F), (double)((float)newSpawnCoordinates.posZ + 0.5F), 0.0F, 0.0F);
		}

		this.playerController.flipPlayer(this.thePlayer);
		this.theWorld.spawnPlayerWithLoadedChunks(this.thePlayer);
		this.thePlayer.movementInput = new MovementInputFromOptions();
		this.thePlayer.entityId = i10;
		this.thePlayer.func_6420_o();
		this.playerController.func_6473_b(this.thePlayer);
		this.preloadWorld(Translator.translateToLocal("menu.respawning"));
		if(this.currentScreen instanceof GuiGameOver) {
			this.displayGuiScreen((GuiScreen)null);
		}

	}

	public static void startMainThread1(String string0, String string1) throws LWJGLException {
		startMainThread(string0, string1, (String)null);
	}

	public static void startMainThread(String string0, String string1, String string2) throws LWJGLException {
		boolean z3 = false;
		Frame frame5 = new Frame("Minecraft");
		Canvas canvas6 = new Canvas();
		frame5.setLayout(new BorderLayout());
		frame5.add(canvas6, "Center");
		canvas6.setPreferredSize(new Dimension(854, 480));
		frame5.pack();
		frame5.setLocationRelativeTo((Component)null);
		MinecraftImpl minecraftImpl7 = new MinecraftImpl(frame5, canvas6, (MinecraftApplet)null, 854, 480, z3, frame5);
		Thread thread8 = new Thread(minecraftImpl7, "Minecraft main thread");
		thread8.setPriority(10);
		minecraftImpl7.minecraftUri = "www.minecraft.net";
		if(string0 != null && string1 != null) {
			minecraftImpl7.session = new User(string0, string1);
		} else {
			minecraftImpl7.session = new User("Player" + System.currentTimeMillis() % 1000L, "");
		}

		if(string2 != null) {
			String[] string9 = string2.split(":");
			minecraftImpl7.setServer(string9[0], Integer.parseInt(string9[1]));
		}

		frame5.setVisible(true);
		frame5.addWindowListener(new GameWindowListener(minecraftImpl7, thread8));
		thread8.start();
	}

	public NetClientHandler getSendQueue() {
		return this.thePlayer instanceof EntityClientPlayerMP ? ((EntityClientPlayerMP)this.thePlayer).sendQueue : null;
	}

	public static void main(String[] string0) throws LWJGLException {
		String string1 = null;
		String string2 = null;
		string1 = "Player" + System.currentTimeMillis() % 1000L;
		if(string0.length > 0) {
			string1 = string0[0];
		}

		string2 = "-";
		if(string0.length > 1) {
			string2 = string0[1];
		}

		startMainThread1(string1, string2);
	}

	public static boolean isGuiEnabled() {
		return theMinecraft == null || !GameSettingsValues.hideGUI;
	}

	public static boolean isFancyGraphicsEnabled() {
		return theMinecraft != null && GameSettingsValues.fancyGraphics;
	}

	public static boolean isAmbientOcclusionEnabled() {
		return theMinecraft != null && GameSettingsValues.ambientOcclusion;
	}

	public static boolean isDebugInfoEnabled() {
		return theMinecraft != null && GameSettingsValues.showDebugInfo;
	}

	public boolean lineIsCommand(String string1) {
		if(string1.startsWith("/")) {
			;
		}

		return false;
	}

	private void clickMiddleMouseButton() {
		if(this.objectMouseOver != null) {
			boolean isCreative = this.thePlayer.capabilities.isCreativeMode;
			int blockID = this.theWorld.getBlockId(this.objectMouseOver.blockX, this.objectMouseOver.blockY, this.objectMouseOver.blockZ);
			if(!isCreative) {
				if(blockID == Block.grass.blockID) {
					blockID = Block.dirt.blockID;
				}

				if(blockID == Block.stairDouble.blockID) {
					blockID = Block.stairSingle.blockID;
				}

				if(blockID == Block.bedrock.blockID) {
					blockID = Block.stone.blockID;
				}
			}

			int metadata = 0;
			boolean z4 = false;
			if(Item.itemsList[blockID] != null && Item.itemsList[blockID].getHasSubtypes()) {
				metadata = this.theWorld.getBlockMetadata(this.objectMouseOver.blockX, this.objectMouseOver.blockY, this.objectMouseOver.blockZ);
				z4 = true;
			}

			if(Item.itemsList[blockID] != null && Item.itemsList[blockID] instanceof ItemBlock) {
				Block block5 = Block.blocksList[blockID];
				int blockIDdropped = block5.idDropped(metadata, this.thePlayer.worldObj.rand, 0);
				metadata = block5.damageDropped(metadata);
				if(blockIDdropped > 0) {
					blockID = blockIDdropped;
				}
			}

			this.thePlayer.inventory.setCurrentItem(blockID, metadata, z4, isCreative);
			if(isCreative) {
				int i7 = this.thePlayer.inventorySlots.inventorySlots.size() - 9 + this.thePlayer.inventory.currentItem;
				this.playerController.sendSlotPacket(this.thePlayer.inventory.getStackInSlot(this.thePlayer.inventory.currentItem), i7);
			}
		}

	}

	public static String func_52003_C() {
		return "1.2.5";
	}

	public static long getSystemTime() {
		return Sys.getTime() * 1000L / Sys.getTimerResolution();
	}
}
