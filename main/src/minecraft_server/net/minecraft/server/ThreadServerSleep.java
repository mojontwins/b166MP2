package net.minecraft.server;

public class ThreadServerSleep extends Thread {
	final MinecraftServer mc;

	public ThreadServerSleep(MinecraftServer minecraftServer1) {
		this.mc = minecraftServer1;
		this.setDaemon(true);
		this.start();
	}

	public void run() {
		while(true) {
			try {
				Thread.sleep(2147483647L);
			} catch (InterruptedException interruptedException2) {
			}
		}
	}
}
