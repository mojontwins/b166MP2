package net.minecraft.src;

import java.util.Random;

public class MathHelper {
	private static float[] SIN_TABLE = new float[65536];

	public static final float sin(float a) {
		return SIN_TABLE[(int) (a * 10430.378F) & 65535];
	}

	public static final float cos(float a) {
		return SIN_TABLE[(int) (a * 10430.378F + 16384.0F) & 65535];
	}

	public static final float sqrt_float(float v) {
		return (float) Math.sqrt((double) v);
	}

	public static final float sqrt_double(double v) {
		return (float) Math.sqrt(v);
	}

	public static int floor_float(float v) {
		int f = (int) v;
		return v < (float) f ? f - 1 : f;
	}

	public static int func_40346_b(double v) {
		return (int) (v + 1024.0D) - 1024;
	}

	public static int floor_double(double v) {
		int f = (int) v;
		return v < (double) f ? f - 1 : f;
	}

	public static long floor_double_long(double v) {
		long f = (long) v;
		return v < (double) f ? f - 1L : f;
	}

	public static float abs(float v) {
		return v >= 0.0F ? v : -v;
	}

	public static int abs(int v) {
		return v >= 0 ? v : -v;
	}

	public static int clamp_int(int v, int a, int b) {
		return v < a ? a : (v > b ? b : v);
	}

	public static float clamp_float(float v, float a, float b) {
		return v < a ? a : (v > b ? b : v);
	}
	
	public static float lerp(float min, float max, float progress) {
		return min + (max - min) * progress;
	}

	public static double lerp(double min, double max, double progress) {
		return min + (max - min) * progress;
	}

	public static double abs_max(double a, double b) {
		if (a < 0.0D) {
			a = -a;
		}

		if (b < 0.0D) {
			b = -b;
		}

		return a > b ? a : b;
	}

	public static int bucketInt(int a, int b) {
		return a < 0 ? -((-a - 1) / b) - 1 : a / b;
	}

	public static boolean stringNullOrLengthZero(String s) {
		return s == null || s.length() == 0;
	}

	public static int getRandomIntegerInRange(Random rand, int a, int b) {
		return a >= b ? a : rand.nextInt(b - a + 1) + a;
	}

	public static double average(long[] arr) {
		long var1 = 0L;
		long[] var3 = arr;
		int var4 = arr.length;

		for (int var5 = 0; var5 < var4; ++var5) {
			long var6 = var3[var5];
			var1 += var6;
		}

		return (double) var1 / (double) arr.length;
	}

	public static float wrapAngleTo180_float(float a) {
		a %= 360.0F;

		if (a >= 180.0F) {
			a -= 360.0F;
		}

		if (a < -180.0F) {
			a += 360.0F;
		}

		return a;
	}

	public static double wrapAngleTo180_double(double a) {
		a %= 360.0D;

		if (a >= 180.0D) {
			a -= 360.0D;
		}

		if (a < -180.0D) {
			a += 360.0D;
		}

		return a;
	}

	public static double denormalizeClamp(double a, double b, double c) {
		return c < 0.0D ? a : (c > 1.0D ? b : a + (b - a) * c);
	}

	public static int ceiling_float_int(float v) {
		int c = (int) v;
		return v > (float) c ? c + 1 : c;
	}

	public static int ceiling_double_int(double v) {
		int c = (int) v;
		return v > (double) c ? c + 1 : c;
	}

	public static float wrapDegrees(float value) {
		value = value % 360.0F;

		if (value >= 180.0F) {
			value -= 360.0F;
		}

		if (value < -180.0F) {
			value += 360.0F;
		}

		return value;
	}

	public static int abs_int(int i) {
		return i >= 0 ? i : -i;
	}

	public static int floor(double value) {
		int i = (int) value;
		return value < (double) i ? i - 1 : i;
	}
	
	static {
		// Sin LUT
		
		for (int i0 = 0; i0 < 65536; ++i0) {
			SIN_TABLE[i0] = (float) Math.sin((double) i0 * Math.PI * 2.0D / 65536.0D);
		}

	}	
}
