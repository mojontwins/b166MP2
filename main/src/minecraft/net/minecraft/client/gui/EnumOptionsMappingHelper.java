package net.minecraft.client.gui;

class EnumOptionsMappingHelper {
	static final int[] enumOptionsMappingHelperArray = new int[EnumOptions.values().length];

	static {
		try {
			enumOptionsMappingHelperArray[EnumOptions.INVERT_MOUSE.ordinal()] = 1;
		} catch (NoSuchFieldError noSuchFieldError6) {
		}

		try {
			enumOptionsMappingHelperArray[EnumOptions.VIEW_BOBBING.ordinal()] = 2;
		} catch (NoSuchFieldError noSuchFieldError5) {
		}

		try {
			enumOptionsMappingHelperArray[EnumOptions.ADVANCED_OPENGL.ordinal()] = 4;
		} catch (NoSuchFieldError noSuchFieldError3) {
		}

		try {
			enumOptionsMappingHelperArray[EnumOptions.AMBIENT_OCCLUSION.ordinal()] = 5;
		} catch (NoSuchFieldError noSuchFieldError2) {
		}

		try {
			enumOptionsMappingHelperArray[EnumOptions.RENDER_CLOUDS.ordinal()] = 6;
		} catch (NoSuchFieldError noSuchFieldError1) {
		}
		
		try {
			enumOptionsMappingHelperArray[EnumOptions.COLORED_FOG.ordinal()] = 7;
		} catch (NoSuchFieldError noSuchFieldError1) {
		}

	}
}
