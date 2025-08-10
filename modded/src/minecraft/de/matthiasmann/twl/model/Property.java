package de.matthiasmann.twl.model;

public interface Property {
	String getName();

	boolean isReadOnly();

	boolean canBeNull();

	Object getPropertyValue();

	void setPropertyValue(Object object1) throws IllegalArgumentException;

	Class getType();

	void addValueChangedCallback(Runnable runnable1);

	void removeValueChangedCallback(Runnable runnable1);
}
