package net.minecraft.client;

public class EnumOSMappingHelper {
	public static final int[] enumOSMappingArray = new int[EnumOS2.values().length];

	static {
		try {
			enumOSMappingArray[EnumOS2.linux.ordinal()] = 1;
		} catch (NoSuchFieldError noSuchFieldError4) {
		}

		try {
			enumOSMappingArray[EnumOS2.solaris.ordinal()] = 2;
		} catch (NoSuchFieldError noSuchFieldError3) {
		}

		try {
			enumOSMappingArray[EnumOS2.windows.ordinal()] = 3;
		} catch (NoSuchFieldError noSuchFieldError2) {
		}

		try {
			enumOSMappingArray[EnumOS2.macos.ordinal()] = 4;
		} catch (NoSuchFieldError noSuchFieldError1) {
		}

	}
}
