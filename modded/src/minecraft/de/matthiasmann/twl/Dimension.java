package de.matthiasmann.twl;

public class Dimension {
	public static final Dimension ZERO = new Dimension(0, 0);
	private final int x;
	private final int y;

	public Dimension(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public boolean equals(Object obj) {
		if(obj != null && this.getClass() == obj.getClass()) {
			Dimension other = (Dimension)obj;
			return this.x == other.x && this.y == other.y;
		} else {
			return false;
		}
	}

	public int hashCode() {
		byte hash = 3;
		int hash1 = 71 * hash + this.x;
		hash1 = 71 * hash1 + this.y;
		return hash1;
	}

	public String toString() {
		return "Dimension[x=" + this.x + ", y=" + this.y + "]";
	}
}
