package net.minecraft.client;

public class ThreadCheckHasPaid extends Thread {
	final Minecraft mc;

	public ThreadCheckHasPaid(Minecraft mc) {
		this.mc = mc;
	}

	public void run() {
		/*
		try {
			HttpURLConnection httpURLConnection1 = (HttpURLConnection)(new URL("https://login.minecraft.net/session?name=" + this.mc.session.username + "&session=" + this.mc.session.sessionId)).openConnection();
			httpURLConnection1.connect();
			
			if(httpURLConnection1.getResponseCode() == 400 && this == null) {
				//Minecraft.hasPaidCheckTime = System.currentTimeMillis();
			}
			
			httpURLConnection1.disconnect();
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}
		*/
	}
}
