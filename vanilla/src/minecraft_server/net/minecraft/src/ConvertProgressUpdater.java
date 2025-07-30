package net.minecraft.src;

import net.minecraft.server.MinecraftServer;

public class ConvertProgressUpdater implements IProgressUpdate {
	private long lastTimeMillis;
	final MinecraftServer mcServer;

	public ConvertProgressUpdater(MinecraftServer minecraftServer1) {
		this.mcServer = minecraftServer1;
		this.lastTimeMillis = System.currentTimeMillis();
	}

	public void func_438_a(String string1) {
	}

	public void setLoadingProgress(int i1) {
		if(System.currentTimeMillis() - this.lastTimeMillis >= 1000L) {
			this.lastTimeMillis = System.currentTimeMillis();
			MinecraftServer.logger.info("Converting... " + i1 + "%");
		}

	}

	public void displayLoadingString(String string1) {
	}
}
