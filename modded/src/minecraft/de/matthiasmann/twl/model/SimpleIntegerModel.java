package de.matthiasmann.twl.model;

public class SimpleIntegerModel extends HasCallback implements IntegerModel {
	private final int minValue;
	private final int maxValue;
	private int value;

	public SimpleIntegerModel(int minValue, int maxValue, int value) {
		if(maxValue < minValue) {
			throw new IllegalArgumentException("maxValue < minValue");
		} else {
			this.minValue = minValue;
			this.maxValue = maxValue;
			this.value = value;
		}
	}

	public int getMaxValue() {
		return this.maxValue;
	}

	public int getMinValue() {
		return this.minValue;
	}

	public int getValue() {
		return this.value;
	}

	public void setValue(int value) {
		if(this.value != value) {
			this.value = value;
			this.doCallback();
		}

	}
}
