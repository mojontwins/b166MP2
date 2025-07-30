package net.minecraft.world.level.chunk.storage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadedFileIOBase implements Runnable {
	public static final ThreadedFileIOBase threadedIOInstance = new ThreadedFileIOBase();
	private List<IThreadedFileIO> threadedIOQueue = Collections.synchronizedList(new ArrayList<IThreadedFileIO>());
	private volatile long writeQueuedCounter = 0L;
	private volatile long savedIOCounter = 0L;
	private volatile boolean isThreadWaiting = false;

	private ThreadedFileIOBase() {
		Thread thread1 = new Thread(this, "File IO Thread");
		thread1.setPriority(1);
		thread1.start();
	}

	public void run() {
		while(true) {
			this.processQueue();
		}
	}

	private void processQueue() {
		for(int i1 = 0; i1 < this.threadedIOQueue.size(); ++i1) {
			IThreadedFileIO iThreadedFileIO2 = (IThreadedFileIO)this.threadedIOQueue.get(i1);
			boolean z3 = iThreadedFileIO2.writeNextIO();
			if(!z3) {
				this.threadedIOQueue.remove(i1--);
				++this.savedIOCounter;
			}

			try {
				if(!this.isThreadWaiting) {
					Thread.sleep(10L);
				} else {
					Thread.sleep(0L);
				}
			} catch (InterruptedException interruptedException6) {
				interruptedException6.printStackTrace();
			}
		}

		if(this.threadedIOQueue.isEmpty()) {
			try {
				Thread.sleep(25L);
			} catch (InterruptedException interruptedException5) {
				interruptedException5.printStackTrace();
			}
		}

	}

	public void queueIO(IThreadedFileIO iThreadedFileIO1) {
		if(!this.threadedIOQueue.contains(iThreadedFileIO1)) {
			++this.writeQueuedCounter;
			this.threadedIOQueue.add(iThreadedFileIO1);
		}
	}

	public void waitForFinish() throws InterruptedException {
		this.isThreadWaiting = true;

		while(this.writeQueuedCounter != this.savedIOCounter) {
			Thread.sleep(10L);
		}

		this.isThreadWaiting = false;
	}
}
