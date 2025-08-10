package de.matthiasmann.twl;

public class FPSCounter extends Label {
	private long startTime;
	private int frames;
	private int framesToCount;
	private final char[] fmtBuffer;
	private final int decimalPoint;
	private final long scale;

	public FPSCounter(int numIntegerDigits, int numDecimalDigits) {
		this.framesToCount = 100;
		if(numIntegerDigits < 2) {
			throw new IllegalArgumentException("numIntegerDigits must be >= 2");
		} else if(numDecimalDigits < 0) {
			throw new IllegalArgumentException("numDecimalDigits must be >= 0");
		} else {
			this.decimalPoint = numDecimalDigits;
			this.startTime = System.nanoTime();
			this.fmtBuffer = new char[numIntegerDigits + numDecimalDigits + Integer.signum(numDecimalDigits)];
			long tmp = 1000000000L;

			for(int i = 0; i < this.decimalPoint; ++i) {
				tmp *= 10L;
			}

			this.scale = tmp;
			this.updateText(0);
		}
	}

	public FPSCounter() {
		this(3, 2);
	}

	public int getFramesToCount() {
		return this.framesToCount;
	}

	public void setFramesToCount(int framesToCount) {
		if(framesToCount < 1) {
			throw new IllegalArgumentException("framesToCount < 1");
		} else {
			this.framesToCount = framesToCount;
		}
	}

	protected void paintWidget(GUI gui) {
		if(++this.frames >= this.framesToCount) {
			this.updateFPS();
		}

		super.paintWidget(gui);
	}

	private static void format(char[] buf, int value, int decimalPoint) {
		int pos = buf.length;

		while(pos > 0) {
			--pos;
			buf[pos] = (char)(48 + value % 10);
			value /= 10;
			--decimalPoint;
			if(decimalPoint == 0) {
				--pos;
				buf[pos] = 46;
			}
		}

		if(value > 0) {
			for(int i = 0; i < buf.length; ++i) {
				if(buf[i] != 46) {
					buf[i] = 57;
				}
			}
		}

	}

	private void updateFPS() {
		long curTime = System.nanoTime();
		long elapsed = curTime - this.startTime;
		this.startTime = curTime;
		this.updateText((int)(((long)this.frames * this.scale + elapsed / 2L) / elapsed));
		this.frames = 0;
	}

	private void updateText(int scaledValue) {
		format(this.fmtBuffer, scaledValue, this.decimalPoint);
		this.setText(new String(this.fmtBuffer));
	}
}
