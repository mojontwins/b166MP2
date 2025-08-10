package de.matthiasmann.twl.model;

public interface ColorSpace {
	String getColorSpaceName();

	int getNumComponents();

	String getComponentName(int i1);

	String getComponentShortName(int i1);

	float getMinValue(int i1);

	float getMaxValue(int i1);

	float getDefaultValue(int i1);

	int toRGB(float[] f1);

	float[] fromRGB(int i1);
}
