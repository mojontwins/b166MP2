package net.minecraft.network;

import java.io.IOException;

class NetworkWriterThread extends Thread {
	final NetworkManager netManager;

	NetworkWriterThread(NetworkManager networkManager1, String string2) {
		super(string2);
		this.netManager = networkManager1;
	}

	public void run() {
		synchronized(NetworkManager.threadSyncObject) {
			++NetworkManager.numWriteThreads;
		}

		while(true) {
			boolean z13 = false;

			try {
				z13 = true;
				if(!NetworkManager.isRunning(this.netManager)) {
					z13 = false;
					break;
				}

				while(NetworkManager.sendNetworkPacket(this.netManager)) {
				}

				try {
					if(NetworkManager.getOutputStream(this.netManager) != null) {
						NetworkManager.getOutputStream(this.netManager).flush();
					}
				} catch (IOException iOException18) {
					if(!NetworkManager.isTerminating(this.netManager)) {
						NetworkManager.sendError(this.netManager, iOException18);
					}

					iOException18.printStackTrace();
				}

				try {
					sleep(2L);
				} catch (InterruptedException interruptedException16) {
				}
			} finally {
				if(z13) {
					synchronized(NetworkManager.threadSyncObject) {
						--NetworkManager.numWriteThreads;
					}
				}
			}
		}

		synchronized(NetworkManager.threadSyncObject) {
			--NetworkManager.numWriteThreads;
		}
	}
}
