package de.matthiasmann.twl.model;

public class SimpleProperty extends AbstractProperty {
	private final Class type;
	private final String name;
	private boolean readOnly;
	private Object value;

	public SimpleProperty(Class type, String name, Object value) {
		this(type, name, value, false);
	}

	public SimpleProperty(Class type, String name, Object value, boolean readOnly) {
		this.type = type;
		this.name = name;
		this.readOnly = readOnly;
		this.value = value;
	}

	public String getName() {
		return this.name;
	}

	public boolean isReadOnly() {
		return this.readOnly;
	}

	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}

	public boolean canBeNull() {
		return false;
	}

	public Object getPropertyValue() {
		return this.value;
	}

	public void setPropertyValue(Object value) throws IllegalArgumentException {
		if(value == null && !this.canBeNull()) {
			throw new NullPointerException("value");
		} else {
			if(this.valueChanged(value)) {
				this.value = value;
				this.fireValueChangedCallback();
			}

		}
	}

	public Class getType() {
		return this.type;
	}

	protected boolean valueChanged(Object newValue) {
		return this.value != newValue && (this.value == null || !this.value.equals(newValue));
	}
}
