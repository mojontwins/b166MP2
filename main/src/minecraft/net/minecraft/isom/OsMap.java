package net.minecraft.isom;

class OsMap {
	static final int[] osValues = new int[EnumOS1.values().length];

	static {
		try {
			osValues[EnumOS1.linux.ordinal()] = 1;
		} catch (NoSuchFieldError noSuchFieldError4) {
		}

		try {
			osValues[EnumOS1.solaris.ordinal()] = 2;
		} catch (NoSuchFieldError noSuchFieldError3) {
		}

		try {
			osValues[EnumOS1.windows.ordinal()] = 3;
		} catch (NoSuchFieldError noSuchFieldError2) {
		}

		try {
			osValues[EnumOS1.macos.ordinal()] = 4;
		} catch (NoSuchFieldError noSuchFieldError1) {
		}

	}
}
