package net.minecraft.server.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import net.minecraft.server.MinecraftServer;

class NetworkAcceptThread extends Thread {
	final MinecraftServer mcServer;
	final NetworkListenThread netWorkListener;

	NetworkAcceptThread(NetworkListenThread networkListenThread1, String string2, MinecraftServer minecraftServer3) {
		super(string2);
		this.netWorkListener = networkListenThread1;
		this.mcServer = minecraftServer3;
	}

	public void run() {
		while(this.netWorkListener.isListening) {
			try {
				Socket socket1 = NetworkListenThread.getServerSocket(this.netWorkListener).accept();
				if(socket1 != null) {
					synchronized(NetworkListenThread.s_func_35504_b(this.netWorkListener)) {
						InetAddress inetAddress3 = socket1.getInetAddress();
						if(NetworkListenThread.s_func_35504_b(this.netWorkListener).containsKey(inetAddress3) && !"127.0.0.1".equals(inetAddress3.getHostAddress()) && System.currentTimeMillis() - ((Long)NetworkListenThread.s_func_35504_b(this.netWorkListener).get(inetAddress3)).longValue() < 4000L) {
							NetworkListenThread.s_func_35504_b(this.netWorkListener).put(inetAddress3, System.currentTimeMillis());
							socket1.close();
							continue;
						}

						NetworkListenThread.s_func_35504_b(this.netWorkListener).put(inetAddress3, System.currentTimeMillis());
					}

					NetLoginHandler netLoginHandler2 = new NetLoginHandler(this.mcServer, socket1, "Connection #" + NetworkListenThread.s_func_712_b(this.netWorkListener));
					NetworkListenThread.s_func_716_a(this.netWorkListener, netLoginHandler2);
				}
			} catch (IOException iOException6) {
				iOException6.printStackTrace();
			}
		}

	}
}
