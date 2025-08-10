package de.matthiasmann.twl.model;

public abstract class AbstractColorSpace implements ColorSpace {
	private final String colorSpaceName;
	private final String[] names;

	public AbstractColorSpace(String colorSpaceName, String... names) {
		this.colorSpaceName = colorSpaceName;
		this.names = names;
	}

	public String getComponentName(int component) {
		return this.names[component];
	}

	public String getColorSpaceName() {
		return this.colorSpaceName;
	}

	public int getNumComponents() {
		return this.names.length;
	}

	public float getMinValue(int component) {
		return 0.0F;
	}
}
