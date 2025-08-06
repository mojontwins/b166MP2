package net.minecraft.server.rcon;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.server.IServer;

public abstract class RConThreadBase implements Runnable {
	protected boolean running = false;
	protected IServer server;
	protected Thread rconThread;
	protected int s_field_40415_d = 5;
	protected List<DatagramSocket> socketList = new ArrayList<DatagramSocket>();
	protected List<ServerSocket> serverSocketList = new ArrayList<ServerSocket>();

	RConThreadBase(IServer iServer1) {
		this.server = iServer1;
		if(this.server.isDebuggingEnabled()) {
			this.logWarning("Debugging is enabled, performance maybe reduced!");
		}

	}

	public synchronized void startThread() {
		this.rconThread = new Thread(this);
		this.rconThread.start();
		this.running = true;
	}

	public boolean isRunning() {
		return this.running;
	}

	protected void logInfo(String string1) {
		this.server.logIn(string1);
	}

	protected void log(String string1) {
		this.server.log(string1);
	}

	protected void logWarning(String string1) {
		this.server.logWarning(string1);
	}

	protected void logSevere(String string1) {
		this.server.logSevere(string1);
	}

	protected int getNumberOfPlayers() {
		return this.server.playersOnline();
	}

	protected void registerSocket(DatagramSocket datagramSocket1) {
		this.logInfo("registerSocket: " + datagramSocket1);
		this.socketList.add(datagramSocket1);
	}

	protected boolean closeSocket(DatagramSocket datagramSocket1, boolean z2) {
		this.logInfo("closeSocket: " + datagramSocket1);
		if(null == datagramSocket1) {
			return false;
		} else {
			boolean z3 = false;
			if(!datagramSocket1.isClosed()) {
				datagramSocket1.close();
				z3 = true;
			}

			if(z2) {
				this.socketList.remove(datagramSocket1);
			}

			return z3;
		}
	}

	protected boolean closeServerSocket(ServerSocket serverSocket1) {
		return this.closeServerSocket_do(serverSocket1, true);
	}

	protected boolean closeServerSocket_do(ServerSocket serverSocket1, boolean z2) {
		this.logInfo("closeSocket: " + serverSocket1);
		if(null == serverSocket1) {
			return false;
		} else {
			boolean z3 = false;

			try {
				if(!serverSocket1.isClosed()) {
					serverSocket1.close();
					z3 = true;
				}
			} catch (IOException iOException5) {
				this.logWarning("IO: " + iOException5.getMessage());
			}

			if(z2) {
				this.serverSocketList.remove(serverSocket1);
			}

			return z3;
		}
	}

	protected void closeAllSockets() {
		this.clos(false);
	}

	protected void clos(boolean z1) {
		int i2 = 0;
		Iterator<DatagramSocket> iterator3 = this.socketList.iterator();

		while(iterator3.hasNext()) {
			DatagramSocket datagramSocket4 = (DatagramSocket)iterator3.next();
			if(this.closeSocket(datagramSocket4, false)) {
				++i2;
			}
		}

		this.socketList.clear();
		
		Iterator<ServerSocket> iterator3b = this.serverSocketList.iterator();

		while(iterator3b.hasNext()) {
			ServerSocket serverSocket5 = (ServerSocket)iterator3b.next();
			if(this.closeServerSocket_do(serverSocket5, false)) {
				++i2;
			}
		}

		this.serverSocketList.clear();
		if(z1 && 0 < i2) {
			this.logWarning("Force closed " + i2 + " sockets");
		}

	}
}
