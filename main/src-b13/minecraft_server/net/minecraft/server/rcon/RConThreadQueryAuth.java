package net.minecraft.server.rcon;

import java.net.DatagramPacket;
import java.util.Date;
import java.util.Random;

class RConThreadQueryAuth {
	private long timestamp;
	private int randomChallenge;
	private byte[] requestID;
	private byte[] challengeValue;
	private String requestIDstring;
	final RConThreadQuery queryThread;

	public RConThreadQueryAuth(RConThreadQuery rConThreadQuery1, DatagramPacket datagramPacket2) {
		this.queryThread = rConThreadQuery1;
		this.timestamp = (new Date()).getTime();
		byte[] b3 = datagramPacket2.getData();
		this.requestID = new byte[4];
		this.requestID[0] = b3[3];
		this.requestID[1] = b3[4];
		this.requestID[2] = b3[5];
		this.requestID[3] = b3[6];
		this.requestIDstring = new String(this.requestID);
		this.randomChallenge = (new Random()).nextInt(16777216);
		this.challengeValue = String.format("\t%s%d\u0000", new Object[]{this.requestIDstring, this.randomChallenge}).getBytes();
	}

	public Boolean hasExpired(long j1) {
		return this.timestamp < j1;
	}

	public int getRandomChallenge() {
		return this.randomChallenge;
	}

	public byte[] getChallengeValue() {
		return this.challengeValue;
	}

	public byte[] getRequestID() {
		return this.requestID;
	}
}
