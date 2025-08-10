package de.matthiasmann.twl;

public class Border {
	public static final Border ZERO = new Border(0);
	private final int top;
	private final int left;
	private final int bottom;
	private final int right;

	public Border(int all) {
		this.top = all;
		this.left = all;
		this.bottom = all;
		this.right = all;
	}

	public Border(int horz, int vert) {
		this.top = vert;
		this.left = horz;
		this.bottom = vert;
		this.right = horz;
	}

	public Border(int top, int left, int bottom, int right) {
		this.top = top;
		this.left = left;
		this.bottom = bottom;
		this.right = right;
	}

	public int getBorderBottom() {
		return this.bottom;
	}

	public int getBorderLeft() {
		return this.left;
	}

	public int getBorderRight() {
		return this.right;
	}

	public int getBorderTop() {
		return this.top;
	}

	public String toString() {
		return "[Border top=" + this.top + " left=" + this.left + " bottom=" + this.bottom + " right=" + this.right + "]";
	}
}
