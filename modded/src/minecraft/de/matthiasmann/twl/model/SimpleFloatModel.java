package de.matthiasmann.twl.model;

public class SimpleFloatModel extends AbstractFloatModel {
	private final float minValue;
	private final float maxValue;
	private float value;

	public SimpleFloatModel(float minValue, float maxValue, float value) {
		if(Float.isNaN(minValue)) {
			throw new IllegalArgumentException("minValue is NaN");
		} else if(Float.isNaN(maxValue)) {
			throw new IllegalArgumentException("maxValue is NaN");
		} else if(minValue > maxValue) {
			throw new IllegalArgumentException("minValue > maxValue");
		} else {
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.value = this.limit(value);
		}
	}

	public float getMaxValue() {
		return this.maxValue;
	}

	public float getMinValue() {
		return this.minValue;
	}

	public float getValue() {
		return this.value;
	}

	public void setValue(float value) {
		value = this.limit(value);
		if(this.value != value) {
			this.value = value;
			this.doCallback();
		}

	}

	protected float limit(float value) {
		return Float.isNaN(value) ? this.minValue : Math.max(this.minValue, Math.min(this.maxValue, value));
	}
}
