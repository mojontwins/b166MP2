package net.minecraft.client;

public class ThreadClientSleep extends Thread {
	final Minecraft mc;

	public ThreadClientSleep(Minecraft mc, String string2) {
		super(string2);
		this.mc = mc;
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		while(this.mc.running) {
			try {
				Thread.sleep(2147483647L);
			} catch (InterruptedException interruptedException2) {
			}
		}

	}
}
