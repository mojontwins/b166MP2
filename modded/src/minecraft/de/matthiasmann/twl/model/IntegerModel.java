package de.matthiasmann.twl.model;

public interface IntegerModel {
	int getValue();

	int getMinValue();

	int getMaxValue();

	void setValue(int i1);

	void addCallback(Runnable runnable1);

	void removeCallback(Runnable runnable1);
}
