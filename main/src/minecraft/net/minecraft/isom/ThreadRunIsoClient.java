package net.minecraft.isom;

class ThreadRunIsoClient extends Thread {
	final CanvasIsomPreview field_1197_a;

	ThreadRunIsoClient(CanvasIsomPreview canvasIsomPreview1) {
		this.field_1197_a = canvasIsomPreview1;
	}

	public void run() {
		while(CanvasIsomPreview.isRunning(this.field_1197_a)) {
			this.field_1197_a.render();

			try {
				Thread.sleep(1L);
			} catch (Exception exception2) {
			}
		}

	}
}
