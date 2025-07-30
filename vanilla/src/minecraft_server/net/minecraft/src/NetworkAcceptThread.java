package net.minecraft.src;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;

import net.minecraft.server.MinecraftServer;

class NetworkAcceptThread extends Thread {
	final MinecraftServer mcServer;
	final NetworkListenThread field_985_b;

	NetworkAcceptThread(NetworkListenThread networkListenThread1, String string2, MinecraftServer minecraftServer3) {
		super(string2);
		this.field_985_b = networkListenThread1;
		this.mcServer = minecraftServer3;
	}

	public void run() {
		HashMap hashMap1 = new HashMap();

		while(this.field_985_b.field_973_b) {
			try {
				Socket socket2 = NetworkListenThread.func_713_a(this.field_985_b).accept();
				if(socket2 != null) {
					InetAddress inetAddress3 = socket2.getInetAddress();
					if(hashMap1.containsKey(inetAddress3) && !"127.0.0.1".equals(inetAddress3.getHostAddress()) && System.currentTimeMillis() - ((Long)hashMap1.get(inetAddress3)).longValue() < 5000L) {
						hashMap1.put(inetAddress3, System.currentTimeMillis());
						socket2.close();
					} else {
						hashMap1.put(inetAddress3, System.currentTimeMillis());
						NetLoginHandler netLoginHandler4 = new NetLoginHandler(this.mcServer, socket2, "Connection #" + NetworkListenThread.func_712_b(this.field_985_b));
						NetworkListenThread.func_716_a(this.field_985_b, netLoginHandler4);
					}
				}
			} catch (IOException iOException5) {
				iOException5.printStackTrace();
			}
		}

	}
}
