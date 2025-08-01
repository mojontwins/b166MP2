package net.minecraft.client;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Canvas;

public class MinecraftApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = -133764370282063251L;
	private Canvas mcCanvas;
	private Minecraft mc;
	private Thread mcThread = null;

	public void init() {
		this.mcCanvas = new CanvasMinecraftApplet(this);
		boolean z1 = false;
		if(this.getParameter("fullscreen") != null) {
			z1 = this.getParameter("fullscreen").equalsIgnoreCase("true");
		}

		this.mc = new MinecraftAppletImpl(this, this, this.mcCanvas, this, this.getWidth(), this.getHeight(), z1);
		this.mc.minecraftUri = this.getDocumentBase().getHost();
		if(this.getDocumentBase().getPort() > 0) {
			this.mc.minecraftUri = this.mc.minecraftUri + ":" + this.getDocumentBase().getPort();
		}

		if(this.getParameter("username") != null && this.getParameter("sessionid") != null) {
			this.mc.session = new User(this.getParameter("username"), this.getParameter("sessionid"));
			System.out.println("Setting user: " + this.mc.session.username + ", " + this.mc.session.sessionId);
			if(this.getParameter("mppass") != null) {
				this.mc.session.mpPassParameter = this.getParameter("mppass");
			}
		} else {
			this.mc.session = new User("Player", "");
		}

		if(this.getParameter("server") != null && this.getParameter("port") != null) {
			this.mc.setServer(this.getParameter("server"), Integer.parseInt(this.getParameter("port")));
		}

		this.mc.hideQuitButton = true;
		if("true".equals(this.getParameter("stand-alone"))) {
			this.mc.hideQuitButton = false;
		}

		this.setLayout(new BorderLayout());
		this.add(this.mcCanvas, "Center");
		this.mcCanvas.setFocusable(true);
		this.validate();
	}

	public void startMainThread() {
		if(this.mcThread == null) {
			this.mcThread = new Thread(this.mc, "Minecraft main thread");
			this.mcThread.start();
		}
	}

	public void start() {
		if(this.mc != null) {
			this.mc.isGamePaused = false;
		}

	}

	public void stop() {
		if(this.mc != null) {
			this.mc.isGamePaused = true;
		}

	}

	public void destroy() {
		this.shutdown();
	}

	public void shutdown() {
		if(this.mcThread != null) {
			this.mc.shutdown();

			try {
				this.mcThread.join(10000L);
			} catch (InterruptedException interruptedException4) {
				try {
					this.mc.shutdownMinecraftApplet();
				} catch (Exception exception3) {
					exception3.printStackTrace();
				}
			}

			this.mcThread = null;
		}
	}

	public void clearApplet() {
		this.mcCanvas = null;
		this.mc = null;
		this.mcThread = null;

		try {
			this.removeAll();
			this.validate();
		} catch (Exception exception2) {
		}

	}
}
