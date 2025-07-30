package net.minecraft.client;

import java.awt.Canvas;
import java.awt.Dimension;

class CanvasCrashReport extends Canvas {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1045303830132238555L;

	public CanvasCrashReport(int i1) {
		this.setPreferredSize(new Dimension(i1, i1));
		this.setMinimumSize(new Dimension(i1, i1));
	}
}
