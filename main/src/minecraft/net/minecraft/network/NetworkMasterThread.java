package net.minecraft.network;

class NetworkMasterThread extends Thread {
	final NetworkManager netManager;

	NetworkMasterThread(NetworkManager networkManager1) {
		this.netManager = networkManager1;
	}

	@SuppressWarnings("deprecation")
	public void run() {
		try {
			Thread.sleep(5000L);
			if(NetworkManager.getReadThread(this.netManager).isAlive()) {
				try {
					NetworkManager.getReadThread(this.netManager).stop();
				} catch (Throwable throwable3) {
				}
			}

			if(NetworkManager.getWriteThread(this.netManager).isAlive()) {
				try {
					NetworkManager.getWriteThread(this.netManager).stop();
				} catch (Throwable throwable2) {
				}
			}
		} catch (InterruptedException interruptedException4) {
			interruptedException4.printStackTrace();
		}

	}
}
