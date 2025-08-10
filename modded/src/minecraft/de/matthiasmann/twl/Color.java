package de.matthiasmann.twl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Locale;

public final class Color {
	public static final Color BLACK = new Color(0xFF000000);
	public static final Color SILVER = new Color(-4144960);
	public static final Color GRAY = new Color(-8355712);
	public static final Color WHITE = new Color(-1);
	public static final Color MAROON = new Color(-8388608);
	public static final Color RED = new Color(-65536);
	public static final Color PURPLE = new Color(-8388480);
	public static final Color FUCHSIA = new Color(-65281);
	public static final Color GREEN = new Color(-16744448);
	public static final Color LIME = new Color(-16711936);
	public static final Color OLIVE = new Color(-8355840);
	public static final Color ORANGE = new Color(-23296);
	public static final Color YELLOW = new Color(-256);
	public static final Color NAVY = new Color(-16777088);
	public static final Color BLUE = new Color(-16776961);
	public static final Color TEAL = new Color(-16744320);
	public static final Color AQUA = new Color(-16711681);
	private final byte r;
	private final byte g;
	private final byte b;
	private final byte a;

	public Color(byte r, byte g, byte b, byte a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}

	public Color(int argb) {
		this.a = (byte)(argb >> 24);
		this.r = (byte)(argb >> 16);
		this.g = (byte)(argb >> 8);
		this.b = (byte)argb;
	}

	public int toARGB() {
		return (this.a & 255) << 24 | (this.r & 255) << 16 | (this.g & 255) << 8 | this.b & 255;
	}

	public byte getR() {
		return this.r;
	}

	public byte getG() {
		return this.g;
	}

	public byte getB() {
		return this.b;
	}

	public byte getA() {
		return this.a;
	}

	public float getRedFloat() {
		return (float)(this.r & 255) / 255.0F;
	}

	public float getGreenFloat() {
		return (float)(this.g & 255) / 255.0F;
	}

	public float getBlueFloat() {
		return (float)(this.b & 255) / 255.0F;
	}

	public float getAlphaFloat() {
		return (float)(this.a & 255) / 255.0F;
	}

	public void getFloats(float[] dst, int off) {
		dst[off + 0] = this.getRedFloat();
		dst[off + 1] = this.getGreenFloat();
		dst[off + 2] = this.getBlueFloat();
		dst[off + 3] = this.getAlphaFloat();
	}

	public static Color getColorByName(String name) {
		name = name.toUpperCase(Locale.ENGLISH);

		try {
			Field f = Color.class.getField(name);
			if(Modifier.isStatic(f.getModifiers()) && f.getType() == Color.class) {
				return (Color)f.get((Object)null);
			}
		} catch (Throwable throwable2) {
		}

		return null;
	}

	public static Color parserColor(String value) throws NumberFormatException {
		if(value.length() > 0 && value.charAt(0) == 35) {
			String hexcode = value.substring(1);
			int rgb4;
			int a;
			int r;
			int g;
			switch(value.length()) {
			case 4:
				rgb4 = Integer.parseInt(hexcode, 16);
				a = (rgb4 >> 8 & 15) * 17;
				r = (rgb4 >> 4 & 15) * 17;
				g = (rgb4 & 15) * 17;
				return new Color(0xFF000000 | a << 16 | r << 8 | g);
			case 5:
				rgb4 = Integer.parseInt(hexcode, 16);
				a = (rgb4 >> 12 & 15) * 17;
				r = (rgb4 >> 8 & 15) * 17;
				g = (rgb4 >> 4 & 15) * 17;
				int b = (rgb4 & 15) * 17;
				return new Color(a << 24 | r << 16 | g << 8 | b);
			case 6:
			case 8:
			default:
				throw new NumberFormatException("Can\'t parse \'" + value + "\' as hex color");
			case 7:
				return new Color(0xFF000000 | Integer.parseInt(hexcode, 16));
			case 9:
				return new Color((int)Long.parseLong(hexcode, 16));
			}
		} else {
			return getColorByName(value);
		}
	}

	public String toString() {
		return this.a != -1 ? String.format("#%08X", new Object[]{this.toARGB()}) : String.format("#%06X", new Object[]{this.toARGB() & 0xFFFFFF});
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof Color)) {
			return false;
		} else {
			Color other = (Color)obj;
			return this.toARGB() == other.toARGB();
		}
	}

	public int hashCode() {
		return this.toARGB();
	}

	public Color multiply(Color other) {
		return new Color(this.mul(this.r, other.r), this.mul(this.g, other.g), this.mul(this.b, other.b), this.mul(this.a, other.a));
	}

	private byte mul(byte a, byte b) {
		return (byte)((a & 255) * (b & 255) / 255);
	}
}
