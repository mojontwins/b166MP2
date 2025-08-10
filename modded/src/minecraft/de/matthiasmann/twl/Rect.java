package de.matthiasmann.twl;

public class Rect {
	private int x0;
	private int y0;
	private int x1;
	private int y1;

	public Rect() {
	}

	public Rect(int x, int y, int w, int h) {
		this.setXYWH(x, y, w, h);
	}

	public Rect(Rect src) {
		this.set(src.getX(), src.getY(), src.getRight(), src.getBottom());
	}

	public void setXYWH(int x, int y, int w, int h) {
		this.x0 = x;
		this.y0 = y;
		this.x1 = x + Math.max(0, w);
		this.y1 = y + Math.max(0, h);
	}

	public void set(int x0, int y0, int x1, int y1) {
		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;
	}

	public void intersect(Rect other) {
		this.x0 = Math.max(this.x0, other.x0);
		this.y0 = Math.max(this.y0, other.y0);
		this.x1 = Math.min(this.x1, other.x1);
		this.y1 = Math.min(this.y1, other.y1);
		if(this.x1 < this.x0 || this.y1 < this.y0) {
			this.x1 = this.x0;
			this.y1 = this.y0;
		}

	}

	public boolean isInside(int x, int y) {
		return x >= this.x0 && y >= this.y0 && x < this.x1 && y < this.y1;
	}

	public int getX() {
		return this.x0;
	}

	public int getY() {
		return this.y0;
	}

	public int getRight() {
		return this.x1;
	}

	public int getBottom() {
		return this.y1;
	}

	public int getWidth() {
		return this.x1 - this.x0;
	}

	public int getHeight() {
		return this.y1 - this.y0;
	}

	public int getCenterX() {
		return (this.x0 + this.x1) / 2;
	}

	public int getCenterY() {
		return (this.y0 + this.y1) / 2;
	}

	public Dimension getSize() {
		return new Dimension(this.getWidth(), this.getHeight());
	}

	public boolean isEmpty() {
		return this.x1 <= this.x0 || this.y1 <= this.y0;
	}

	public String toString() {
		return "Rect[x0=" + this.x0 + ", y0=" + this.y0 + ", x1=" + this.x1 + ", y1=" + this.y1 + ']';
	}
}
