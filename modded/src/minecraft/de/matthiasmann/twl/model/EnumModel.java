package de.matthiasmann.twl.model;

public interface EnumModel {
	Class getEnumClass();

	Enum getValue();

	void setValue(Enum enum1);

	void addCallback(Runnable runnable1);

	void removeCallback(Runnable runnable1);
}
