package net.minecraft.client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public final class GameWindowListener extends WindowAdapter {
	final Minecraft mc;
	final Thread mcThread;

	public GameWindowListener(Minecraft mc, Thread thread2) {
		this.mc = mc;
		this.mcThread = thread2;
	}

	public void windowClosing(WindowEvent windowEvent1) {
		this.mc.shutdown();

		try {
			this.mcThread.join();
		} catch (InterruptedException interruptedException3) {
			interruptedException3.printStackTrace();
		}

		System.exit(0);
	}
}
