package net.minecraft.src;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

class ThreadLoginVerifier extends Thread {
	final Packet1Login loginPacket;
	final NetLoginHandler loginHandler;

	ThreadLoginVerifier(NetLoginHandler netLoginHandler1, Packet1Login packet1Login2) {
		this.loginHandler = netLoginHandler1;
		this.loginPacket = packet1Login2;
	}

	public void run() {
		try {
			String string1 = NetLoginHandler.getServerId(this.loginHandler);
			URL uRL2 = new URL("http://www.minecraft.net/game/checkserver.jsp?user=" + URLEncoder.encode(this.loginPacket.username, "UTF-8") + "&serverId=" + URLEncoder.encode(string1, "UTF-8"));
			BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(uRL2.openStream()));
			String string4 = bufferedReader3.readLine();
			bufferedReader3.close();
			if(string4.equals("YES")) {
				NetLoginHandler.setLoginPacket(this.loginHandler, this.loginPacket);
			} else {
				this.loginHandler.kickUser("Failed to verify username!");
			}
		} catch (Exception exception5) {
			this.loginHandler.kickUser("Failed to verify username! [internal error " + exception5 + "]");
			exception5.printStackTrace();
		}

	}
}
