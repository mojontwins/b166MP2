package net.minecraft.server.rcon;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import net.minecraft.server.IServer;

public class RConThreadQuery extends RConThreadBase {
	private long lastAuthCheckTime;
	private int queryPort;
	private int serverPort;
	private int maxPlayers;
	private String serverMotd;
	private String worldName;
	private DatagramSocket querySocket = null;
	private byte[] buffer = new byte[1460];
	private DatagramPacket incomingPacket = null;
	private String queryHostname;
	private String serverHostname;
	private HashMap<SocketAddress, RConThreadQueryAuth> queryClients;
	private RConOutputStream output;
	private long lastQueryResponseTime;

	public RConThreadQuery(IServer iServer1) {
		super(iServer1);
		this.queryPort = iServer1.getIntProperty("query.port", 0);
		this.serverHostname = iServer1.getHostname();
		this.serverPort = iServer1.getPort();
		this.serverMotd = iServer1.getMotd();
		this.maxPlayers = iServer1.getMaxPlayers();
		this.worldName = iServer1.getWorldName();
		this.lastQueryResponseTime = 0L;
		this.queryHostname = "0.0.0.0";
		if(0 != this.serverHostname.length() && !this.queryHostname.equals(this.serverHostname)) {
			this.queryHostname = this.serverHostname;
		} else {
			this.serverHostname = "0.0.0.0";

			try {
				InetAddress inetAddress2 = InetAddress.getLocalHost();
				this.queryHostname = inetAddress2.getHostAddress();
			} catch (UnknownHostException unknownHostException3) {
				this.logWarning("Unable to determine local host IP, please set server-ip in \'" + iServer1.getSettingsFilename() + "\' : " + unknownHostException3.getMessage());
			}
		}

		if(0 == this.queryPort) {
			this.queryPort = this.serverPort;
			this.log("Setting default query port to " + this.queryPort);
			iServer1.setProperty("query.port", this.queryPort);
			iServer1.setProperty("debug", false);
			iServer1.saveProperties();
		}

		new HashMap<Object, Object>();
		this.output = new RConOutputStream(1460);
		this.queryClients = new HashMap<SocketAddress, RConThreadQueryAuth>();
		(new Date()).getTime();
	}

	private void sendResponsePacket(byte[] b1, DatagramPacket datagramPacket2) throws SocketException, IOException {
		this.querySocket.send(new DatagramPacket(b1, b1.length, datagramPacket2.getSocketAddress()));
	}

	private boolean parseIncomingPacket(DatagramPacket datagramPacket1) throws IOException {
		byte[] b2 = datagramPacket1.getData();
		int i3 = datagramPacket1.getLength();
		SocketAddress socketAddress4 = datagramPacket1.getSocketAddress();
		this.logInfo("Packet len " + i3 + " [" + socketAddress4 + "]");
		if(3 <= i3 && -2 == b2[0] && -3 == b2[1]) {
			this.logInfo("Packet \'" + RConUtils.getByteAsHexString(b2[2]) + "\' [" + socketAddress4 + "]");
			switch(b2[2]) {
			case 0:
				if(!this.verifyClientAuth(datagramPacket1).booleanValue()) {
					this.logInfo("Invalid challenge [" + socketAddress4 + "]");
					return false;
				} else if(15 != i3) {
					RConOutputStream rConOutputStream5 = new RConOutputStream(1460);
					rConOutputStream5.writeInt(0);
					rConOutputStream5.writeByteArray(this.getRequestID(datagramPacket1.getSocketAddress()));
					rConOutputStream5.writeString(this.serverMotd);
					rConOutputStream5.writeString("SMP");
					rConOutputStream5.writeString(this.worldName);
					rConOutputStream5.writeString(Integer.toString(this.getNumberOfPlayers()));
					rConOutputStream5.writeString(Integer.toString(this.maxPlayers));
					rConOutputStream5.writeShort((short)this.serverPort);
					rConOutputStream5.writeString(this.queryHostname);
					this.sendResponsePacket(rConOutputStream5.toByteArray(), datagramPacket1);
					this.logInfo("Status [" + socketAddress4 + "]");
				} else {
					this.sendResponsePacket(this.createQueryResponse(datagramPacket1), datagramPacket1);
					this.logInfo("Rules [" + socketAddress4 + "]");
				}
			default:
				return true;
			case 9:
				this.sendAuthChallenge(datagramPacket1);
				this.logInfo("Challenge [" + socketAddress4 + "]");
				return true;
			}
		} else {
			this.logInfo("Invalid packet [" + socketAddress4 + "]");
			return false;
		}
	}

