package net.minecraft.world.level.tile;

final class StepSoundStone extends StepSound {
	StepSoundStone(String string1, float f2, float f3) {
		super(string1, f2, f3);
	}

	public String getBreakSound() {
		return "random.glass";
	}
}
