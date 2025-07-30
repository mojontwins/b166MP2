package net.minecraft.server.rcon;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.server.IServer;

public class RConThreadMain extends RConThreadBase {
	private int rconPort;
	private int serverPort;
	private String hostname;
	private ServerSocket serverSocket = null;
	private String rconPassword;
	private HashMap<SocketAddress, RConThreadClient> clientThreads;

	public RConThreadMain(IServer iServer1) {
		super(iServer1);
		this.rconPort = iServer1.getIntProperty("rcon.port", 0);
		this.rconPassword = iServer1.getStringProperty("rcon.password", "");
		this.hostname = iServer1.getHostname();
		this.serverPort = iServer1.getPort();
		if(0 == this.rconPort) {
			this.rconPort = this.serverPort + 10;
			this.log("Setting default rcon port to " + this.rconPort);
			iServer1.setProperty("rcon.port", this.rconPort);
			if(0 == this.rconPassword.length()) {
				iServer1.setProperty("rcon.password", "");
			}

			iServer1.saveProperties();
		}

		if(0 == this.hostname.length()) {
			this.hostname = "0.0.0.0";
		}

		this.initClientTh();
		this.serverSocket = null;
	}

	private void initClientTh() {
		this.clientThreads = new HashMap<SocketAddress, RConThreadClient>();
	}

	private void cleanClientThreadsMap() {
		Iterator<?> iterator1 = this.clientThreads.entrySet().iterator();

		while(iterator1.hasNext()) {
			Entry<?, ?> map$Entry2 = (Entry<?, ?>)iterator1.next();
			if(!((RConThreadClient)map$Entry2.getValue()).isRunning()) {
				iterator1.remove();
			}
		}

	}

	public void run() {
		this.log("RCON running on " + this.hostname + ":" + this.rconPort);

		try {
			while(this.running) {
				try {
					Socket socket1 = this.serverSocket.accept();
					socket1.setSoTimeout(500);
					RConThreadClient rConThreadClient2 = new RConThreadClient(this.server, socket1);
					rConThreadClient2.startThread();
					this.clientThreads.put(socket1.getRemoteSocketAddress(), rConThreadClient2);
					this.cleanClientThreadsMap();
				} catch (SocketTimeoutException socketTimeoutException7) {
					this.cleanClientThreadsMap();
				} catch (IOException iOException8) {
					if(this.running) {
						this.log("IO: " + iOException8.getMessage());
					}
				}
			}
		} finally {
			this.closeServerSocket(this.serverSocket);
		}

	}

	public void startThread() {
		if(0 == this.rconPassword.length()) {
			this.logWarning("No rcon password set in \'" + this.server.getSettingsFilename() + "\', rcon disabled!");
		} else if(0 < this.rconPort && 65535 >= this.rconPort) {
			if(!this.running) {
				try {
					this.serverSocket = new ServerSocket(this.rconPort, 0, InetAddress.getByName(this.hostname));
					this.serverSocket.setSoTimeout(500);
					super.startThread();
				} catch (IOException iOException2) {
					this.logWarning("Unable to initialise rcon on " + this.hostname + ":" + this.rconPort + " : " + iOException2.getMessage());
				}

			}
		} else {
			this.logWarning("Invalid rcon port " + this.rconPort + " found in \'" + this.server.getSettingsFilename() + "\', rcon disabled!");
		}
	}
}
