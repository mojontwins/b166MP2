package net.minecraft.server.gui;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.chunk.storage.IProgressUpdate;

public class ConvertProgressUpdater implements IProgressUpdate {
	private long lastTimeMillis;
	final MinecraftServer mcServer;

	public ConvertProgressUpdater(MinecraftServer minecraftServer1) {
		this.mcServer = minecraftServer1;
		this.lastTimeMillis = System.currentTimeMillis();
	}

	public void displaySavingString(String string1) {
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
