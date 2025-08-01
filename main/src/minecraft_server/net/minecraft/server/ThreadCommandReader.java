package net.minecraft.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mojontwins.minecraft.commands.ComplexCommand;

public class ThreadCommandReader extends Thread {
	final MinecraftServer mcServer;

	public ThreadCommandReader(MinecraftServer minecraftServer1) {
		this.mcServer = minecraftServer1;
	}

	public void run() {
		BufferedReader bufferedReader1 = new BufferedReader(new InputStreamReader(System.in));
		String string2 = null;

		try {
			while(!this.mcServer.serverStopped && MinecraftServer.isServerRunning(this.mcServer) && (string2 = bufferedReader1.readLine()) != null) {
				this.mcServer.addCommand(new ComplexCommand(string2, null), this.mcServer);
			}
		} catch (IOException iOException4) {
			iOException4.printStackTrace();
		}

	}
}
