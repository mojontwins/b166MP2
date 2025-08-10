package de.matthiasmann.twl.model;

import java.util.ArrayList;
import java.util.Arrays;

public class SimplePropertyList extends AbstractProperty implements PropertyList {
	private final String name;
	private final ArrayList properties;

	public SimplePropertyList(String name) {
		this.name = name;
		this.properties = new ArrayList();
	}

	public SimplePropertyList(String name, Property... properties) {
		this(name);
		this.properties.addAll(Arrays.asList(properties));
	}

	public String getName() {
		return this.name;
	}

	public boolean isReadOnly() {
		return true;
	}

	public boolean canBeNull() {
		return false;
	}

	public PropertyList getPropertyValue() {
		return this;
	}

	public void setPropertyValue(PropertyList value) throws IllegalArgumentException {
		throw new UnsupportedOperationException("Not supported");
	}

	public Class getType() {
		return PropertyList.class;
	}

	public int getNumProperties() {
		return this.properties.size();
	}

	public Property getProperty(int idx) {
		return (Property)this.properties.get(idx);
	}

	public void addProperty(Property property) {
		this.properties.add(property);
		this.fireValueChangedCallback();
	}

	public void addProperty(int idx, Property property) {
		this.properties.add(idx, property);
		this.fireValueChangedCallback();
	}

	public void removeProperty(int idx) {
		this.properties.remove(idx);
		this.fireValueChangedCallback();
	}

	public void removeAllProperties() {
		this.properties.clear();
		this.fireValueChangedCallback();
	}

	public void setPropertyValue(Object object1) throws IllegalArgumentException {
		this.setPropertyValue((PropertyList)object1);
	}

	public Object getPropertyValue() {
		return this.getPropertyValue();
	}
}
