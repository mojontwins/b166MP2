package net.minecraft.isom;

import java.applet.Applet;
import java.awt.BorderLayout;

public class IsomPreviewApplet extends Applet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6124856963361992435L;
	private CanvasIsomPreview isomPreview = new CanvasIsomPreview();

	public IsomPreviewApplet() {
		this.setLayout(new BorderLayout());
		this.add(this.isomPreview, "Center");
	}

	public void start() {
		this.isomPreview.start();
	}

	public void stop() {
		this.isomPreview.stop();
	}
}
