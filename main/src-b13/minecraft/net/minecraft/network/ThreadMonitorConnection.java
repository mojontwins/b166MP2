package net.minecraft.network;

class ThreadMonitorConnection extends Thread {
	final NetworkManager netManager;

	ThreadMonitorConnection(NetworkManager networkManager1) {
		this.netManager = networkManager1;
	}

	public void run() {
		try {
			Thread.sleep(2000L);
			if(NetworkManager.isRunning(this.netManager)) {
				NetworkManager.getWriteThread(this.netManager).interrupt();
				this.netManager.networkShutdown("disconnect.closed", new Object[0]);
			}
		} catch (Exception exception2) {
			exception2.printStackTrace();
		}

	}
}
