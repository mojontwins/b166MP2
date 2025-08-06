package net.minecraft.isom;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferStrategy;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.world.level.World;
import net.minecraft.world.level.WorldSettings;
import net.minecraft.world.level.WorldType;
import net.minecraft.world.level.chunk.ChunkCoordinates;
import net.minecraft.world.level.chunk.storage.SaveHandler;

public class CanvasIsomPreview extends Canvas implements KeyListener, MouseListener, MouseMotionListener, Runnable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -976345097876502093L;
	private int currentRender = 0;
	private int zoom = 2;
	private boolean showHelp = true;
	private World level;
	private File workDir = this.getWorkingDirectory();
	private boolean running = true;
	private List<IsoImageBuffer> zonesToRender = Collections.synchronizedList(new LinkedList<IsoImageBuffer>());
	private IsoImageBuffer[][] zoneMap = new IsoImageBuffer[64][64];
	private int field_1785_i;
	private int field_1784_j;
	private int field_1783_k;
	private int field_1782_l;

	public File getWorkingDirectory() {
		if(this.workDir == null) {
			this.workDir = this.getWorkingDirectory("minecraft");
		}

		return this.workDir;
	}

	public File getWorkingDirectory(String string1) {
		String string2 = System.getProperty("user.home", ".");
		File file3;
		switch(OsMap.osValues[getPlatform().ordinal()]) {
		case 1:
		case 2:
			file3 = new File(string2, '.' + string1 + '/');
			break;
		case 3:
			String string4 = System.getenv("APPDATA");
			if(string4 != null) {
				file3 = new File(string4, "." + string1 + '/');
			} else {
				file3 = new File(string2, '.' + string1 + '/');
			}
			break;
		case 4:
			file3 = new File(string2, "Library/Application Support/" + string1);
			break;
		default:
			file3 = new File(string2, string1 + '/');
		}

		if(!file3.exists() && !file3.mkdirs()) {
			throw new RuntimeException("The working directory could not be created: " + file3);
		} else {
			return file3;
		}
	}

	private static EnumOS1 getPlatform() {
		String string0 = System.getProperty("os.name").toLowerCase();
		return string0.contains("win") ? EnumOS1.windows : (string0.contains("mac") ? EnumOS1.macos : (string0.contains("solaris") ? EnumOS1.solaris : (string0.contains("sunos") ? EnumOS1.solaris : (string0.contains("linux") ? EnumOS1.linux : (string0.contains("unix") ? EnumOS1.linux : EnumOS1.unknown)))));
	}

	public CanvasIsomPreview() {
		for(int i1 = 0; i1 < 64; ++i1) {
			for(int i2 = 0; i2 < 64; ++i2) {
				this.zoneMap[i1][i2] = new IsoImageBuffer((World)null, i1, i2);
			}
		}

		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.requestFocus();
		this.setBackground(Color.red);
	}

	public void loadLevel(String string1) {
		this.field_1785_i = this.field_1784_j = 0;
		this.level = new World(new SaveHandler(new File(this.workDir, "saves"), string1, false), string1, new WorldSettings((new Random()).nextLong(), 0, true, false, false, WorldType.DEFAULT));
		this.level.skylightSubtracted = 0;
		synchronized(this.zonesToRender) {
			this.zonesToRender.clear();

			for(int i3 = 0; i3 < 64; ++i3) {
				for(int i4 = 0; i4 < 64; ++i4) {
					this.zoneMap[i3][i4].init(this.level, i3, i4);
				}
			}

		}
	}

	private void setBrightness(int i1) {
		synchronized(this.zonesToRender) {
			this.level.skylightSubtracted = i1;
			this.zonesToRender.clear();

			for(int i3 = 0; i3 < 64; ++i3) {
				for(int i4 = 0; i4 < 64; ++i4) {
					this.zoneMap[i3][i4].init(this.level, i3, i4);
				}
			}

		}
	}

	public void start() {
		(new ThreadRunIsoClient(this)).start();

		for(int i1 = 0; i1 < 8; ++i1) {
			(new Thread(this)).start();
		}

	}

	public void stop() {
		this.running = false;
	}

	private IsoImageBuffer getZone(int i1, int i2) {
		int i3 = i1 & 63;
		int i4 = i2 & 63;
		IsoImageBuffer isoImageBuffer5 = this.zoneMap[i3][i4];
		if(isoImageBuffer5.x == i1 && isoImageBuffer5.y == i2) {
			return isoImageBuffer5;
		} else {
			synchronized(this.zonesToRender) {
				this.zonesToRender.remove(isoImageBuffer5);
			}

			isoImageBuffer5.init(i1, i2);
			return isoImageBuffer5;
		}
	}

	public void run() {
		TerrainTextureManager terrainTextureManager1 = new TerrainTextureManager();

		while(this.running) {
			IsoImageBuffer isoImageBuffer2 = null;
			synchronized(this.zonesToRender) {
				if(this.zonesToRender.size() > 0) {
					isoImageBuffer2 = (IsoImageBuffer)this.zonesToRender.remove(0);
				}
			}

			if(isoImageBuffer2 != null) {
				if(this.currentRender - isoImageBuffer2.lastVisible < 2) {
					terrainTextureManager1.render(isoImageBuffer2);
					this.repaint();
				} else {
					isoImageBuffer2.addedToRenderQueue = false;
				}
			}

			try {
				Thread.sleep(2L);
			} catch (InterruptedException interruptedException5) {
				interruptedException5.printStackTrace();
			}
		}

	}

	public void update(Graphics graphics1) {
	}

	public void paint(Graphics graphics1) {
	}

	public void render() {
		BufferStrategy bufferStrategy1 = this.getBufferStrategy();
		if(bufferStrategy1 == null) {
			this.createBufferStrategy(2);
		} else {
			this.render((Graphics2D)bufferStrategy1.getDrawGraphics());
			bufferStrategy1.show();
		}
	}

	public void render(Graphics2D graphics2D1) {
		++this.currentRender;
		AffineTransform affineTransform2 = graphics2D1.getTransform();
		graphics2D1.setClip(0, 0, this.getWidth(), this.getHeight());
		graphics2D1.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		graphics2D1.translate(this.getWidth() / 2, this.getHeight() / 2);
		graphics2D1.scale((double)this.zoom, (double)this.zoom);
		graphics2D1.translate(this.field_1785_i, this.field_1784_j);
		if(this.level != null) {
			ChunkCoordinates chunkCoordinates3 = this.level.getSpawnPoint();
			graphics2D1.translate(-(chunkCoordinates3.posX + chunkCoordinates3.posZ), -(-chunkCoordinates3.posX + chunkCoordinates3.posZ) + 64);
		}

		Rectangle rectangle17 = graphics2D1.getClipBounds();
		graphics2D1.setColor(new Color(-15724512));
		graphics2D1.fillRect(rectangle17.x, rectangle17.y, rectangle17.width, rectangle17.height);
		byte b4 = 16;
		byte b5 = 3;
		int i6 = rectangle17.x / b4 / 2 - 2 - b5;
		int i7 = (rectangle17.x + rectangle17.width) / b4 / 2 + 1 + b5;
		int i8 = rectangle17.y / b4 - 1 - b5 * 2;
		int i9 = (rectangle17.y + rectangle17.height + 16 + 128) / b4 + 1 + b5 * 2;

		int i10;
		for(i10 = i8; i10 <= i9; ++i10) {
			for(int i11 = i6; i11 <= i7; ++i11) {
				int i12 = i11 - (i10 >> 1);
				int i13 = i11 + (i10 + 1 >> 1);
				IsoImageBuffer isoImageBuffer14 = this.getZone(i12, i13);
				isoImageBuffer14.lastVisible = this.currentRender;
				if(!isoImageBuffer14.rendered) {
					if(!isoImageBuffer14.addedToRenderQueue) {
						isoImageBuffer14.addedToRenderQueue = true;
						this.zonesToRender.add(isoImageBuffer14);
					}
				} else {
					isoImageBuffer14.addedToRenderQueue = false;
					if(!isoImageBuffer14.noContent) {
						int i15 = i11 * b4 * 2 + (i10 & 1) * b4;
						int i16 = i10 * b4 - 128 - 16;
						graphics2D1.drawImage(isoImageBuffer14.image, i15, i16, (ImageObserver)null);
					}
				}
			}
		}

		if(this.showHelp) {
			graphics2D1.setTransform(affineTransform2);
			i10 = this.getHeight() - 32 - 4;
			graphics2D1.setColor(new Color(Integer.MIN_VALUE, true));
			graphics2D1.fillRect(4, this.getHeight() - 32 - 4, this.getWidth() - 8, 32);
			graphics2D1.setColor(Color.WHITE);
			String string18 = "F1 - F5: load levels   |   0-9: Set time of day   |   Space: return to spawn   |   Double click: zoom   |   Escape: hide this text";
			graphics2D1.drawString(string18, this.getWidth() / 2 - graphics2D1.getFontMetrics().stringWidth(string18) / 2, i10 + 20);
		}

		graphics2D1.dispose();
	}

	public void mouseDragged(MouseEvent mouseEvent1) {
		int i2 = mouseEvent1.getX() / this.zoom;
		int i3 = mouseEvent1.getY() / this.zoom;
		this.field_1785_i += i2 - this.field_1783_k;
		this.field_1784_j += i3 - this.field_1782_l;
		this.field_1783_k = i2;
		this.field_1782_l = i3;
		this.repaint();
	}

	public void mouseMoved(MouseEvent mouseEvent1) {
	}

	public void mouseClicked(MouseEvent mouseEvent1) {
		if(mouseEvent1.getClickCount() == 2) {
			this.zoom = 3 - this.zoom;
			this.repaint();
		}

	}

	public void mouseEntered(MouseEvent mouseEvent1) {
	}

	public void mouseExited(MouseEvent mouseEvent1) {
	}

	public void mousePressed(MouseEvent mouseEvent1) {
		int i2 = mouseEvent1.getX() / this.zoom;
		int i3 = mouseEvent1.getY() / this.zoom;
		this.field_1783_k = i2;
		this.field_1782_l = i3;
	}

	public void mouseReleased(MouseEvent mouseEvent1) {
	}

	public void keyPressed(KeyEvent keyEvent1) {
		if(keyEvent1.getKeyCode() == 48) {
			this.setBrightness(11);
		}

		if(keyEvent1.getKeyCode() == 49) {
			this.setBrightness(10);
		}

		if(keyEvent1.getKeyCode() == 50) {
			this.setBrightness(9);
		}

		if(keyEvent1.getKeyCode() == 51) {
			this.setBrightness(7);
		}

		if(keyEvent1.getKeyCode() == 52) {
			this.setBrightness(6);
		}

		if(keyEvent1.getKeyCode() == 53) {
			this.setBrightness(5);
		}

		if(keyEvent1.getKeyCode() == 54) {
			this.setBrightness(3);
		}

		if(keyEvent1.getKeyCode() == 55) {
			this.setBrightness(2);
		}

		if(keyEvent1.getKeyCode() == 56) {
			this.setBrightness(1);
		}

		if(keyEvent1.getKeyCode() == 57) {
			this.setBrightness(0);
		}

		if(keyEvent1.getKeyCode() == 112) {
			this.loadLevel("World1");
		}

		if(keyEvent1.getKeyCode() == 113) {
			this.loadLevel("World2");
		}

		if(keyEvent1.getKeyCode() == 114) {
			this.loadLevel("World3");
		}

		if(keyEvent1.getKeyCode() == 115) {
			this.loadLevel("World4");
		}

		if(keyEvent1.getKeyCode() == 116) {
			this.loadLevel("World5");
		}

		if(keyEvent1.getKeyCode() == 32) {
			this.field_1785_i = this.field_1784_j = 0;
		}

		if(keyEvent1.getKeyCode() == 27) {
			this.showHelp = !this.showHelp;
		}

		this.repaint();
	}

	public void keyReleased(KeyEvent keyEvent1) {
	}

	public void keyTyped(KeyEvent keyEvent1) {
	}

	static boolean isRunning(CanvasIsomPreview canvasIsomPreview0) {
		return canvasIsomPreview0.running;
	}
}
