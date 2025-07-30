package net.minecraft.server.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.MinecraftServer;

public class NetworkListenThread {
	public static Logger logger = Logger.getLogger("Minecraft");
	private ServerSocket serverSocket;
	private Thread networkAcceptThread;
	public volatile boolean isListening = false;
	private int s_field_977_f = 0;
	private ArrayList<NetLoginHandler> pendingConnections = new ArrayList<NetLoginHandler>();
	private ArrayList<NetServerHandler> playerList = new ArrayList<NetServerHandler>();
	public MinecraftServer mcServer;
	private HashMap<InetAddress, Long> s_field_35506_i = new HashMap<InetAddress, Long>();

	public NetworkListenThread(MinecraftServer minecraftServer1, InetAddress inetAddress2, int i3) throws IOException {
		this.mcServer = minecraftServer1;
		this.serverSocket = new ServerSocket(i3, 0, inetAddress2);
		this.serverSocket.setPerformancePreferences(0, 2, 1);
		this.isListening = true;
		this.networkAcceptThread = new NetworkAcceptThread(this, "Listen thread", minecraftServer1);
		this.networkAcceptThread.start();
	}

	public void s_func_35505_a(Socket socket1) {
		InetAddress inetAddress2 = socket1.getInetAddress();
		synchronized(this.s_field_35506_i) {
			this.s_field_35506_i.remove(inetAddress2);
		}
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

			netLoginHandler2.netManager.wakeThreads();
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

			netServerHandler6.netManager.wakeThreads();
		}

	}

	static ServerSocket getServerSocket(NetworkListenThread networkListenThread0) {
		return networkListenThread0.serverSocket;
	}

	static HashMap<InetAddress, Long> s_func_35504_b(NetworkListenThread networkListenThread0) {
		return networkListenThread0.s_field_35506_i;
	}

	static int s_func_712_b(NetworkListenThread networkListenThread0) {
		return networkListenThread0.s_field_977_f++;
	}

	static void s_func_716_a(NetworkListenThread networkListenThread0, NetLoginHandler netLoginHandler1) {
		networkListenThread0.addPendingConnection(netLoginHandler1);
	}
}
