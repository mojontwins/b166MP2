package net.minecraft.world.level.tile;

public class StepSound {
	public final String stepSoundName;
	public final float stepSoundVolume;
	public final float stepSoundPitch;

	public StepSound(String string1, float f2, float f3) {
		this.stepSoundName = string1;
		this.stepSoundVolume = f2;
		this.stepSoundPitch = f3;
	}

	public float getVolume() {
		return this.stepSoundVolume;
	}

	public float getPitch() {
		return this.stepSoundPitch;
	}

	public String getBreakSound() {
		return "step." + this.stepSoundName;
	}

	public String getStepSound() {
		return "step." + this.stepSoundName;
	}
}
