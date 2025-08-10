package de.matthiasmann.twl.model;

public interface FloatModel {
	float getValue();

	float getMinValue();

	float getMaxValue();

	void setValue(float f1);

	void addCallback(Runnable runnable1);

	void removeCallback(Runnable runnable1);
}
