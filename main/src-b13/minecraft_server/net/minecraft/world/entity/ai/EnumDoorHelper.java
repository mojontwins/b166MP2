package net.minecraft.world.entity.ai;

class EnumDoorHelper {
	static final int[] doorEnum = new int[EnumDoor.values().length];

	static {
		try {
			doorEnum[EnumDoor.OPENING.ordinal()] = 1;
		} catch (NoSuchFieldError noSuchFieldError4) {
		}

		try {
			doorEnum[EnumDoor.WOOD_DOOR.ordinal()] = 2;
		} catch (NoSuchFieldError noSuchFieldError3) {
		}

		try {
			doorEnum[EnumDoor.GRATES.ordinal()] = 3;
		} catch (NoSuchFieldError noSuchFieldError2) {
		}

		try {
			doorEnum[EnumDoor.IRON_DOOR.ordinal()] = 4;
		} catch (NoSuchFieldError noSuchFieldError1) {
		}

	}
}
