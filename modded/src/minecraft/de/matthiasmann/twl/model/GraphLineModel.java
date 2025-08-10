package de.matthiasmann.twl.model;

public interface GraphLineModel {
	String getVisualStyleName();

	int getNumPoints();

	float getPoint(int i1);

	float getMinValue();

	float getMaxValue();
}
