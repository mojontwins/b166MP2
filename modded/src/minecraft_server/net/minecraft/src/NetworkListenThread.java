package net.minecraft.src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

public class NetworkListenThread {
	public static Logger logger = Logger.getLogger("Minecraft");
	private ServerSocket serverSocket;
	private Thread networkAcceptThread;
	public volatile boolean field_973_b = false;
	private int field_977_f = 0;
	private ArrayList pendingConnections = new ArrayList();
	private ArrayList playerList = new ArrayList();
	public MinecraftServer mcServer;

	public NetworkListenThread(MinecraftServer minecraftServer1, InetAddress inetAddress2, int i3) throws IOException {
		this.mcServer = minecraftServer1;
		this.serverSocket = new ServerSocket(i3, 0, inetAddress2);
		this.serverSocket.setPerformancePreferences(0, 2, 1);
		this.field_973_b = true;
		this.networkAcceptThread = new NetworkAcceptThread(this, "Listen thread", minecraftServer1);
		this.networkAcceptThread.start();
	}

	public void addPlayer(NetServerHandler netServerHandler1) {
		this.playerList.add(netServerHandler1);
	}

	private void addPendingConnection(NetLoginHandler netLoginHandler1) {
		if(netLoginHandler1 == null) {
			throw new IllegalArgumentException("Got null pendingconnection!");
		} else {
			this.pendingConnections.add(netLoginHandler1);
		}
	}

	public void handleNetworkListenThread() {
		int i1;
		for(i1 = 0; i1 < this.pendingConnections.size(); ++i1) {
			NetLoginHandler netLoginHandler2 = (NetLoginHandler)this.pendingConnections.get(i1);

			try {
				netLoginHandler2.tryLogin();
			} catch (Exception exception5) {
				netLoginHandler2.kickUser("Internal server error");
				logger.log(Level.WARNING, "Failed to handle packet: " + exception5, exception5);
			}

			if(netLoginHandler2.finishedProcessing) {
				this.pendingConnections.remove(i1--);
			}

			netLoginHandler2.netManager.func_28138_a();
		}

		for(i1 = 0; i1 < this.playerList.size(); ++i1) {
			NetServerHandler netServerHandler6 = (NetServerHandler)this.playerList.get(i1);

			try {
				netServerHandler6.handlePackets();
			} catch (Exception exception4) {
				logger.log(Level.WARNING, "Failed to handle packet: " + exception4, exception4);
				netServerHandler6.kickPlayer("Internal server error");
			}

			if(netServerHandler6.connectionClosed) {
				this.playerList.remove(i1--);
			}

			netServerHandler6.netManager.func_28138_a();
		}

	}

	static ServerSocket func_713_a(NetworkListenThread networkListenThread0) {
		return networkListenThread0.serverSocket;
	}

	static int func_712_b(NetworkListenThread networkListenThread0) {
		return networkListenThread0.field_977_f++;
	}

	static void func_716_a(NetworkListenThread networkListenThread0, NetLoginHandler netLoginHandler1) {
		networkListenThread0.addPendingConnection(netLoginHandler1);
	}
}
