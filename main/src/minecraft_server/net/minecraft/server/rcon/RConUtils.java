package net.minecraft.server.rcon;

public class RConUtils {
	public static char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	public static String getBytesAsString(byte[] b0, int i1, int i2) {
		int i3 = i2 - 1;

		int i4;
		for(i4 = i1 > i3 ? i3 : i1; 0 != b0[i4] && i4 < i3; ++i4) {
		}

		return new String(b0, i1, i4 - i1);
	}

	public static int getRemainingBytesAsLEInt(byte[] b0, int i1) {
		return getBytesAsLEInt(b0, i1, b0.length);
	}

	public static int getBytesAsLEInt(byte[] b0, int i1, int i2) {
		return 0 > i2 - i1 - 4 ? 0 : b0[i1 + 3] << 24 | (b0[i1 + 2] & 255) << 16 | (b0[i1 + 1] & 255) << 8 | b0[i1] & 255;
	}

	public static int getBytesAsBEint(byte[] b0, int i1, int i2) {
		return 0 > i2 - i1 - 4 ? 0 : b0[i1] << 24 | (b0[i1 + 1] & 255) << 16 | (b0[i1 + 2] & 255) << 8 | b0[i1 + 3] & 255;
	}

	public static String getByteAsHexString(byte b0) {
		return "" + hexDigits[(b0 & 240) >>> 4] + hexDigits[b0 & 15];
	}
}