	private byte[] createQueryResponse(DatagramPacket datagramPacket1) throws IOException {
		long j2 = System.currentTimeMillis();
		if(j2 < this.lastQueryResponseTime + 5000L) {
			byte[] b7 = this.output.toByteArray();
			byte[] b8 = this.getRequestID(datagramPacket1.getSocketAddress());
			b7[1] = b8[0];
			b7[2] = b8[1];
			b7[3] = b8[2];
			b7[4] = b8[3];
			return b7;
		} else {
			this.lastQueryResponseTime = j2;
			this.output.reset();
			this.output.writeInt(0);
			this.output.writeByteArray(this.getRequestID(datagramPacket1.getSocketAddress()));
			this.output.writeString("splitnum");
			this.output.writeInt(128);
			this.output.writeInt(0);
			this.output.writeString("hostname");
			this.output.writeString(this.serverMotd);
			this.output.writeString("gametype");
			this.output.writeString("SMP");
			this.output.writeString("game_id");
			this.output.writeString("MINECRAFT");
			this.output.writeString("version");
			this.output.writeString(this.server.getVersionString());
			this.output.writeString("plugins");
			this.output.writeString(this.server.getPlugin());
			this.output.writeString("map");
			this.output.writeString(this.worldName);
			this.output.writeString("numplayers");
			this.output.writeString("" + this.getNumberOfPlayers());
			this.output.writeString("maxplayers");
			this.output.writeString("" + this.maxPlayers);
			this.output.writeString("hostport");
			this.output.writeString("" + this.serverPort);
			this.output.writeString("hostip");
			this.output.writeString(this.queryHostname);
			this.output.writeInt(0);
			this.output.writeInt(1);
			this.output.writeString("player_");
			this.output.writeInt(0);
			String[] string4 = this.server.getPlayerNamesAsList();
			byte b5 = (byte)string4.length;

			for(byte b6 = (byte)(b5 - 1); b6 >= 0; --b6) {
				this.output.writeString(string4[b6]);
			}

			this.output.writeInt(0);
			return this.output.toByteArray();
		}
	}

	private byte[] getRequestID(SocketAddress socketAddress1) {
		return ((RConThreadQueryAuth)this.queryClients.get(socketAddress1)).getRequestID();
	}

	private Boolean verifyClientAuth(DatagramPacket datagramPacket1) {
		SocketAddress socketAddress2 = datagramPacket1.getSocketAddress();
		if(!this.queryClients.containsKey(socketAddress2)) {
			return false;
		} else {
			byte[] b3 = datagramPacket1.getData();
			return ((RConThreadQueryAuth)this.queryClients.get(socketAddress2)).getRandomChallenge() != RConUtils.getBytesAsBEint(b3, 7, datagramPacket1.getLength()) ? false : true;
		}
	}

	private void sendAuthChallenge(DatagramPacket datagramPacket1) throws SocketException, IOException {
		RConThreadQueryAuth rConThreadQueryAuth2 = new RConThreadQueryAuth(this, datagramPacket1);
		this.queryClients.put(datagramPacket1.getSocketAddress(), rConThreadQueryAuth2);
		this.sendResponsePacket(rConThreadQueryAuth2.getChallengeValue(), datagramPacket1);
	}

	private void cleanQueryClientsMap() {
		if(this.running) {
			long j1 = System.currentTimeMillis();
			if(j1 >= this.lastAuthCheckTime + 30000L) {
				this.lastAuthCheckTime = j1;
				Iterator<?> iterator3 = this.queryClients.entrySet().iterator();

				while(iterator3.hasNext()) {
					Entry<?, ?> map$Entry4 = (Entry<?, ?>)iterator3.next();
					if(((RConThreadQueryAuth)map$Entry4.getValue()).hasExpired(j1).booleanValue()) {
						iterator3.remove();
					}
				}

			}
		}
	}

	public void run() {
		this.log("Query running on " + this.serverHostname + ":" + this.queryPort);
		this.lastAuthCheckTime = System.currentTimeMillis();
		this.incomingPacket = new DatagramPacket(this.buffer, this.buffer.length);

		try {
			while(this.running) {
				try {
					this.querySocket.receive(this.incomingPacket);
					this.cleanQueryClientsMap();
					this.parseIncomingPacket(this.incomingPacket);
				} catch (SocketTimeoutException socketTimeoutException7) {
					this.cleanQueryClientsMap();
				} catch (PortUnreachableException portUnreachableException8) {
				} catch (IOException iOException9) {
					this.stopWithException(iOException9);
				}
			}
		} finally {
			this.closeAllSockets();
		}

	}

	public void startThread() {
		if(!this.running) {
			if(0 < this.queryPort && 65535 >= this.queryPort) {
				if(this.initQuerySystem()) {
					super.startThread();
				}

			} else {
				this.logWarning("Invalid query port " + this.queryPort + " found in \'" + this.server.getSettingsFilename() + "\' (queries disabled)");
			}
		}
	}

	private void stopWithException(Exception exception1) {
		if(this.running) {
			this.logWarning("Unexpected exception, buggy JRE? (" + exception1.toString() + ")");
			if(!this.initQuerySystem()) {
				this.logSevere("Failed to recover from buggy JRE, shutting down!");
				this.running = false;
				this.server.s_func_40010_o();
			}

		}
	}

	private boolean initQuerySystem() {
		try {
			this.querySocket = new DatagramSocket(this.queryPort, InetAddress.getByName(this.serverHostname));
			this.registerSocket(this.querySocket);
			this.querySocket.setSoTimeout(500);
			return true;
		} catch (SocketException socketException2) {
			this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Socket): " + socketException2.getMessage());
		} catch (UnknownHostException unknownHostException3) {
			this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (Unknown Host): " + unknownHostException3.getMessage());
		} catch (Exception exception4) {
			this.logWarning("Unable to initialise query system on " + this.serverHostname + ":" + this.queryPort + " (E): " + exception4.getMessage());
		}

		return false;
	}
}
