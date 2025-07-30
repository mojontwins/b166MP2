package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public final class ThreadServerApplication extends Thread {
	final MinecraftServer mcServer;

	public ThreadServerApplication(String string1, MinecraftServer minecraftServer2) {
		super(string1);
		this.mcServer = minecraftServer2;
	}

	public void run() {
		this.mcServer.run();
	}
}
