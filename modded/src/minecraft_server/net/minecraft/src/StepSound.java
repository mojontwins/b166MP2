package net.minecraft.src;

public class StepSound {
	public final String field_1029_a;
	public final float field_1028_b;
	public final float field_1030_c;

	public StepSound(String string1, float f2, float f3) {
		this.field_1029_a = string1;
		this.field_1028_b = f2;
		this.field_1030_c = f3;
	}

	public float getVolume() {
		return this.field_1028_b;
	}

	public float getPitch() {
		return this.field_1030_c;
	}

	public String func_737_c() {
		return "step." + this.field_1029_a;
	}
}
