package de.matthiasmann.twl.model;

public class SimpleEnumModel extends AbstractEnumModel {
	private Enum value;

	public SimpleEnumModel(Class clazz, Enum value) {
		super(clazz);
		if(value == null) {
			throw new NullPointerException("value");
		} else if(!clazz.isInstance(value)) {
			throw new IllegalArgumentException("value");
		} else {
			this.value = value;
		}
	}

	public Enum getValue() {
		return this.value;
	}

	public void setValue(Enum value) {
		if(!this.getEnumClass().isInstance(value)) {
			throw new IllegalArgumentException("value");
		} else {
			if(this.value != value) {
				this.value = value;
				this.doCallback();
			}

		}
	}
}
