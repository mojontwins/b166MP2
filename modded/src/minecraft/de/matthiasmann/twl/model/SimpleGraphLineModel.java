package de.matthiasmann.twl.model;

public class SimpleGraphLineModel implements GraphLineModel {
	private String visualStyleName;
	private float minValue = 0.0F;
	private float maxValue = 100.0F;
	private float[] data;

	public SimpleGraphLineModel(String style, int size, float minValue, float maxValue) {
		this.setVisualStyleName(style);
		this.data = new float[size];
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	public String getVisualStyleName() {
		return this.visualStyleName;
	}

	public void setVisualStyleName(String visualStyleName) {
		if(visualStyleName.length() < 1) {
			throw new IllegalArgumentException("Invalid style name");
		} else {
			this.visualStyleName = visualStyleName;
		}
	}

	public int getNumPoints() {
		return this.data.length;
	}

	public float getPoint(int idx) {
		return this.data[idx];
	}

	public float getMinValue() {
		return this.minValue;
	}

	public float getMaxValue() {
		return this.maxValue;
	}

	public void addPoint(float value) {
		System.arraycopy(this.data, 1, this.data, 0, this.data.length - 1);
		this.data[this.data.length - 1] = value;
	}

	public void setMaxValue(float maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(float minValue) {
		this.minValue = minValue;
	}

	public void setNumPoints(int numPoints) {
		float[] newData = new float[numPoints];
		int overlap = Math.min(this.data.length, numPoints);
		System.arraycopy(this.data, this.data.length - overlap, newData, numPoints - overlap, overlap);
		this.data = newData;
	}
}
